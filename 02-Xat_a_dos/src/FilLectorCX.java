import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class FilLectorCX extends Thread {
    private ObjectOutputStream sortida;
    private static final String MSG_SORTIR = "sortir";

    public FilLectorCX(ObjectOutputStream sortida) {
        this.sortida = sortida;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            String missatge;
            do {
                missatge = scanner.nextLine();
                System.out.println("Enviant missatge: " + missatge);
                sortida.writeObject(missatge);
                sortida.flush();
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}