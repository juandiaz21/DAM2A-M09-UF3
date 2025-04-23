import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
    private ObjectInputStream entrada;
    private static final String MSG_SORTIR = "sortir";

    public FilServidorXat(ObjectInputStream entrada) {
        this.entrada = entrada;
    }

    public void run() {
        try {
            String missatge;
            do {
                missatge = (String) entrada.readObject();
                System.out.println("Rebut: " + missatge);
            } while (!missatge.equalsIgnoreCase(MSG_SORTIR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}