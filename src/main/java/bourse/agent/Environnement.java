package bourse.agent;

import bourse.agent.sdd.*;

/** Stocke l'environnement courant visible de l'agent. Informations destin�es � �tre m�moris�es. */
public class Environnement {
    
    /** Variables d'instance. */
    /** L'ench�re en cours. */
    private Enchere courante;
    /** Renvoie le type d'ench�re demand�. */
    private int typeDemande;
    /** Renvoie le nombre d'actions r�alis�es depuis la connexion � la pdm courante. */
    private int nombreActions;
    /** Renvoie la date du dernier t�l�chargement de la liste des agents. */
    private long dateListeAgent;
    /** Renvoie la date du dernier t�l�chargement de la liste de programmes. */
    private long dateListeProgramme;
    /** Renvoit l'int�r�t port� � l'ench�re en cours. */
    private boolean enchereInteressante;
    
    /** Constructeurs. */
    /** Construit un environnement vierge. */
    public Environnement() {
        courante = new Enchere();
        typeDemande = 0;
        nombreActions = 0;
        dateListeAgent = Long.MAX_VALUE;
        dateListeProgramme = Long.MAX_VALUE;
        enchereInteressante = false;
    }
    /** Construit un environnement avec ench�re. */
    public Environnement(Enchere courante, int typeDemande, int nombreActions) {
        this.courante = courante;
        this.typeDemande = typeDemande;
        this.nombreActions = nombreActions;
    }
    
    /** M�thodes. */
    /** M�thode d'affichage. */
    public String toString(int decalage) {
        String delta = "";
        for (int i=0; i<decalage; i++) delta += " ";
        return delta + "ench�re =\n" + courante.toString(decalage+1) + "\n"
             + delta + "typeEnch�reDemand�e = " + typeDemande + " "
             + delta + "nombreActions = " + nombreActions + " "
             + delta + "dateListeAgent = " + dateListeAgent + " "
             + delta + "dateListeProgramme = " + dateListeProgramme + " "
             + delta + "ench�reInteressante = " + enchereInteressante;
    }
    /** Renvoie le type d'ench�re choisi au tour pr�c�dent par l'agent. */
    public int getTypeDemande() { return this.typeDemande; }
    /** Renvoie l'ench�re courante. */
    public Enchere getCourante(){return this.courante;}
    /** Met � jour l'ench�re courante. */
    public void setCourante(Enchere e){this.courante = new Enchere(e); }
    /** Renvoit le nombre d'actions r�alis�es sur la pdm courante. */
    public int getNombreActions() { return nombreActions; }
    /** Met � jour le nombre d'actions r�alis�es sur la pdm courante. */
    public void setNombreActions(int _nombreActions) { this.nombreActions = _nombreActions; }
    /** Renvoie la date du dernier t�l�chargement de la liste des agents. */
    public long getDateListeAgent() { return dateListeAgent; }
    /** Met � jour la date du dernier t�l�chargement de la liste des agents. */
    public void setDateListeAgent(long _date) { dateListeAgent = _date; }
    /** Met � jour la date du dernier t�l�chargement de la liste des agents avec
     *  la date actuelle. */
    public void setDateListeAgent() { dateListeAgent = new java.util.Date().getTime(); }
    /** Renvoie la date du dernier t�l�chargement de la liste des programmes. */
    public long getDateListeProgramme() { return dateListeProgramme; }
    /** Met � jour la date du dernier t�l�chargement de la liste des programmes. */
    public void setDateListeProgramme(long _date) { dateListeProgramme = _date; }
    /** Met � jour la date du dernier t�l�chargement de la liste des programmes
     *  avec la date actuelle. */
    public void setDateListeProgramme() { dateListeProgramme = new java.util.Date().getTime(); }
    /** Renvoie l'int�r�t port� au livre. */
    public boolean getEnchereInteressante() { return enchereInteressante; }
    /** Modifie l'int�r�t port� au livre. */
    public void setEnchereInteressante(boolean interet) { enchereInteressante = interet; }
    /** Programme principal. */
    public static void main(String argc[]) {
        bourse.sdd.Livre l1 = new bourse.sdd.Livre("l1", "a2", new bourse.protocole.Categorie(), "poche", "O'reilly", (float)50.65, (float)0.45, "2004-01-01", "222222222", 1, "arno", (float)12);
        Environnement e = new Environnement(new Enchere(12, l1, (float)9.67, 25, (float)5, 1, "Protocol'man"), 1, 0);
        System.out.println(e.toString(0));
    }
}
