package metier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mediatheque {

    private HashMap<Integer, Abonne> abonnes = new HashMap<>();
    private HashMap<String, Document> documents = new HashMap<>();

    // pour sitting bull : on stocke les emails des gens qui attendent un doc
    private HashMap<String, List<String>> listeAttente = new HashMap<>();

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

    // inscription a une alerte pour un document
    public void inscrireAlerte(String idDoc, String email) {
        if (!listeAttente.containsKey(idDoc)) {
            listeAttente.put(idDoc, new ArrayList<>());
        }
        listeAttente.get(idDoc).add(email);
        System.out.println("[Alerte] " + email + " sera prevenu au retour de " + idDoc);
    }

    // a appeler quand un document est rendu
    public void notifierAttente(String idDoc) {
        List<String> emails = listeAttente.get(idDoc);
        if (emails == null || emails.isEmpty()) {
            return;
        }

        Document doc = documents.get(idDoc);
        String titre = idDoc; // si on trouve pas le titre on met juste l'id
        if (doc instanceof Livre) {
            titre = ((Livre) doc).getTitre();
        } else if (doc instanceof DVD) {
            titre = ((DVD) doc).getTitre();
        }

        for (String email : emails) {
            AlerteMail.envoyerSignalDeFumee(email, idDoc, titre);
        }

        // on vide la liste apres avoir notifie tout le monde
        listeAttente.remove(idDoc);
    }

    public boolean yaDesGensQuiAttendent(String idDoc) {
        List<String> emails = listeAttente.get(idDoc);
        return emails != null && !emails.isEmpty();
    }
}