package bourse.placeDeMarche;

import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import bourse.protocole.*;
import bourse.sdd.*;
import bourse.placeDeMarche.enchere.*;

/** G�re une connexion de la place de march� vers l'agent. */
public class ConnexionAgent extends bourse.reseau.ManagerConnexion {
    
    private Agent agent;
    private PlaceDeMarche pdm;
    private int port;
    
    public Agent getAgent() { return this.agent; }
    public int getPort() { return this.port; }
    public ConnexionAgent(Socket socket, PlaceDeMarche pdm, boolean verbose) throws IOException, java.util.MissingResourceException {
        super(socket, Protocole.MOTIF_FIN_FICHIER_XML, verbose);
        if (pdm != null)
            this.pdm = pdm;
        else
            throw new java.util.MissingResourceException("Le thread de connexion � un agent doit absolument conna�tre sa place de march�.", this.getClass().getName(), "pdm");
        this.agent = new Agent(socket.getPort());
        this.port = socket.getPort();
    }
    public void run() {
        if (pdm.getVerbose()) System.out.println("D�marrage d'une connexion vers l'agent localis� � " + this.getHostAddress() + ":" + agent.getPort());
        super.run(); // Commence � �couter les transmissions.
    }
    public String toString() { return this.agent.getNomAgent(); }
    public String toHtml() { return agent.toHtml(); }
    protected void traiter(String message) {
        if (message == null) { // La connexion a �t� coup�e par le destinataire.
            if (pdm.getSalleDesVentes().estIdentifie(this.agent))
                pdm.getSalleDesVentes().supprimerAgent(this);
            this.agent = null;
            this.pdm = null;
        } else {
            Protocole msg = Protocole.newInstance(message);
            if (pdm.getVerbose()) System.out.println(" IN " + msg.getType().toString());
            Protocole reponse = null;
            Protocole reponse2 = null;
            switch (msg.getType().getValue()) {
                case TypeMessage.TM_WELCOME :
                    // Instancie le bon type de message.
                    Welcome m = (Welcome)msg;
                    // Cr�e un agent contenant le nom venant du message et le port venant de la connexion
                    bourse.placeDeMarche.Agent nouvelAgent = new bourse.placeDeMarche.Agent(port);
                    nouvelAgent.setNom(m.getNom());
                    if (pdm.getSalleDesVentes().estIdentifie(nouvelAgent.getNomAgent())) { // La salle des ventes contient un agent du m�me nom : l'agent envoie un WELCOME �trange...
                        if (pdm.getSalleDesVentes().estIdentifie(nouvelAgent)) { // L'agent a renvoy� un WELCOME alors qu'il �tait d�j� identifi�.
                            reponse = new ResultWelcome(agent.getArgent(), agent.getCategorie());
                        } else { // L'agent a envoy� un WELCOME sur une nouvelle connexion alors qu'il �tait toujours connu de la place de march�.
                            // On supprime la pr�c�dente connexion
                            ConnexionAgent ancienAgent = pdm.getSalleDesVentes().getConnexionAgent(nouvelAgent.getNomAgent());
                            agent = new bourse.placeDeMarche.Agent(ancienAgent.getAgent());
                            agent.setPort(this.port);
                            pdm.getSalleDesVentes().identifierAgent(this);
                            ancienAgent.deconnecter();
                            reponse = new ResultWelcome(agent.getArgent(), agent.getCategorie());
                        }
                    } else { // L'agent a envoy� un WELCOME et la place de march� ne conna�t pas ce nom d'agent.
                        // V�rification de l'existence de l'agent dans la base de donn�es.
                        if (pdm.getRequetes().agentPresentDansBaseDeDonnees(agent)) { // L'agent est pr�sent dans la base de donn�es et ses informations sont actualis�es.
                            // V�rification de la non-r�plication de l'agent sur d'autres places de march�.
                            if (agent.getNomPDM().equalsIgnoreCase("HOME")) { // L'agent n'est connect� � aucune place de march�, on peut l'accepter.
                                agent.setNomPDM(pdm.getNom());
                                pdm.getRequetes().inscrireAgent(agent);
                                pdm.getSalleDesVentes().identifierAgent(this);
                                reponse = new ResultWelcome(agent.getArgent(), agent.getCategorie());
                                if (pdm.getCommissairePriseur().getEnchereCourante() != null) // Le commissaire priseur a lanc� une ench�re, nous allons donc lui r�annoncer l'ench�re.
                                    reponse2 = pdm.getCommissairePriseur().getEnchereCourante().reAnnonce();
                            } else // L'agent est d�j� connect� sur une autre place de march�
                                reponse = new Erreur("Duplication", "D�sol�, vous �tes d�j� connect� sur une autre place de march�.", this.agent.getNomPDM(), pdm.getRequetes().getAdressePdm(this.agent.getNomPDM()));
                        } else { // C'est visiblement la premi�re fois que l'agent se connecte puisqu'il n'est pas pr�sent dans la base de donn�es.
                            agent.setNom(m.getNom());
                            agent.setNomPDM(pdm.getNom());
                            agent.setCategorie(new Categorie());
                            agent.setArgent(pdm.getRequetes().soldeDeDepart());
                            pdm.getRequetes().inscrireAgent(agent);
                            pdm.getSalleDesVentes().identifierAgent(this);
                            reponse = new ResultWelcome(agent.getArgent(), agent.getCategorie());
                            if (pdm.getCommissairePriseur().getEnchereCourante() != null) // Le commissaire priseur a lanc� une ench�re, nous allons donc lui r�annoncer l'ench�re.
                                reponse2 = pdm.getCommissairePriseur().getEnchereCourante().reAnnonce();
                        }
                    }
                    break;
                case TypeMessage.TM_BYE :
                    if (agent.getBloque()) // L'agent est bloqu� par la place de march� : il ne peut pas sortir.
                        reponse = new Erreur("Bloque", "Vous �tes bloqu� chez moi, niark niark !");
                    else { // On autorise l'agent � partir.
                        pdm.getRequetes().desinscrireAgent(this.getAgent());
                        pdm.getSalleDesVentes().supprimerAgent(this);
                        reponse = new ResultBye(this.pdm.getRequetes().getAdressesPdm());
                    }
                    break;
                case TypeMessage.TM_REQUETE_PROGRAMME :
                    reponse = new Programme(this.pdm.getCommissairePriseur().getProgramme().getListeProgramme());
                    break;
                case TypeMessage.TM_PROPOSE_VENTE :
                    ProposeVente proposeVente = (ProposeVente)msg;
                    Livre livreAgent = pdm.getRequetes().getLivre(proposeVente.getId());
                    if (livreAgent != null) // L'item correspond � un livre.
                        if ((proposeVente.getPrix() > 0.0 && (proposeVente.getNom() == 3 || proposeVente.getNom() == 4)) && livreAgent.getProprietaire().equalsIgnoreCase(agent.getNomAgent())) // La mise � prix n'est pas nulle
                            if (pdm.getCommissairePriseur().getEnchereCourante().getLivre().getId() != proposeVente.getId() && pdm.getCommissairePriseur().getProgramme().insertionPossible(pdm.getNom(), proposeVente.getId())) {
                                reponse = new ResultProposeVente(proposeVente.getId());
                                pdm.getRequetes().supprimerDerniereEnchere(((ProgrammePro)pdm.getCommissairePriseur().getProgramme().getListeProgramme().getLast()).getLivre());
                                pdm.getCommissairePriseur().getProgramme().ajouterVente(livreAgent, pdm.getNom(), proposeVente.getNom(), proposeVente.getPrix());
                            } else
                                reponse = new Erreur("Zerovente", "D�sol� mais il n'y a vraiment plus de place dans le programme (pourtant, notre pdm g�re l'insertion d'ench�re sur tout le programme !).", "autreVendeur");
                        else
                            reponse = new Erreur("Zerovente", "Vous avez propos� un prix n�gatif ou le livre ne vous appartient pas petit coquin.", "nonValide");
                    else
                        reponse = new Erreur("Zerovente", "L'id du livre est incorrect : tu pourrais faire plus attention !", "nonValide");
                    break;
                case TypeMessage.TM_PROPOSITION_ENCHERE_A : if (pdm.getCommissairePriseur().getEnchereCourante() != null) {// Si non, l'agent a envoy� trop tard sa proposition...
                    int typeEnchereCourante = pdm.getCommissairePriseur().getEnchereCourante().getTypeEnchere();
                    Enchere enchereCourante = pdm.getCommissairePriseur().getEnchereCourante();
                    switch (typeEnchereCourante) {
                        case Enchere.ENCHERE_TROIS :
                            if (((PropositionEnchereA)msg).getNumero() == enchereCourante.getNumEnchere() // Le num�ro de l'ench�re est correct
                                 && ((PropositionEnchereA)msg).getEnchere() <= this.agent.getArgent()     // L'agent a assez d'argent pour ench�rir
                                 && ((EnchereReponseMultiple)enchereCourante).getPrixCourant()+enchereCourante.getPas()<=((PropositionEnchereA)msg).getEnchere() // L'agent a propos� suffisamment
                                 && !enchereCourante.getVendeur().equals(this.agent.getNomAgent())        // L'agent n'est pas le vendeur
                            ) {
                                // On a besoin de r�veiller le commissaire priseur.
                                pdm.getCommissairePriseur().vousAvezUnMessage((PropositionEnchereA)msg,this);
                                agent.setBloque(true);
                            }
                            break;
                        case Enchere.ENCHERE_QUATRE :
                            if (((PropositionEnchereA)msg).getNumero() == enchereCourante.getNumEnchere() // Le num�ro de l'ench�re est correct
                                 && ((EnchereReponseBoucle)enchereCourante).getPrixCourant() <= this.agent.getArgent() // L'agent a assez d'argent pour acqu�rir le livre.
                                 && !enchereCourante.getVendeur().equals(this.agent.getNomAgent())        // L'agent n'est pas le vendeur
                            ) {
                                // On a besoin de r�veiller le commissaire priseur.
                                pdm.getCommissairePriseur().vousAvezUnMessage((PropositionEnchereA)msg,this);
                                agent.setBloque(true);
                            }
                            break;
                        case Enchere.ENCHERE_UN :
                            if (((PropositionEnchereA)msg).getNumero() == enchereCourante.getNumEnchere() // Le num�ro de l'ench�re est correct
                                 && enchereCourante.getPrixCourant() <= this.agent.getArgent()            // L'agent a assez d'argent pour ench�rir
                                 && !enchereCourante.getVendeur().equals(this.agent.getNomAgent())        // L'agent n'est pas le vendeur
                            ) {
                                ((EnchereReponseUnique)(pdm.getCommissairePriseur().getEnchereCourante())).actualiser((PropositionEnchereA)msg, agent.getNomAgent());
                                agent.setBloque(true);
                            }
                        default : // Il reste le type Vickrey et Plis scell�
                            if (((PropositionEnchereA)msg).getNumero() == enchereCourante.getNumEnchere() // Le num�ro de l'ench�re est correct
                                 && ((PropositionEnchereA)msg).getEnchere() <= this.agent.getArgent()      // L'agent a assez d'argent pour ench�rir
                                 && ((PropositionEnchereA)msg).getEnchere() > 0                           // L'agent a propos� un montant coh�rent
                                 && !enchereCourante.getVendeur().equals(this.agent.getNomAgent())        // L'agent n'est pas le vendeur
                            ) {

                            ((EnchereReponseUnique)(pdm.getCommissairePriseur().getEnchereCourante())).actualiser((PropositionEnchereA)msg, agent.getNomAgent());
                            agent.setBloque(true);
                            }
                    }
                }
                break;
                case TypeMessage.TM_REQUETE_AGENTS :
                    reponse = new ResultAgents(pdm.getSalleDesVentes().agentsToListeDeNoms());
                    break;
                case TypeMessage.TM_ADMIN :
                    switch (((Admin)msg).getTypeRequete()) {
                        case Admin.ADMIN_DOC :
                            try {
                                ecrire(pdm.toHtml());
                            } catch (java.io.IOException e) { e.printStackTrace(System.err); }
                            break;
                        case Admin.ADMIN_TERMINER :
                            this.pdm.terminer();
                    }
                    deconnecter();
                    break;
            }
            if (reponse != null) { // Le message re�u n�cessite une r�ponse imm�diate de la pdm (car il n'est pas vide).
                String xml = reponse.toXML(reponse.toDOM());
                try {
                    this.ecrire(xml);
                    if (pdm.getVerbose()) System.out.println("OUT " + reponse.getType().toString());
                } catch (java.io.IOException e) { if (pdm.getVerbose()) System.err.println(e); }
                if (reponse2 != null) { // Le message re�u n�cessite une seconde r�ponse imm�diate (typiquement un broadcast d'ench�re courante).
                    xml = reponse2.toXML(reponse2.toDOM());
                    try {
                        this.ecrire(xml);
                        if (pdm.getVerbose()) System.out.println("OUT " + reponse2.getType().toString());
                    } catch (java.io.IOException e) { if (pdm.getVerbose()) System.err.println(e); }
                }
            }
            if (!pdm.getSalleDesVentes().estIdentifie(agent.getNomAgent())) // L'agent n'est vraiment plus pr�sent dans la salle des ventes.
                pdm.getRequetes().desinscrireAgent(agent);
        }
    }
    public boolean possede(Livre livre) { return this.pdm.getRequetes().agentPossede(this.getAgent(), livre); }
    /** Appel� par ThreadLecture lorsque la connexion est termin�e. */
    public void deconnecter() {
        System.out.println("Fermeture de la connexion vers l'agent localis� � " + this.getHostAddress());
        super.deconnecter();
        pdm.getSalleDesVentes().supprimerAgent(this);
    }
}