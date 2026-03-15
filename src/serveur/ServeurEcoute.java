package serveur;

import metier.Mediatheque;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Thread d'écoute générique.
 * Attend des connexions sur un port donné et délègue le traitement
 * à un Runnable créé par la fabrique fournie.
 *
 * Cela permet d'avoir 3 serveurs indépendants (2000, 2001, 2002)
 * sans dupliquer la boucle d'acceptation.
 */
public class ServeurEcoute extends Thread {

    public interface FabriqueService {
        Runnable creer(Socket socket, Mediatheque mediatheque);
    }

    private final int port;
    private final Mediatheque mediatheque;
    private final FabriqueService fabrique;

    public ServeurEcoute(int port, Mediatheque mediatheque, FabriqueService fabrique) {
        this.port = port;
        this.mediatheque = mediatheque;
        this.fabrique = fabrique;
        setDaemon(true);
        setName("Serveur-port-" + port);
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(port)) {
            System.out.println("[ServeurEcoute] Ecoute sur le port " + port);
            while (true) {
                Socket socket = ss.accept();
                Runnable service = fabrique.creer(socket, mediatheque);
                new Thread(service).start();
            }
        } catch (IOException e) {
            System.err.println("[ServeurEcoute] Port " + port + " : " + e.getMessage());
        }
    }
}
