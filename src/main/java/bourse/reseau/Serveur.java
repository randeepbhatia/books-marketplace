package bourse.reseau;
import java.net.*;
import java.io.IOException;
/**
 * �coute les demandes de connexion physique au r�seau
 */
public abstract class Serveur extends Thread {
    /** Socket d'�coute du serveur. */
    private ServerSocket socketServeur;
    /** Acc�s au socketServeur. */
    public ServerSocket getSocketServeur() { return this.socketServeur; }
    /** Constructeur de serveur. */
    public Serveur(int port) throws java.net.PortUnreachableException {
        try { socketServeur = new ServerSocket(port); }
        catch (IOException e) {
            throw new PortUnreachableException("Ne peut �couter le port " + port );
        }
    }
}