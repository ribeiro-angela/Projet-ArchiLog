package metier;

public class Livre extends DocumentAbstrait {

    private final int nbPages;

    public Livre(String id, String titre, int nbPages) {
        super(id, titre);
        this.nbPages = nbPages;
    }

    public int getNbPages() { return nbPages; }

    @Override
    public String toString() {
        return super.toString() + " [Livre, " + nbPages + " pages]";
    }
}
