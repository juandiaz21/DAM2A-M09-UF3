import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private static final String MSG_SORTIR = "sortir";

    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    private Scanner scanner;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat a " + HOST + ":" + PORT);
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");
        scanner = new Scanner(System.in);
    }

    public void enviarMissatge(String missatge) throws IOException {
        sortida.writeObject(missatge);
        sortida.flush();
        System.out.println("Enviant missatge: " + missatge);
    }

    public void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (entrada != null) entrada.close();
            if (sortida != null) sortida.close();
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
            System.out.println("Client tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        try {
            client.connecta();
            
            System.out.println("Missatge ('" + MSG_SORTIR + "' per tancar): Fil de lectura iniciat");
            System.out.print("Rebut: Escriu el teu nom: \n");
            
            String nom = client.scanner.nextLine();
            client.enviarMissatge(nom);

            new Thread(() -> {
                try {
                    String missatge;
                    do {
                        System.out.print("Missatge ('" + MSG_SORTIR + "' per tancar): ");
                        missatge = (String) client.entrada.readObject();
                        System.out.println("Rebut: " + missatge);
                    } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
            
            String missatge;
            do {
                missatge = client.scanner.nextLine();
                client.enviarMissatge(missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
            
            client.tancarClient();
            System.out.println("El servidor ha tancat la connexió.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}