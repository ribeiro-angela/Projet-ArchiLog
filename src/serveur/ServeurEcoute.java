package serveur;

import metier.Mediatheque;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurEcoute extends Thread {

    private int port;
    private Mediatheque mediatheque;
    private String typeService; // "reservation", "emprunt" ou "retour"

    public ServeurEcoute(int port, Mediatheque mediatheque, String typeService) {
        this.port = port;
        this.mediatheque = mediatheque;
        this.typeService = typeService;
    }

    public void run() {
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("[ServeurEcoute] Ecoute sur le port " + port);

            while (true) {
                Socket socket = ss.accept();

                Runnable service;
                if (typeService.equals("reservation")) {
                    service = new ServiceReservation(socket, mediatheque);
                } else if (typeService.equals("emprunt")) {
                    service = new ServiceEmprunt(socket, mediatheque);
                } else {
                    service = new ServiceRetour(socket, mediatheque);
                }

                new Thread(service).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
