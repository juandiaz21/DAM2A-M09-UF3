import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String MSG_SORTIR = "sortir";
    private ServerSocket serverSocket;

    public void inicialitzarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a localhost:" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    private static class FilServidorXat extends Thread {
        private final ObjectInputStream entrada;
        private final ObjectOutputStream sortida;
        private final String nomClient;

        public FilServidorXat(ObjectInputStream entrada, ObjectOutputStream sortida, String nomClient) {
            this.entrada = entrada;
            this.sortida = sortida;
            this.nomClient = nomClient;
        }

        public void run() {
            try {
                String missatge;
                do {
                    missatge = (String) entrada.readObject();
                    System.out.println("Missatge ('" + MSG_SORTIR + "' per tancar): Rebut: " + missatge);
                } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
                System.out.println("Fil de xat finalitzat.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        try {
            servidor.inicialitzarServidor();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket.getInetAddress());

            ObjectOutputStream sortida = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());

            String nomClient = (String) entrada.readObject();
            System.out.println("Nom rebut: " + nomClient);

            FilServidorXat fil = new FilServidorXat(entrada, sortida, nomClient);
            System.out.println("Fil de xat creat.");
            fil.start();
            System.out.println("Fil de " + nomClient + " iniciat");

            Scanner scanner = new Scanner(System.in);
            String missatge;
            do {
                System.out.print("Missatge ('" + MSG_SORTIR + "' per tancar): ");
                missatge = scanner.nextLine();
                sortida.writeObject(missatge);
                sortida.flush();
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));

            fil.join();
            System.out.println(MSG_SORTIR);
            clientSocket.close();
            servidor.pararServidor();
            System.out.println("Servidor aturat.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}