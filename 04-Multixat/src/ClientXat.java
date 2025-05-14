import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientXat extends Thread {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream input;
    private boolean sortir;
    
    
    public ClientXat() {
        this.sortir = false;
    }
    
    public void connecta() {
        try {
            socket = new Socket(HOST, PORT);
            System.out.println("Client connectat a " + HOST + ":" + PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (UnknownHostException e) {
            System.err.println("Error: Host desconegut - " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error en la connexió: " + e.getMessage());
            System.exit(1);
        }
    }
    
    public void enviarMissatge(String missatge) {
        try {
            if (out != null) {
                System.out.println("Enviant missatge: " + missatge);
                out.writeObject(missatge);
                out.flush();
            } else {
                System.out.println("out null. Sortint...");
            }
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
        }
    }
    
    public void tancarClient() {
        System.out.println("Tancant client...");
        try {
            if (input != null) {
                input.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (out != null) {
                out.close();
                System.out.println("Flux de sortida tancat.");
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error tancant el client: " + e.getMessage());
        }
    }
    
    @Override
    public void run() {
        try {
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            
            String missatgeCru;
            while (!sortir) {
                try {
                    missatgeCru = (String) input.readObject();
                    String codi = Missatge.getCodiMissatge(missatgeCru);
                    if (codi == null) continue;
                    
                    String[] parts = Missatge.getPartsMissatge(missatgeCru);
                    
                    switch (codi) {
                        case Missatge.CODI_SORTIR_TOTS:
                            sortir = true;
                            break;
                            
                        case Missatge.CODI_MSG_PERSONAL:
                            if (parts.length >= 3) {
                                String remitent = parts[1];
                                String missatge = parts[2];
                                System.out.println("Missatge de (" + remitent + "): " + missatge);
                            }
                            break;
                            
                        case Missatge.CODI_MSG_GRUP:
                            if (parts.length >= 2) {
                                System.out.println(parts[1]);
                            }
                            break;
                            
                        default:
                            System.err.println("Error: Codi desconegut - " + codi);
                            break;
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("Error en el format del missatge: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            if (!sortir) {
                System.err.println("Error rebent missatge. Sortint...");
            }
        }
    }
    
    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }
    
    public String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        System.out.print(missatge);
        String linea = scanner.nextLine().trim();
        
        while (obligatori && linea.isEmpty()) {
            System.out.println("El camp no pot estar buit. Torna a intentar-ho.");
            System.out.print(missatge);
            linea = scanner.nextLine().trim();
        }
        
        return linea;
    }
    
    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        client.start();
        
        Scanner scanner = new Scanner(System.in);
        client.ajuda();
        
        String linea;
        String missatge;
        
        while (!client.sortir) {
            linea = scanner.nextLine().trim();
            
            if (linea.isEmpty() || linea.equals("4")) {
                client.sortir = true;
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                break;
            }
            
            switch (linea) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                    missatge = Missatge.getMissatgeConectar(nom);
                    client.enviarMissatge(missatge);
                    break;
                    
                case "2":
                    String destinatari = client.getLinea(scanner, "Destinatari: ", true);
                    String contingut = client.getLinea(scanner, "Missatge a enviar: ", true);
                    missatge = Missatge.getMissatgePersonal(destinatari, contingut);
                    client.enviarMissatge(missatge);
                    break;
                    
                case "3":
                    contingut = client.getLinea(scanner, "Missatge a enviar: ", true);
                    missatge = Missatge.getMissatgeGrup(contingut);
                    client.enviarMissatge(missatge);
                    break;
                    
                case "5":
                    missatge = Missatge.getMissatgeSortirTots("Adéu");
                    client.enviarMissatge(missatge);
                    client.sortir = true;
                    break;
                    
                default:
                    System.out.println("Opció no vàlida.");
                    break;
            }
            
            if (!client.sortir) {
                client.ajuda();
            }
        }
        
        scanner.close();
        client.tancarClient();
        System.exit(0);
    }
}