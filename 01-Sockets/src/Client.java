import java.io.*;
import java.net.*;

public class Client {
    private static final int PORT = 7777;
    private static final String HOST = "localhost";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader lector;

    public void connecta() throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        lector = new BufferedReader(new InputStreamReader(System.in));
    }

    public void enviaMissatges() throws IOException {
        String missatge;

        //pruebas de que funcione como en el ejemplo
        missatge = "Prova d'enviament 1";
        out.println(missatge);
        System.out.println("Enviat al servidor: " + missatge);
        
        missatge = "Prova d'enviament 2";
        out.println(missatge);
        System.out.println("Enviat al servidor: " + missatge);
        
        //bucle hasta que sea null
        while (missatge != null && !missatge.isEmpty()) {
            System.out.println("Prem enter per tancar el servidor...");
            missatge = lector.readLine();
            out.println(missatge);
            System.out.println("Enviat al servidor: " + missatge);
            missatge = lector.readLine();
        }
    }

    public void tanca() {
        try {
            if (out != null) out.close();
            if (lector != null) lector.close();
            if (socket != null) socket.close();
            System.out.println("Client tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connecta();
        client.enviaMissatges();
        client.tanca();
    }
}
