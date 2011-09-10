package bourse.placeDeMarche;

import java.util.*;
import bourse.protocole.*;
import bourse.sdd.*;
import bourse.placeDeMarche.enchere.*;

/** Le commissaire priseur est en quelque sorte le chef d'orchestre de la gestion
 * des ench�res. C'est lui qui lance les ench�res et qui g�re le programme.
 */
public class CommissairePriseur extends Thread {
    
    /** Le nombre d'ench�res pr�vues par le commissaire priseur. */
    public static final int TAILLE_PROGRAMME = 5;
    
    private Programme programme;
    private Enchere enchereCourante = null;
    private PlaceDeMarche placeDeMarche;
    /** Symbolise le fait de savoir si le commissaire priseur est pr�t � recevoir
     * des messages de la part des agents. */
    private boolean attendsDesPropositions;
    private PropositionEnchereA message;
    /** Repr�sente le num�ro de l'ench�re la plus avanc�e pr�vue par le commissaire
     * priseur. */
    private int numEnchere;
    /** Le nombre de transactions restantes � ex�cuter. */
    private int nbTransactionsRestantes = Protocole.transactionsParPlaceDeMarche;
    /** Acc�de au programme pr�vu par le commissaire priseur. */
    public Programme getProgramme() { return this.programme; }
    public Enchere getEnchereCourante() { return this.enchereCourante; }
    private void incrementerNumEnchere() { this.numEnchere++; }
    private void decrementerNumEnchere() { this.numEnchere--; }
    private void decrementerNbTransactionsRestantes() {
        if (nbTransactionsRestantes > 0)
            nbTransactionsRestantes--;
    }
    
    public CommissairePriseur(PlaceDeMarche placeDeMarche) {
        this.placeDeMarche = placeDeMarche;
        this.programme = new Programme(new LinkedList());
        this.numEnchere = 1;
        this.attendsDesPropositions = true;
        this.message = null;
    }
    
    /** R�cupp�re la premi�re des ench�res pr�vue au programme, la stocke dans
     * la donn�e membre <i>enchere courante</i> et l'enl�ve du programme.
     * Cependant, la m�thode s'assure que si l'ench�re est une proposition d'ench�re
     * d'un agent, celui-ci soit bien pr�sent sur la place de march� et le cas �ch�ant
     * le bloque.
     */
    private void setEnchereCourante() {
        try {
            ProgrammePro enchere = (ProgrammePro)programme.getListeProgramme().removeFirst();
            creerItem();
            Livre livre = enchere.getLivre();
            if (!livre.getProprietaire().equalsIgnoreCase(placeDeMarche.getNom()))
                //vendeur different de la pdm
            {
                if (placeDeMarche.getSalleDesVentes().estIdentifie(livre.getProprietaire()))
                { // le vendeur est identifi�
                    placeDeMarche.getSalleDesVentes().getConnexionAgent(livre.getProprietaire()).getAgent().setBloque(true);
                    enchereCourante = Enchere.newInstance(enchere.getNum(), enchere.getPrixVente(), livre.getId(), enchere.getTypeEnchere());
                }
                else
                { // le vendeur n'est plus pr�sent
                    setEnchereCourante();
                }
            }
            else
            { // C'est la place de march� qui r�alise la vente.
                enchereCourante = Enchere.newInstance(enchere.getNum(), livre);
            }
        } catch (NoSuchElementException e) { if (this.placeDeMarche.verbose) System.err.println("Le programme est vide, je "); }
    }
    
    /** Cr�e un nouveau livre prit au hasard parmi tous les livres de la base de 
     * donn�es. Ne fait rien si le commissaire priseur a d�j� fait plus de transactions
     * que 100 - tailleDuProgramme. */
    private void creerItem() {
        if (numEnchere <= Protocole.transactionsParPlaceDeMarche) {
            // Nous pouvons ajouter une nouvelle ench�re au programme.
            programme.getListeProgramme().add(new ProgrammePro(this.numEnchere, placeDeMarche.getRequetes().creerItem()));
            this.incrementerNumEnchere();
        }
    }
    
    /** Cette m�thode synchronis�e pourra �tre appel�e par plusieurs agents en
     * m�me temps, mais elle ne s'ex�cutera toujours qu'une seule fois.
     * Elle met � disposition son message et r�veille le commissaire priseur.
     */
    public synchronized void vousAvezUnMessage(PropositionEnchereA message, ConnexionAgent connexionAgent) {
        System.out.println("Le commissaire Priseur a un message");
        while (!attendsDesPropositions) { try { wait(); } catch (InterruptedException e) { } }
        attendsDesPropositions = false;
        this.message = message;
        if (enchereCourante.getTypeEnchere() == Enchere.ENCHERE_TROIS)
            ((EnchereTrois)this.enchereCourante).setConnexionAgent(connexionAgent);
        else if (enchereCourante.getTypeEnchere() == Enchere.ENCHERE_QUATRE)
            ((EnchereQuatre)this.enchereCourante).setConnexionAgent(connexionAgent);
        this.notifyAll();
    }
    
    /** Annule l'ench�re courante en d�cr�mentant chaque num�ro d'ench�res dans
     * le programme. */
    private void annulerEnchere() {
        decrementerNumEnchere();
        ListIterator i = programme.getListeProgramme().listIterator();
        while (i.hasNext())
            ((ProgrammePro)i.next()).decrementerNumEnchere();
    }
    
    /** Le commissaire priseur a d�tect� qu'il y avait plus de 3 agents connect�s
     * en dehors du vendeur �ventuel de l'ench�re et de ce fait d�marre une ench�re. */
    public void demarrerEnchere() {
        setEnchereCourante();
        if (placeDeMarche.getVerbose()) System.out.println("OUT PROPOSITIONENCHEREP");
        placeDeMarche.getSalleDesVentes().envoyerIdentifies(enchereCourante.annonce().toXML());
        Resultat resultat = null;
        switch (enchereCourante.getTypeEnchere()) {
        case Enchere.ENCHERE_UN :
        case Enchere.ENCHERE_DEUX :
        case Enchere.ENCHERE_CINQ :
            // Il s'agit d'ench�res � r�ponse unique. Le commissaire priseur ne fait que s'endormir pendant la dur�e du timeout.
            try {
                wait(((EnchereReponseUnique)enchereCourante).TIMEOUT * 1000);
            } catch (InterruptedException e) { }
            resultat = enchereCourante.resolution();
            break;
        case Enchere.ENCHERE_TROIS :
            // Tant que j'attends des propositions, je dors TIMEOUT secondes.
            attendsDesPropositions = true;
            while (attendsDesPropositions) {
                try { wait(((EnchereReponseMultiple)enchereCourante).TIMEOUT * 1000); } catch (InterruptedException e) { }
                if (this.message == null)
                    // Le timeout a �t� atteint
                    attendsDesPropositions = false;
                else {
                    // Le commissaire priseur s'est fait r�veill� par un agent.
                    enchereCourante.setPrixCourant(message.getEnchere());
                    if (placeDeMarche.getVerbose()) System.out.println("OUT PROPOSITIONENCHEREP");
                    placeDeMarche.getSalleDesVentes().envoyerIdentifies(((EnchereTrois)enchereCourante).actualiser(message,((EnchereReponseMultiple)enchereCourante).getConnexionAgent().getAgent().getNomAgent()).toXML());
                    attendsDesPropositions = true;
                    notifyAll();
                    message = null;
                }
            }
            ConnexionAgent dernierAgentEncherisseur = ((EnchereReponseMultiple)enchereCourante).getConnexionAgent();
            if (dernierAgentEncherisseur == null) // Aucun agent n'a ench�ri, la vente est annul�e.
                resultat = ((EnchereTrois)enchereCourante).resolution();
            else
                resultat = ((EnchereTrois)enchereCourante).resolution(enchereCourante.getPrixCourant(), dernierAgentEncherisseur.getAgent().getNomAgent());
            break;
        case Enchere.ENCHERE_QUATRE :
            attendsDesPropositions = true;
            while (message == null && enchereCourante.getPrixCourant() > 0.15*enchereCourante.getPrixDepart()) {
                // D�s que je re�ois une proposition, j'arrete d'attendre
                try { wait(((EnchereReponseBoucle)enchereCourante).TIMEOUT * 1000); } catch (InterruptedException e) { }
                if (message == null) { // On est arriv� au TIMEOUT
                    if (placeDeMarche.getVerbose()) System.out.println("OUT PROPOSITIONENCHEREP");
                    placeDeMarche.getSalleDesVentes().envoyerIdentifies(((EnchereQuatre)enchereCourante).actualiser().toXML());
                }
            }
        }
        // Cr�ation d'une copie du livre vendu qui va sauvegarder le nouveau prix et le nouveau proprietaire.
        Livre nouveauLivre = new Livre(enchereCourante.getLivre());
        // Si c'est une ench�re descendante, on r�soud l'ench�re
        if (enchereCourante.getTypeEnchere() == Enchere.ENCHERE_QUATRE) {
            if (message != null) { // On a re�u une proposition valable de la part d'un agent.
                nouveauLivre.setProprietaire(((EnchereReponseBoucle)enchereCourante).getConnexionAgent().getAgent().getNomAgent());
                resultat = ((EnchereQuatre)enchereCourante).resolution(enchereCourante.getPrixCourant(), ((EnchereReponseBoucle)enchereCourante).getConnexionAgent().getAgent().getNomAgent());
            } else // L'ench�re n'a pas trouv� preneur et est arriv� � 1% du prix de d�part
                resultat = enchereCourante.resolution();
            message = null;
        }
        // Le commissaire vient de se r�veiller. Il va maintenant r�soudre l'ench�re.
        enchereCourante.setPrixCourant(resultat.getEnchere());

        if (!resultat.getAcheteur().equals(enchereCourante.getVendeur())) {
            // L'ench�re a r�ussie. Le vendeur doit donc se voir retirer le livre contre l'argent de l'acheteur.
            System.out.println("CommissairePriseur : L'ench�re a trouv� preneur.\nresultat.getAcheteur() = " + resultat.getAcheteur());
            if (!placeDeMarche.getRequetes().transaction(enchereCourante.getLivre().getId(), enchereCourante.getVendeur(), resultat.getAcheteur(), enchereCourante.getPrixCourant())) {
                // La mise � jour des informations a �chou�e. Il faut modifier le message de r�sultat et r�ordonner les num�ros d'ench�res courantes.
                resultat.setAcheteur(enchereCourante.getVendeur());
                annulerEnchere();
                System.out.println("CommissairePriseur : La transaction a �chou�e.");
            } else {
                // La transaction a bien eu lieu. Nous devons changer le propri�taire du livre, d�cr�menter le solde de l'agent, lib�rer les agents, d�cr�menter le nombre de transactions restantes.
                nouveauLivre.setProprietaire(resultat.getAcheteur());
                nouveauLivre.setPrixAchat(enchereCourante.getPrixCourant());
                
                // Retrait de l'argent � l'acheteur
                Agent acheteur = placeDeMarche.getSalleDesVentes().getConnexionAgent(resultat.getAcheteur()).getAgent();
                acheteur.setArgent(acheteur.getArgent() - enchereCourante.getPrixCourant());
                
                // Credit de l'argent au vendeur si c'est un agent
                ConnexionAgent connexionAgent = placeDeMarche.getSalleDesVentes().getConnexionAgent(enchereCourante.getVendeur());
                if (connexionAgent != null) { // C'est un agent qui a vendu.
                    Agent vendeur = connexionAgent.getAgent();
                    vendeur.setArgent(vendeur.getArgent() + enchereCourante.getPrixCourant());
                }

                decrementerNbTransactionsRestantes();
                System.out.println("CommissairePriseur : La transaction a r�ussie.");
            }
        } else {
            annulerEnchere();
            System.out.println("Commissaire Priseur : l'ench�re n'a pas trouv�e preneur.");
        }
        placeDeMarche.getSalleDesVentes().libererAgents();
        if (placeDeMarche.getVerbose()) System.out.println("OUT RESULTAT");
        placeDeMarche.getSalleDesVentes().envoyerIdentifies(resultat.toXML());
        enchereCourante.setLivre(nouveauLivre);
    }
    
    /** Permet de r�veiller le commissaire priseur. */
    public synchronized void reveilleToi() { this.notifyAll(); }
    
    public synchronized void run() {
        System.out.println("Arriv�e du commissaire priseur dans la salle des ventes.");
        // Le commissaire priseur r�serve TAILLE_PROGRAMME livres au d�but.
        for (int i = 0; i < CommissairePriseur.TAILLE_PROGRAMME; i++) { creerItem(); }
        while (nbTransactionsRestantes >= 0 && placeDeMarche.getAccepterAgents()) {
            while (!placeDeMarche.getSalleDesVentes().plusDeTroisAgentsIdentifies() && placeDeMarche.getAccepterAgents()) {
                enchereCourante = null;
                // Il n'y a pas encore assez d'agents identifi�s pour que le commissaire priseur lance une ench�re.
                try { wait(); } catch (InterruptedException e) { System.err.println("J'ai �t� interrompu alors que j'attendais qu'il y ait assez d'agents identifi�s pour lancer une ench�re."); }
                System.out.println("Commissaire priseur : je me suis fait r�veill�.");
                notifyAll();
            }
            demarrerEnchere();
        }
    }
    
    public String toHtml() {
        String sortie = "<h2>Commissaire Priseur</h2>\n";
        sortie += "<p>Il me reste <b>" + nbTransactionsRestantes + "</b> transactions avant la fin de la session.<br>J'anticipe " + TAILLE_PROGRAMME + " ench&egrave;res dans mon programme.</p>";
        if (enchereCourante != null)
            sortie += "<h3>Ench&egrave;re courante</h3>" + enchereCourante.toHtml();
        sortie += "<h3>Programme</h3>" + programme.toHtml();
        return sortie;
    }
    
}