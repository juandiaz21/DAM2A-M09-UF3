import java.io.*;
import java.net.*;

public class Servidor {
    private static final int PORT = 7777;
    private ServerSocket srvSocket;
    private Socket clientSocket;

    public void connecta() throws IOException {
        srvSocket = new ServerSocket(PORT);
        System.out.println("Servidor en marxa a " + PORT);
        System.out.println("Esperant connexions...");

        clientSocket = srvSocket.accept();
        System.out.println("Client connectat: " + clientSocket.getInetAddress().getHostAddress());
    }

    public void repDades() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String missatge;
            while ((missatge = in.readLine()) != null && !missatge.trim().isEmpty()) {
                System.out.println("Rebut: " + missatge);
            }
        }
    }

    public void tanca() throws IOException {
        if (clientSocket != null) clientSocket.close();
        if (srvSocket != null) srvSocket.close();
        System.out.println("Servidor tancat.");
    }

    public static void main(String[] args) {
        try {
            Servidor servidor = new Servidor();
            servidor.connecta();
            servidor.repDades();
            servidor.tanca();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}