package metier;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class AlerteMail {

    private String emailDestinataire;
    private String idDocument;

    public AlerteMail(String emailDestinataire, String idDocument) {
        this.emailDestinataire = emailDestinataire;
        this.idDocument = idDocument;
    }

    public String getEmailDestinataire() {
        return emailDestinataire;
    }

    public String getIdDocument() {
        return idDocument;
    }

    // envoie un mail pour prevenir qu'un document est revenu
    public static void envoyerSignalDeFumee(String destinataire, String idDoc, String titreDoc) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.u-paris.fr");
        props.put("mail.smtp.port", "25");

        Session session = Session.getInstance(props);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("mediatheque@u-paris.fr"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            msg.setSubject("Le document que vous attendiez est disponible !");
            msg.setText("Bonjour,\n\n"
                    + "Le document suivant est maintenant disponible a la mediatheque :\n"
                    + "  Titre : " + titreDoc + "\n"
                    + "  ID    : " + idDoc + "\n\n"
                    + "Venez vite l'emprunter !\n\n"
                    + "La mediatheque");

            Transport.send(msg);
            System.out.println("[Sitting Bull] Signal de fumee envoye a " + destinataire);

        } catch (MessagingException e) {
            // pas de serveur smtp dispo, on simule juste
            System.out.println("[Sitting Bull] (simulation) mail envoye a " + destinataire
                    + " pour le doc " + idDoc);
        }
    }

    // test pour la certification - envoi vers le grand Wakan Tanka
    public static void main(String[] args) {
        System.out.println("Test certification Sitting Bull...");
        envoyerSignalDeFumee("jean-francois.brette@u-paris.fr", "L001", "Le Seigneur des Anneaux");
        System.out.println("Termine.");
    }
}