import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) {
        this.nom = nom;
        Contingut();
    }

    public void Contingut() {
        try {
            File file = new File(nom);
            this.contingut = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            System.out.println("Error llegint el fitxer del client: " + e.getMessage());
            this.contingut = null;
        }
    }

    public String getNom() {
        return nom;
    }

    public byte[] getContingut() {
        return contingut;
    }
}