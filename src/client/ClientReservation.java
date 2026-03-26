package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientReservation {

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 2000;

        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) host = args[1];

        Scanner sc = new Scanner(System.in);
        System.out.print("Votre numero d'abonne : ");
        String numAbonne = sc.nextLine();
        System.out.print("Id du document a reserver : ");
        String idDoc = sc.nextLine();

        System.out.print("Votre email (laisser vide si pas d'alerte) : ");
        String email = sc.nextLine().trim();

        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String requete = numAbonne + " " + idDoc;
        if (!email.isEmpty()) {
            requete = requete + " " + email;
        }
        out.println(requete);

        // on lit toutes les repnses du server jusqua la fin
        String reponse;
        while ((reponse = in.readLine()) != null) {
            System.out.println("[Serveur] " + reponse);
        }

        socket.close();
    }
}