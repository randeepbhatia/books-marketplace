package bourse.agent.sdd;

/** R�pertorie les �tats possibles. */
public class Etat {
    
    /** Variables d'instance. */
    /** 0 : Quitter. */
    public static int quitter = 0;
    /** 1 : Etat initial. */
    public static int initial = 1;
    /** 2 : Connait les pdms actives. */
    public static int connaitPdms = 2;
    /** 3 : Pdm choisie dans la liste des actives. */
    public static int pdmChoisie = 3;
    /** 4 : Connexion physique effectu�e. */
    public static int connectePhysiquement = 4;
    /** 5 : Attente d'une r�ponse au WELCOME. */
    public static int attenteRESULTWELCOME = 5;
    /** 6 : Pret pour travailler. */
    public static int pret = 6 ;
    /** 7 : Attente d'une r�ponse au BYE. */
    public static int attenteRESULTBYE = 7;
    /** 8 : Non connect� physiquement. */
    public static int nonConnecte = 8;
    /** 9 : Action choisie. */
    public static int actionChoisie = 9;
    /** 10 : Attente de r�ponse � la demande de vente. */
    public static int attentePropositionEnchere = 10;
    /** 11 : Attente du d�clenchement de la vente. */
    public static int attenteDeclenchementEnchere = 11;
    /** 12 : Debut du mode ench�re. */
    public static int modeEnchere = 12;
    /** 13 : Attente du r�sultat de sa vente. */
    public static int attenteRESULTATdeSaVente = 13;
    /** 14 : Ench�re interessante. */
    public static int enchereInteressante = 14;
    /** 15 : L'enchere est une Ench�reUn. */
    public static int enchereUnOuQuatre = 15;
    /** 16 : L'enchere est une Ench�reDeux ou une ench�re cinq car on g�re de la meme facon. */
    public static int enchereDeuxOuCinq = 16;
    /** 17 : L'enchere est une Ench�reTrois. */
    public static int enchereTrois = 17;
    /** L'�tat courant. */
    private int etat;
    
    /** Constructeur. */
    public Etat(int etat) { this.etat = etat; }
    
    /** M�thodes. */
    /** Renvoie l'�tat cod� sous forme d'entier. */
    public int getEtat() { return this.etat; }
    /** Modifie la valeur de l'�tat. */
    public void setEtat(int etat) { this.etat = etat; }
    /** M�thode d'affichage qui pr�sente le code et la signification de l'�tat. */
    public String toString(int decalage) {
        String delta = "";
        for (int i=0; i<decalage; i++) delta += " ";
        String output = delta + String.valueOf(this.etat) + " (";
        switch (this.etat) {
            case 0: output += "quitter"; break;
            case 1: output += "initialis�"; break;
            case 2: output += "connait les pdms actives"; break;
            case 3: output += "pdm choisie dans la liste des actives"; break;
            case 4: output += "connexion physique effectu�e"; break;
            case 5: output += "attente d'une r�ponse au WELCOME"; break;
            case 6: output += "pret pour travailler"; break;
            case 7: output += "attente d'une r�ponse au BYE"; break;
            case 8: output += "non connect� physiquement"; break;
            case 9: output += "action choisie"; break;            
            case 10: output += "attente de r�ponse � la demande de vente"; break;  
            case 11: output += "attente du d�clenchement de la vente"; break;
            case 12: output += "debut du mode ench�re"; break;
            case 13: output += "attente du r�sultat de sa vente"; break;
            case 14: output += "ench�re interessante"; break;
            case 15: output += "ench�re � prendre ou � laisser ou ench�re descendante"; break;
            case 16: output += "ench�re � pli scell� ou ench�re de Vickrey"; break;
            case 17: output += "ench�re ascendante"; break;
            //case 18: output += "ench�re descendante"; break;
            default : output += "ench�re non g�r�e"; break;
        } return output + ")";
    }
    /** Vrai si l'�tat n�cessite une synchronisation. */
    public boolean isWaiting() {
        return (/*etat == pret
      || */etat == modeEnchere
        || etat == enchereInteressante
        || etat == enchereUnOuQuatre
        || etat == enchereDeuxOuCinq
        || etat == enchereTrois
        || etat == actionChoisie);
    }
    /** Renvoie vrai si l'�tat accepte les messages asynchrones c'est � dire
     *  les messages resultprogramme, resultagent et resultat.
     *  6 7 10 11 12 13 14 15 16 17 18 */
    public boolean acceptAsynchronus() {
        return (etat == 6 || etat == 7 || etat == 10
        || etat == 11 || etat == 12 || etat == 13
        || etat == 14 || etat == 15 || etat == 16
        || etat == 17 || etat == 9);
    }
    /** test */
    public static void main(String argc[]) {
        Etat e = new Etat(2);
        System.out.println(e.toString(0));
    }    
}
