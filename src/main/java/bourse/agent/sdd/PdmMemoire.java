package bourse.agent.sdd;

/** Stocke les donn�es relatives aux Pdms. */
public class PdmMemoire extends Pdm {
    
    /** Variables d'instance. */
    /** Vrai si on a d�j� visit� la pdm. */
    private boolean visitee = false;
    /** Vrai si la pdm est active. */
    private boolean active = true;
    /** Stocke le dernier programme recu par cette pdm. */
    private ListeProgramme programme;
    /** STocke les encheres g�r�es par la pdm entre 1 et 5*/
    private boolean[] encheregeree = {true,true,true,true,true};
    /** donne le dernier num�ro de tour sur la pdm. */
    private int dernier_numero_tour = 0;
    /** Constructeur. */
    /** Constructeur de PdmMemoire. */
    public PdmMemoire(String nom, String adresse, boolean visitee, boolean active, ListeProgramme programme, int num) {
        super(nom, adresse);
        this.visitee = visitee;
        this.active = active;
        this.programme = programme;
        this.dernier_numero_tour = num;    }
    /** Constructeur de PdmMemoire � partir d'une Pdm */
    public PdmMemoire(Pdm pdm) {
        super(pdm.getNom(), pdm.getAdresse().toString());
        this.visitee = false;
        this.active = true;
        this.programme = new ListeProgramme();
        this.dernier_numero_tour = 0;
    }
    
    /** M�thodes. */
    /** Renvoie un tableau des encheres g�r�e par la pdm*/
    public boolean[] getEnchereGeree(){ return this.encheregeree; }
    /** met directement � false l'enchere qui n'est pas g�r�e*/
    public void setNonEnchereGeree(int e){ if ((e>=1) & (e<=5))this.encheregeree[e-1] = false; }
    /** Renvoie true si la pdm a d�j� �t� visit�e. */
    public boolean getVisitee() { return this.visitee; }
    /** Modifie l'�tat de visite. */
    public void setVisitee(boolean visite) { this.visitee = visite; }
    /** Renvoie l'�tat d'activit�. */
    public boolean getActive() { return this.active; }
    /** Modifie l'�tat d'activit�. */
    public void setActive(boolean etat) { this.active = etat; }
    /** Renvoie le programme actuel. */
    public ListeProgramme getProgramme() { return this.programme; }
    /** Modifie le programme actuel. */
    public void setProgramme(ListeProgramme lp) { this.programme = lp; }
    /** Modifie le dernier numero de tour constat� sur la pdm. */
    public void setNumeroDernierTour(int num) { this.dernier_numero_tour = num; }
    /** Renvoie le dernier numero de tour constat� sur la pdm. */
    public int getNumeroDernierTour() { return this.dernier_numero_tour; }
    /** M�thode d'affichage dans un javax.swing.JTable */
    public void toRow(javax.swing.JTable tableau, int numeroLigne) {
        tableau.setValueAt(getNom().toString(), numeroLigne, 0);
        tableau.setValueAt(getAdresse().toString(), numeroLigne, 1);
        tableau.setValueAt((getActive()?"oui":"non"), numeroLigne, 2);
        tableau.setValueAt((getVisitee()?"oui":"non"), numeroLigne, 3);
        String res = "";
        for (int i=0; i<5; i++) { res += getEnchereGeree()[i]?"oui/":"non/"; }
        tableau.setValueAt(res.substring(0, res.length()-1), numeroLigne, 4);
        tableau.setValueAt(new Integer(getNumeroDernierTour()), numeroLigne, 5);
    }
    /** M�thode d'affichage. */
    public String toString(int decalage) { 
        return super.toString(decalage)
            + ", visit�e = " + this.visitee + ", active = " + this.active + ", programme = \n" + this.programme.toString(decalage+1);
    }
    /** Programme principal. */
    public static void main(String[] argc) {
        bourse.agent.Visualisation visu = new bourse.agent.Visualisation();
        visu.show();
        javax.swing.table.DefaultTableModel tm = new javax.swing.table.DefaultTableModel(
            new String [] {"Nom", "Adresse", "Active", "Visit�e", "Ench�res g�r�es", "Numero du tour" },
            4
        );
        javax.swing.JTable tableau = new javax.swing.JTable(tm);
        PdmMemoire p1 = new PdmMemoire("Groupe-A", "HOME", false, true, new ListeProgramme(),1);
        PdmMemoire p2 = new PdmMemoire("Groupe-B", "192.168.1.2:80", true, false, new ListeProgramme(),1);
        PdmMemoire p3 = new PdmMemoire("Groupe-A", "0.0.0.0:0", false, false, new ListeProgramme(),1);
        PdmMemoire p4 = new PdmMemoire(new Pdm("Groupe-X", "169.254.1.5:2525"));
        System.out.println(p1.toString(0)); p1.toRow(tableau, 0);
        System.out.println(p2.toString(1)); p2.toRow(tableau, 1);
        System.out.println(p3.toString(2)); p3.toRow(tableau, 2);
        System.out.println(p4.toString(3)); p4.toRow(tableau, 3);
        visu.getJScrollPanePdmMemoire().remove(visu.getTableauPdmMemoire());
        visu.getJScrollPanePdmMemoire().setViewportView(tableau);
    }
}
