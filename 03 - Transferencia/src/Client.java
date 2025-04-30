import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final String DIR_ARRIBADA = "C:/tmp"; 
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connectat() {
        try {
            System.out.println("Connectant a -> localhost:9999");
            socket = new Socket("localhost", 9999);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connexio acceptada: " + socket.getInetAddress());
        } catch (IOException e) {
            System.out.println("Error connectant al servidor: " + e.getMessage());
        }
    }

    public void rebreFitxer() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();
            if (nomFitxer.equalsIgnoreCase("sortir")) {
                try {
                    out.writeObject("sortir");
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Error enviant 'sortir': " + e.getMessage());
                }
                System.out.println("Sortint...");
                break;
            }

            try {
                out.writeObject(nomFitxer);
                out.flush();

                byte[] contingut = (byte[]) in.readObject();

                if (contingut != null) {
                    String nomArxiu = new File(nomFitxer).getName();
                    File desti = new File(DIR_ARRIBADA, nomArxiu);

                    try (FileOutputStream fos = new FileOutputStream(desti)) {
                        fos.write(contingut);
                        System.out.println("Fitxer rebut i guardat com: " + desti.getAbsolutePath());
                    }
                } else {
                    System.out.println("El fitxer no existeix al servidor.");
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error rebent fitxer: " + e.getMessage());
            }
        }
    }

    public void tancarConnectio() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) {
                socket.close();
                System.out.println("Connexio tancada.");
            }
        } catch (IOException e) {
            System.out.println("Error tancant connexio: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connectat();
        client.rebreFitxer();
        client.tancarConnectio();
    }
}
