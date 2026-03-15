package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientEmprunt {

    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 2001;

        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) host = args[1];

        Scanner sc = new Scanner(System.in);
        System.out.print("Votre numero d'abonne : ");
        String numAbonne = sc.nextLine();
        System.out.print("Id du document a emprunter : ");
        String idDoc = sc.nextLine();

        Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.println(numAbonne + " " + idDoc);
        System.out.println("[Serveur] " + in.readLine());

        socket.close();
    }
}
