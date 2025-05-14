import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GestorClients extends Thread {
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream input;
    private ServidorXat servidorXat;
    private String nom;
    private boolean sortir;
    
    public GestorClients(Socket client, ServidorXat servidorXat) {
        this.client = client;
        this.servidorXat = servidorXat;
        this.sortir = false;
        
        try {
            this.out = new ObjectOutputStream(client.getOutputStream());
            this.input = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            System.err.println("Error inicialitzant streams: " + e.getMessage());
        }
    }
    
    public String getNom() {
        return nom;
    }
    
    @Override
    public void run() {
        try {
            String missatge;
            while (!sortir) {
                missatge = (String) input.readObject();
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en el gestor de clients: " + e.getMessage());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                System.err.println("Error tancant el socket del client: " + e.getMessage());
            }
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            if (out != null) {
                out.writeObject(missatge);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge a " + nom + ": " + e.getMessage());
        }
    }
    
    public void processaMissatge(String missatgeCru) {
        String codi = Missatge.getCodiMissatge(missatgeCru);
        if (codi == null) return;
        
        String[] parts = Missatge.getPartsMissatge(missatgeCru);
        
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                if (parts.length >= 2) {
                    this.nom = parts[1];
                    servidorXat.afegirClient(this);
                }
                break;
                
            case Missatge.CODI_SORTIR_CLIENT:
                sortir = true;
                servidorXat.eliminarClient(nom);
                break;
                
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidorXat.finalitzarXat();
                break;
                
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String missatge = parts[2];
                    servidorXat.enviarMissatgePersonal(destinatari, nom, missatge);
                }
                break;
                
            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    String missatge = parts[1];
                    servidorXat.enviarMissatgeGrup("(" + nom + "): " + missatge);
                }
                break;
                
            default:
                System.err.println("Error: Codi desconegut - " + codi);
                break;
        }
    }
}