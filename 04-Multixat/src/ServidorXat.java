import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    
    private Hashtable<String, GestorClients> clients;
    private boolean sortir;
    private ServerSocket serverSocket;
    
    public ServidorXat() {
        this.clients = new Hashtable<>();
        this.sortir = false;
    }
    
    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error iniciant el servidor: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant el servidor: " + e.getMessage());
        }
    }
    
    public void finalitzarXat() {
        enviarMissatgeGrup(MSG_SORTIR);
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        clients.clear();
        sortir = true;
        pararServidor();
        System.exit(0);
    }
    
    public void afegirClient(GestorClients gestorClient) {
        String nom = gestorClient.getNom();
        clients.put(nom, gestorClient);
        System.out.println(nom + " connectat.");
        enviarMissatgeGrup("Entra: " + nom);
    }
    
    public void eliminarClient(String nom) {
        if (nom != null && clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " desconnectat.");
            enviarMissatgeGrup("Ha sortit: " + nom);
        }
    }
    
    public void enviarMissatgeGrup(String missatge) {
        String missatgeCodificat = Missatge.getMissatgeGrup(missatge);
        System.out.println("DEBUG: multicast " + missatge);
        
        for (GestorClients client : clients.values()) {
            client.enviarMissatge(missatgeCodificat);
        }
    }
    
    public void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        if (clients.containsKey(destinatari)) {
            String missatgeCodificat = Missatge.getMissatgePersonal(remitent, missatge);
            clients.get(destinatari).enviarMissatge(missatgeCodificat);
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
        } else {
            if (clients.containsKey(remitent)) {
                String errorMissatge = Missatge.getMissatgePersonal("Servidor", "El destinatari " + destinatari + " no existeix.");
                clients.get(remitent).enviarMissatge(errorMissatge);
            }
        }
    }
    
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
        
        try {
            while (!servidor.sortir) {
                Socket clientSocket = servidor.serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getInetAddress());
                
                GestorClients gestorClient = new GestorClients(clientSocket, servidor);
                gestorClient.start();
            }
        } catch (IOException e) {
            if (!servidor.sortir) {
                System.err.println("Error acceptant connexions: " + e.getMessage());
            }
        } finally {
            servidor.pararServidor();
        }
    }
}