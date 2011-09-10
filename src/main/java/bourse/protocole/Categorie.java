package bourse.protocole;

/** R�pertorie toutes les cat�gories du protocole. */
public class Categorie {
    
    // Variables d'instance

    // Constantes utilis�es pour l'�change et le stockage des cat�gories.
    public static final String SF = "Science fiction";
    public static final String BD = "Bandes Dessin�es";
    public static final String SCIENCE = "Science";
    public static final String ROMAN = "Romans Policiers";
    public static final String INFO = "Informatique";
    public static final String AUCUNE = "Aucune";
    // Constantes utilis�es pour le stockage dans la Base de donn�es.
    private static final String _SF = "Science fiction";
    private static final String _SCIENCE = "Science";
    private static final String _ROMAN = "Romans Policiers";
    private static final String _BD = "Bandes dessin�es";
    private static final String _INFO = "Informatique";
    /** Classe utilis�e pour g�nerer des cat�gories al�atoires */
    private static final java.util.Random generateurAleatoire = new java.util.Random();
    
    /** La cat�gorie courante */
    private int categorie;
    
    // Constructeurs.
    /** Construit une cat�gorie au hasard. */
    public Categorie() { this.categorie = generateurAleatoire.nextInt(5); }
    /** Construit une cat�gorie � partir de son code. */
    public Categorie(int c) { this.categorie = c; }
    /** Construit une cat�gorie � partir d'une string. */
    public Categorie(String c) {
        if ((c.startsWith("s") || c.startsWith("S")) && c.length() > 11) this.categorie = 0;
        else if (c.startsWith("b") || c.startsWith("B")) this.categorie = 1;
        else if ((c.startsWith("s") || c.startsWith("S")) && c.length() < 11) this.categorie = 2;
        else if (c.startsWith("r") || c.startsWith("R")) this.categorie = 3;
        else if (c.startsWith("i") || c.startsWith("I")) this.categorie = 4;
        else if (c.equalsIgnoreCase(this.AUCUNE)) this.categorie = 5;
        else this.categorie = 5;
    }
    /** Construit une cat�gorie � partir d'une autre cat�gorie (constructeur par
     * recopie. */
    public Categorie(Categorie c) {
        this.categorie = c.getCode();
    }
    /** Cette m�thode g�n�re une cat�gorie � partir d'une cha�ne stocqu�e dans la
     * base de donn�es, donc lib�r�e de tout caract�re folklorique et autre URLEncode. */
    public static Categorie newCategorieFromBd(String c) {
        if (c.equalsIgnoreCase(_SF)) return new Categorie(SF);
        else if (c.equalsIgnoreCase(_SCIENCE)) return new Categorie(SCIENCE);
        else if (c.equalsIgnoreCase(_ROMAN)) return new Categorie(ROMAN);
        else if (c.equalsIgnoreCase(_BD)) return new Categorie(BD);
        else if (c.equalsIgnoreCase(_INFO)) return new Categorie(INFO);
        else return new Categorie(AUCUNE);
    }
        
    /** M�thodes. */
    /** Acc�der � la cat�gorie. */
    public String getCategorie() {
        String output;
        switch (this.categorie) {
            case 0: output = this.SF; break;
            case 1: output = this.BD; break;
            case 2: output = this.SCIENCE; break;
            case 3: output = this.ROMAN; break;
            case 4: output = this.INFO; break;
            case 5: output = this.AUCUNE; break;
            default: output = this.AUCUNE; break;
        } return output;
    }
    /** Modifier la cat�gorie en donnant une string. */
    public void setCategorie(String categorie) { this.categorie = new Categorie(categorie).getCode();} 
    /** Acc�der au code de la cat�gorie. */
    public int getCode() { return this.categorie; }
    /** Modifier la cat�gorie en donnant son code. */
    public void setCategorie(int categorie) { this.categorie = categorie; }
    /** M�thode de comparaison standard */
    public boolean equals(Object o) {
        if (o instanceof Categorie)
            return ((Categorie)o).getCode() == this.categorie;
        else
            return false;
    }
    /** M�thode d'affichage. */
    public String toString(int decalage) {
        String delta = "";
        for (int i=0; i<decalage; i++) delta += " ";
        return delta + this.getCode() + " (" + this.getCategorie() + ")"; }
    /** M�thode d'affichage par d�faut. */
    public String toString() {
        return getCategorie();
    }
    /** M�thode publique. */
    public static void main(String argc[]) {
        Categorie c = new Categorie();
        System.out.println(c.getCategorie());
        c.setCategorie(c.INFO);
        System.out.println(c.toString(0));
        System.out.println(c.getCategorie());
        c.setCategorie(2);
        System.out.println(c.toString(0));
        c = Categorie.newCategorieFromBd("Science fiction");
        System.out.println(c.toString(0));
        c = Categorie.newCategorieFromBd("Bandes dessin�es");
        System.out.println(c.toString(0));
        c = Categorie.newCategorieFromBd("Science");
        System.out.println(c.toString(0));
        c = Categorie.newCategorieFromBd("Romans Policiers");
        System.out.println(c.toString(0));
        c = Categorie.newCategorieFromBd("Informatique");
        System.out.println(c.toString(0));
    }
}
