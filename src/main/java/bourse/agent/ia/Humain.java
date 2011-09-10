package bourse.agent.ia;

import bourse.agent.sdd.*;

public class Humain extends Decision {
    /** Constructeur de descision humaine. */
    public Humain(bourse.agent.Agent appelant) { super(appelant); }
    /** Arbitrairement 10 secondes d'attente. */
    public long timeout() { return 10000; }
    /** On demande � l'utilisateur d'entrer l'action � r�aliser. */
    public void choixAction() {
        pere.setAction(new Action(Action.aucune));
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        System.out.println(" 0 : aucune (pour r�pondre � une proposition d'ench�re)");
        System.out.println(" 1 : vendre son premier bouquin");
        System.out.println(" 2 : migrer");
        System.out.println(" 3 : bilan");
        System.out.println(" 4 : programme");
        System.out.println(" 5 : agents connect�s");
        System.out.println(" 6 : attendre une proposition ench�re");
        System.out.print("action ? ");
        try {
            pere.setAction(new Action(Integer.parseInt(in.readLine())));
        } catch (java.io.IOException e) { e.printStackTrace(System.err); pere.setAction(new Action(0)); }
    }
    /** On demande � l'utilsateur d'entrer un prix. */
    public float choixPrix() {
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        System.out.print("prix ? ");
        float reponse = 0;
        try {
            reponse = Float.parseFloat(in.readLine());
        } catch (java.io.IOException e) { e.printStackTrace(System.err); return reponse; }
         return reponse;
    }
    /** On d�clare � l'utilisateur le prix maximaum fix�. */
    public float choixPrixMax() {
//        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
//        System.out.print("prix maximum ? ");
        float reponse = 0;
//        try {
//            reponse = Float.parseFloat(in.readLine());
//        } catch (java.io.IOException e) { e.printStackTrace(System.err); return reponse; }
        System.out.println("prix maximum = " + String.valueOf(reponse) );
        return reponse;
    }
    /** On demande � l'utilisateur si la vente l'int�resse ou pas. */
    public boolean livreInteressant(bourse.sdd.Livre l, int typeEnchere, int miseAPrix) {
        pere.getEnvironnement().getCourante().setPrixMaximum(this.choixPrixMax());
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        System.out.println(" 1 : oui");
        System.out.println(" 2 : non");
        System.out.print("vente int�ressante ? ");
        int reponse = 0;
        try {
            reponse = Integer.parseInt(in.readLine());
        } catch (java.io.IOException e) { e.printStackTrace(System.err); return false; }
         if (reponse == 1) return true;
         else return false;
    }
    /** Renvoie vrai si la vente est int�ressante. */
    public boolean venteInteressante() {
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        System.out.println(" 1 : oui");
        System.out.println(" 2 : non");
        System.out.print("vente int�ressante ? ");
        int reponse = 0;
        try {
            reponse = Integer.parseInt(in.readLine());
        } catch (java.io.IOException e) { e.printStackTrace(System.err); return false; }
         if (reponse == 1) return true;
         else return false;
    }
    /** C'est l'�valuation de l'int�r�t port� par le livre en vente. */
    public boolean livreInteressant(bourse.sdd.Livre l, int typeEnchere, float miseAPrix) {
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        System.out.println(" 1 : oui");
        System.out.println(" 2 : non");
        System.out.print("vente int�ressante ? ");
        int reponse = 0;
        try {
            reponse = Integer.parseInt(in.readLine());
        } catch (java.io.IOException e) { e.printStackTrace(System.err); return false; }
         if (reponse == 1) return true;
         else return false;
    }
    /** On demande � l'utilisateur les informations n�cessaires � la vente de
     *  son premier livre. */
    public bourse.protocole.ProposeVente choixLivreAVendre(bourse.agent.sdd.ListeLivre l) {
        java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        bourse.sdd.Livre ouvrage;
        int numero = 1;
        String nom = "Enchere";
        int id;
        int prix = 0;
        java.util.Iterator parcours = l.getValeurs().iterator();
        if (parcours.hasNext()) {
            ouvrage = (bourse.sdd.Livre)parcours.next();
            System.out.print("Le num�ro de l'ench�re : ");
            try { numero = Integer.parseInt(in.readLine());
            } catch (java.io.IOException e) { e.printStackTrace(System.err); }
            switch(numero) {
                case 1: nom += "Un"; break;
                case 2: nom += "Deux"; break;
                case 3: nom += "Trois"; break;
                case 4: nom += "Quatre"; break;
                case 5: nom += "Cinq"; break;
                default: nom += "Un"; break;
            }
            id = ouvrage.getId();
            System.out.print("Le prix propos� : ");
            try { prix = Integer.parseInt(in.readLine());
            } catch (java.io.IOException e) { e.printStackTrace(System.err); }
            return new bourse.protocole.ProposeVente(nom, prix, id);
        } else { return null; }
    }
} 
