package bourse.agent.ia;

import java.net.*;
import java.io.*;
import bourse.protocole.*;
import bourse.agent.sdd.*;

/** Centralise les m�thodes d'ia. */
public class Ia extends Decision {
    
    /** M�thodes */
    /** Constructeur. */
    public Ia(bourse.agent.Agent appelant) { super(appelant); }
    /** Renvoie le temps d'attente en millisecondes � param�trer en fonction de
     *  l'impatience. Il y aurait un net b�n�fice � sauvegardr les temps moyens
     *  de r�ponse d'une pdm pour pouvoir ajouster automatiquement ce timeout en
     *  fonction de la pdm. On gagnerai en vitesse et donc en efficacit�
     *  g�n�rale. */
    public long timeout() { return 10000; }
    /** Algorithme de choix d'une action. */
    public void choixAction() {
        /** on s'informe en fonction du nombre d'actions pass�es sur la pdm. */
        /** Si on vient d'arriver dans la pdm, on doit demander la liste des agents connect�s. */
        if (pere.getEnvironnement().getNombreActions() == 0)
            pere.setAction(new Action(5));
        /** C'est notre deuxi�me action, on t�l�charge le programme. */
        else if (pere.getEnvironnement().getNombreActions() == 1)
            pere.setAction(new Action(4));
        /** si on a pas t�l�charg� la liste des agents depuis 5 secondes, on
         *  redemande la liste des agents. */
        else if ((new java.util.Date().getTime() - pere.getEnvironnement().getDateListeAgent()) > 5000) {
            pere.setAction(new Action(5));
        }
        /** si on a pas t�l�charg� la liste des programmes depuis 5 secondes, on
         *  redemande la liste des programmes. */
        else if ((new java.util.Date().getTime() - pere.getEnvironnement().getDateListeProgramme()) > 5000) {
            pere.setAction(new Action(4));
        }
        /** si moins de deux agents sont connect�s, on peut migrer. */
        else if (this.pere.getMemoire().getAgents().taille() < 3)
            pere.setAction(new Action(Action.migrer));
//            pere.setAction(new Action(6));
        /* Test de la cat�gorie du premier livre du programme de la pdm n'est pas int�ressante. */
        else if (pere.getMemoire().getPdms().acceder(pere.getCurrentPdm().getNom()).getProgramme().getIeme(1).getLivre() != null) {
            if (pere.getMemoire().getPdms().acceder(pere.getCurrentPdm().getNom()).getProgramme().getIeme(1).getLivre().getCategorie().getCode() != pere.getCategorie().getCode())
                pere.setAction(new Action(Action.migrer));
//                pere.setAction(new Action(6));
        } else
            pere.setAction(new Action(6));
    }
    /** Si le livre est de notre cat�gorie, on fixe � 120 % du prix propos�,
     *  sinon, on fixe � 80%. */
    public float choixPrix() {
        return this.pere.getEnvironnement().getCourante().getPrixMaximum();
    }
    /** On donne le prix du livre comme prix maximum. */
    public float choixPrixMax(){
        return pere.getEnvironnement().getCourante().getLivre().getPrix();
    }
    /** Dans une premi�re approche, un livre est int�ressant si il appartient �
     *  la m�me cat�gorie que l'agent.
     *  Sinon, une fois sur cinq, on le juge int�ressant quand m�me. */
    public boolean livreInteressant(bourse.sdd.Livre l, int typeEnchere, float miseAPrix) {
        boolean reponse = false;
        float seuilBonneCategorie;
        float seuilAutreCategorie;
        float seuil;
        /** si le livre est de la m�me cat�gorie que l'agent. */
        if (l.getCategorie().equals(pere.getCategorie())) {
            seuilBonneCategorie = (float)0.5; // on est le seul � �tre de cette cat�gorie
            seuilAutreCategorie = (float)1.1; // d'autes agents suceptibles d'�tre int�ress�s.
            seuil = 2;
        } else {
            seuil = (float)0.8;
            seuilBonneCategorie = (float)0.5;
            seuilAutreCategorie = (float)0.8;
        }
        System.out.println("typeEnch�re = " + typeEnchere);
        switch (typeEnchere) {
            case 1 :
            case 2 :
            case 5 :
                if (pere.getMemoire().getAgents().concurrent(l.getCategorie(), pere.getNom()) == 0) {
                    pere.getEnvironnement().getCourante().setPrixMaximum((float)(seuilBonneCategorie * l.getPrix() * l.getEtat()));
                } else {
                    pere.getEnvironnement().getCourante().setPrixMaximum((float)(seuilAutreCategorie * l.getPrix() * l.getEtat()));
                }
                reponse =true;
                break;
            case 3 :
                reponse = (miseAPrix < (float)(seuil * (l.getPrix() * l.getEtat()) ));
                if (pere.getMemoire().getAgents().concurrent(l.getCategorie(), pere.getNom()) == 0)
                    pere.getEnvironnement().getCourante().setPrixMaximum((float)(miseAPrix * 1.2));
                else
                    pere.getEnvironnement().getCourante().setPrixMaximum(miseAPrix);
                break;
            case 4 :
                pere.getEnvironnement().getCourante().setPrixMaximum(miseAPrix);
                float prixDeBase = ((float)(l.getPrix() * l.getEtat()));
                if (pere.getMemoire().getAgents().concurrent(l.getCategorie(), pere.getNom()) == 0)
                    reponse = (miseAPrix < (float)(seuilBonneCategorie * prixDeBase));
                else
                    reponse = (miseAPrix < (float)(seuilAutreCategorie * prixDeBase));
                System.out.println("Le livre est-il int�ressant ?\nMiseAPrix = " + miseAPrix + "\nprixDeBase = " + prixDeBase + "si il n'y a pas de concurrents : " +  (miseAPrix < (float)(seuilBonneCategorie * prixDeBase)) + "si il y a des concurrents dans la meme cat�gorie = " + (miseAPrix < (float)(seuilAutreCategorie * prixDeBase)));
                break;
        }
        return reponse;
    }
    /** On donne en entr�e la liste des livres que l'on poss�de, l'agorithme doit d�terminer le livre � vendre.
     *  on retourne un objet bourse.protocole.ProposeVente pr�t � �tre export�. */
    public bourse.protocole.ProposeVente choixLivreAVendre(ListeLivre l) {
        float prix = 0;
        int id = pere.getMemoire().getLivreAVendre();
        bourse.sdd.Livre ouvrage = pere.getMemoire().getPossessions().get(id).getLivre();
        String nom = "Enchere";
        if (new java.util.Random().nextBoolean()) {
            nom += "Trois";
            prix = (float)(ouvrage.getPrixAchat() * 1.1);
        } else {
            nom += "Quatre";
            prix = (float)(ouvrage.getPrixAchat() * 1.5);
        }
        return new bourse.protocole.ProposeVente(nom, prix, id);
    }
    public boolean venteInteressante() {
        AideDecisionVente ad = this.pere.getMemoire().getAideDecisionVente();
        ad.miseAJourQVente();
        if (pere.getMemoire().getTemps() > 50)
            return ((ad.getSommeQVente() / ad.getNbQVenteCalcules()) < (0.5 * ad.getQVenteActuel()));
        else
            return false;
    }
    public float qVente() {
        float coeffVente = 0;
        float totalVente = 0;
        int meilleureVente = 0;
        float maxCoeffVente = 0;
        bourse.sdd.Livre li = new bourse.sdd.Livre();
        java.util.Iterator parcours = pere.getMemoire().getPossessions().getValues().iterator();
        while (parcours.hasNext()) {
            Possession p = (Possession)parcours.next();
            li = p.getLivre();
            if (li.getProprietaire().equalsIgnoreCase(pere.getNom()) && !p.getInstanceDeVente())
                if (!pere.getCategorie().equals(li.getCategorie())) {
                    coeffVente = interetVente(li) * pere.getMemoire().getAgents().coefficientCategorie(li.getCategorie());                
                    if (coeffVente > maxCoeffVente) {
                        maxCoeffVente = coeffVente;
                        meilleureVente = li.getId();
                    }
                    totalVente += coeffVente;
                }
        }
        pere.getMemoire().setLivreAVendre(meilleureVente);
        return totalVente;
    }
    /** Calcule un coefficient qui est plus grand si on a fait une bonne affaire. */
    public float interetVente(bourse.sdd.Livre li) {
        return li.getPrix() * li.getEtat() / li.getPrixAchat();
    }
}
