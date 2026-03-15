package metier;

import java.util.HashMap;

public class Mediatheque {

    private HashMap<Integer, Abonne> abonnes = new HashMap<>();
    private HashMap<String, Document> documents = new HashMap<>();

    public void ajouterAbonne(Abonne a) {
        abonnes.put(a.getNumero(), a);
    }

    public void ajouterDocument(Document d) {
        documents.put(d.idDoc(), d);
    }

    public Abonne getAbonne(int num) {
        return abonnes.get(num);
    }

    public Document getDocument(String id) {
        return documents.get(id);
    }
}
