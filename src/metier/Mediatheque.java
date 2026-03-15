package metier;

import java.util.HashMap;
import java.util.Map;

/**
 * Registre central de la médiathèque.
 * Contient tous les abonnés et tous les documents.
 * C'est la seule classe que les services manipulent.
 */
public class Mediatheque {

    private final Map<Integer, Abonne> abonnes = new HashMap<>();
    private final Map<String, DocumentAbstrait> documents = new HashMap<>();

    public void ajouterAbonne(Abonne ab) {
        abonnes.put(ab.getNumero(), ab);
    }

    public void ajouterDocument(DocumentAbstrait doc) {
        documents.put(doc.idDoc(), doc);
    }

    public Abonne getAbonne(int numero) {
        return abonnes.get(numero);
    }

    public DocumentAbstrait getDocument(String id) {
        return documents.get(id);
    }

    public Map<Integer, Abonne> getAbonnes() { return abonnes; }
    public Map<String, DocumentAbstrait> getDocuments() { return documents; }
}
