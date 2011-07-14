package bourse.placeDeMarche;

import java.net.ServerSocket;
import java.io.IOException;

/** �coute les demandes de connexion physique des agents et lance les threads qui
 * s'occuperont de chacun d'eux.
 */
public class PdmServeur extends bourse.reseau.Serveur {
    private PlaceDeMarche pdm;
    public PdmServeur(PlaceDeMarche pdm) throws java.net.PortUnreachableException, java.util.MissingResourceException {
        super(pdm.getPort());
        if (pdm != null)
            this.pdm = pdm;
        else
            throw new java.util.MissingResourceException("Le serveur doit absolument conna�tre sa place de march�.", PdmServeur.class.getName(), "pdm");
    }
    public void run() {
        if (pdm.getVerbose()) System.out.println("D�marrage du serveur de la place de march�.");
        while (pdm.getAccepterAgents()) {
            try {
                ConnexionAgent connexionAgent = new ConnexionAgent(this.getSocketServeur().accept(), this.pdm, false);
                connexionAgent.start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}