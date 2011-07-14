package bourse.agent.ia;

public class Aleatoire extends Decision {
    
    /** Creates a new instance of Aleatoire */
    public Aleatoire(bourse.agent.Agent appelant) {
        super(appelant);
    }
    
    /** Algorithme de choix d'une action.  */
    public void choixAction() {
        if (pere.getWallet() < (float)5.0) {
            System.out.println("<<< Aleatoire >>> J'ai d�cid� de partir.");
            pere.setAction(new bourse.agent.sdd.Action(bourse.agent.sdd.Action.bilan));
        } else {
            bourse.agent.sdd.Action a = null;
            do {
                a = new bourse.agent.sdd.Action();
                pere.setAction(a);
            } while (a.getAction() == bourse.agent.sdd.Action.bilan);
            System.out.print("<<< Aleaoire >>> J'ai d�cid� ");
            switch (a.getAction()) {
                case bourse.agent.sdd.Action.adversaires    : System.out.println("de demander la liste des agents connect�s."); break;
                case bourse.agent.sdd.Action.attenteEnchere : System.out.println("d'attendre une ench�re."); break;
                case bourse.agent.sdd.Action.aucune         : System.out.println("de ne rien faire."); break;
                case bourse.agent.sdd.Action.bilan          : System.out.println("de faire le bilan."); break;
                case bourse.agent.sdd.Action.migrer         : System.out.println("de migrer"); break;
                case bourse.agent.sdd.Action.programme      : System.out.println("de demander le programme."); break;
                case bourse.agent.sdd.Action.vendre         : System.out.println("de vendre un de mes livres."); break;
            }
        }
    }
    
    /** On donne en entr�e la liste des livres que l'on poss�de, l'agorithme
     *  doit d�terminer le livre � vendre. On retourne un objet
     *  bourse.protocole.ProposeVente pr�t � �tre export�.
     */
    public bourse.protocole.ProposeVente choixLivreAVendre(bourse.agent.sdd.ListeLivre l) {
        // L'algorithme choisit le premier livre qui n'appartient pas � la cat�gorie de l'agent.
        java.util.Iterator i = l.getListe().values().iterator();
        boolean trouve = false;
        bourse.sdd.Livre livre = null;
        while (i.hasNext() && !trouve) {
            livre = (bourse.sdd.Livre)i.next();
            trouve = (!livre.getCategorie().equals(pere.getCategorie()));
        }
        return new bourse.protocole.ProposeVente(bourse.placeDeMarche.enchere.Enchere.NOM[new java.util.Random().nextInt(5)], new java.util.Random().nextFloat()*livre.getPrixAchat(), livre.getId());
    }
    
    /** C'est le prix qui sera envoy� � la pdm dans le message du protocole.  */
    public float choixPrix() {
        bourse.sdd.Livre livre = pere.getEnvironnement().getCourante().getLivre();
        return livre.getPrix();
    }
    
    /** C'est l'�valutation pr�alable du prix maximum que l'agent est pr�t �
     *  d�bourser pour vendre son livre.
     */
    public float choixPrixMax() {
        return pere.getWallet();
    }
    
    /** C'est l'�valuation de l'int�r�t port� par le livre en vente.  */
    public boolean livreInteressant() {
        return pere.getEnvironnement().getCourante().getLivre().getCategorie().equals(pere.getCategorie());
    }
    
    /** Renvoie le temps d'attente en millisecondes. Ce temps est utilis� par
     *  l'agent pour "annuler" une attente de r�ponse � une de ses requ�tes
     *  synchronis�es.
     */
    public long timeout() {
        return 0;
    }
    /** Renvoie vrai si la vente est int�ressante. */
    public boolean venteInteressante() {
        return new java.util.Random().nextBoolean();
    }
    /** C'est l'�valuation de l'int�r�t port� par le livre en vente. */
    public boolean livreInteressant(bourse.sdd.Livre l, int typeEnchere, float miseAPrix) {
        return new java.util.Random().nextBoolean();
    }
    
}
