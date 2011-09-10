package bourse.agent.ia;

import bourse.agent.sdd.*;

/** Centralise les m�thodes d'ia. */
public abstract class Decision {
    
    /** Variables d'instances. */
    /** Lien vers l'agent. */
    protected bourse.agent.Agent pere;
    
    /** M�thodes */
    /** Constructeur. */
    public Decision(bourse.agent.Agent appelant) { this.pere = appelant; }
    /** M�thode de choix d'une pdm.
     *   1� on doit conna�tre les pdms actives (etat = 2).
     *   2� choisir la premi�re pdm active non visit�e
     *   3� sinon la premi�re pdm active visit�e
     *   4� initialiser la pdm courante.
     *   5� si on a pas trouv�, la pdm courante n'est pas modifi�e. */
    public void choixPdm() {
        if (pere.getEtat() == Etat.connaitPdms) { // l'agent doit �tre dans le bon �tat.
            if (!pere.getMemoire().getPdms().aucuneActive()) { // la liste doit contenir des pdms actives
                PdmMemoire choisie = pere.getMemoire().getPdms().premiereActiveNonVisitee();
                if (choisie == null) choisie = pere.getMemoire().getPdms().premiereActiveVisitee();
                if (choisie != null) { // on a trouv� une pdm satisfaisante.
                    pere.setEtat(Etat.pdmChoisie); // changer d'�tat
                    pere.setHote(choisie.getNom()); // mettre � jour le nom de la pdm choisie.
                    choisie.setVisitee(true); // on visite la pdm. 
                    pere.getMemoire().getPdms().ajouter(choisie); // mise � jour de la pdm choisie dans la m�moire.
                } else System.out.println("Aucune pdm active visit�e ou active non visit�e trouv�e.");
            } else System.out.println("Aucune pdm n'est active.");
        } else System.out.println("Impossible de choisir une pdm dans ces conditions.");
    }
    /** Renvoie le temps d'attente en millisecondes. Ce temps est urilis� par
     *  l'agent pour "annuler" une attente de r�ponse � une de ses requ�tes
     *  synchronis�es. */
    public abstract long timeout();
    /** Algorithme de choix d'une action. */
    public abstract void choixAction();
    /** C'est le prix qui sera envoy� � la pdm dans le message du protocole. */
    public abstract float choixPrix();
    /** C'est l'�valutation pr�alable du prix maximum que l'agent est pr�t �
     *  d�bourser pour vendre son livre. */
    public abstract float choixPrixMax();
    /** C'est l'�valuation de l'int�r�t port� par le livre en vente. */
    public abstract boolean livreInteressant(bourse.sdd.Livre l, int typeEnchere, float miseAPrix);
    /** On donne en entr�e la liste des livres que l'on poss�de, l'agorithme
     *  doit d�terminer le livre � vendre. On retourne un objet
     *  bourse.protocole.ProposeVente pr�t � �tre export�. */
    public abstract bourse.protocole.ProposeVente choixLivreAVendre(ListeLivre l);
    /** Renvoie vrai si la vente est int�ressante. */
    public abstract boolean venteInteressante();
}
