import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connectar() {
        try {
            System.out.println("Acceptant connexions en -> localhost:" + PORT);
            serverSocket = new ServerSocket(PORT);
            System.out.println("Esperant connexio...");
            clientSocket = serverSocket.accept();
            System.out.println("Connexio acceptada: " + clientSocket.getInetAddress());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error en la connexió: " + e.getMessage());
        }
    }

    public void enviarFitxers() {
        try {
            while (true) {
                System.out.println("Esperant el nom del fitxer del client...");
                String nomFitxer = (String) in.readObject();

                if (nomFitxer == null || nomFitxer.equalsIgnoreCase("sortir")) {
                    System.out.println("Nom del fitxer buit o 'sortir'. Sortint...");
                    break;
                }

                System.out.println("Nomfitxer rebut: " + nomFitxer);
                File file = new File(nomFitxer);

                if (!file.exists() || !file.isFile()) {
                    System.out.println("Fitxer no trobat o no vàlid: " + nomFitxer);
                    out.writeObject(null);
                    out.flush();
                    continue;
                }

                // Llegim el fitxer com a array de bytes
                byte[] contingut = llegirFitxer(file);
                if (contingut != null) {
                    System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                    out.writeObject(contingut);
                    out.flush();
                    System.out.println("Fitxer enviat al client: " + nomFitxer);
                } else {
                    System.out.println("Error llegint el fitxer: " + nomFitxer);
                    out.writeObject(null);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en enviarFitxers: " + e.getMessage());
        }
    }

    private byte[] llegirFitxer(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        } catch (IOException e) {
            System.out.println("No s'ha pogut llegir el fitxer: " + e.getMessage());
            return null;
        }
    }

    public void tancarConnexio() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) {
                System.out.println("Tancant connexió amb el client: " + clientSocket.getInetAddress());
                clientSocket.close();
            }
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            System.out.println("Error tancant connexions: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        servidor.connectar();
        servidor.enviarFitxers();
        servidor.tancarConnexio();
    }
}
