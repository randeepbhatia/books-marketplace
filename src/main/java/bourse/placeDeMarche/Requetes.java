package bourse.placeDeMarche;

import java.sql.*;
import bourse.protocole.*;
import bourse.sdd.*;
import java.util.*;

public class Requetes extends bourse.reseau.Bd {

    private PlaceDeMarche pdm;
    private Random random;

    Requetes(PlaceDeMarche pdm, boolean verbose) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super(verbose);
        this.pdm = pdm;
        this.random = new Random();
    }
    
// Requ�tes concernant la place de march�
    
    /** D�clare la cr�ation de la place de march� sur la base de donn�es. */
    public final synchronized void declarationPlaceDeMarche(String nom) {
        String hoteLocal = "";
        try {
            hoteLocal = this.pdm.getPdmServeur().getSocketServeur().getInetAddress().getLocalHost().getHostAddress() + ":" + pdm.getPort();
        } catch (java.net.UnknownHostException e) {
            // Le syst�me ne connait pas son propre h�te, ce qui para�t plut�t �trange.
            // Cela n'emp�che pas le fait que ce soit une erreure fatale (une pdm sans
            // ip valide ne pourra jamais �tre contact�e).
            if (verbose) System.err.println("Requetes: L'adresse ip locale ne peut pas �tre d�termin�e.");
            System.exit(1);
        }
        String requete = "INSERT INTO pdms (nom, adresse) VALUES ('" + nom + "', '" + hoteLocal + "');";
        try {
            if (this.requete(requete) == 0) {
                // Aucune ligne a �t� affect�e par la requ�te : rien ne sert de continuer.
                if (verbose) System.err.println("Requetes: impossible d'ajouter la place de march� dans la table pdms.");
                System.exit(1);
            }
        } catch (SQLException e) {
            try {
                requete = "UPDATE pdms SET adresse = '" + hoteLocal + "' WHERE nom = '" + nom + "';";
                this.requete(requete);
            } catch (SQLException ePrime) { if (verbose) System.err.println("Requetes: " + ePrime); }
        }
        if (verbose) System.out.println(requete);
    }

    /** R�cupp�re les informations d'un livre.
     * @return une instance de Livre. Attention : peut retourner null si l'id est
     * inconnu. */
    public Livre getLivre(final int id) {
        Livre livre = null;
        try {
            String requete = "SELECT Categorie, Titre, Auteur, DatePar, Editeur, Format, PrixN, livres.ISBN, Etat, Proprio, PrixAchat FROM livres, items WHERE livres.ISBN = items.ISBN AND ID = '" + id + "';";
            ResultSet r = this.resultat(requete);
            r.next();
            livre = new Livre(r.getString("Titre"), r.getString("Auteur"), Categorie.newCategorieFromBd(r.getString("Categorie")), r.getString("Format"), r.getString("Editeur"), r.getFloat("PrixN"), r.getFloat("Etat"), r.getString("DatePar"), r.getString("ISBN"), id, r.getString("Proprio"), r.getFloat("PrixAchat"));
        } catch (SQLException e) { if (verbose) System.err.println(e); }
        return livre;
    }

    /** Lorsque la place de march� arr�te d'accepter de nouveaux agents, on doit
     * se d�sincrire de la base de donn�es. */
    public final synchronized void suppressionPlaceDeMarche(String nom) {
        try {
            String requete = "DELETE FROM pdms WHERE nom = '" + nom + "'";
            this.requete(requete);
            if (verbose) System.out.println(requete);
            requete = "DELETE FROM items WHERE Prorio = '" + nom + "';";
            this.requete(requete);
            if (verbose) System.out.println(requete);
        } catch (SQLException e) { if (verbose) System.err.println("Requetes: " + e); }
    }

    /** Choisit un livre au hasard dans la table des livres et cr�e un nouvel
     * item correspondant � l'acquisition par la place de march� du livre. */
    public final synchronized Livre creerItem() {
        Livre livre = null;
        try {
            String requete = "SELECT * FROM livres ORDER BY RAND() LIMIT 0, 1;"; // Cette requete ne fonctionne malheureusement pas avec mysql 3.22 :(
            requete = "SELECT FLOOR(RAND() * COUNT(*)) FROM livres;";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            int numeroEnregistrement = 0;
            if (r.next())
                numeroEnregistrement = r.getInt(1);
            requete = "SELECT * FROM livres LIMIT " + numeroEnregistrement + ", 1;";
            r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            if (r.next()) {
                String titre = r.getString("Titre");
                String auteur = r.getString("Auteur");
                Categorie categorie = Categorie.newCategorieFromBd(r.getString("Categorie"));
                String format = r.getString("Format");
                String editeur = r.getString("Editeur");
                float prixNeuf = Math.round(r.getFloat("PrixN") * 100) / (float)100.0;
                float etat = Math.round(random.nextFloat() * 100) / (float)100.0;
                String dateParution = r.getString("DatePar");
                String isbn = r.getString("ISBN");
                float prixAchat = Math.round(etat * prixNeuf * 100) / (float)100.0;
                requete = "INSERT INTO items (ID, ISBN, Etat, Proprio, PrixAchat) VALUES ('', '" + isbn + "', '" + etat + "', '" + pdm.getNom() + "', '" + prixAchat + "');";
                this.requete(requete);
                if (verbose) System.out.println(requete);
                // Essaie de retrouver l'id pr�c�demment ins�r� (last_insert_id :( )
                requete = "SELECT ID from items WHERE ISBN = '" + isbn + "' AND Proprio = '" + pdm.getNom() + "' ORDER BY ID DESC LIMIT 0, 1;";
                r = this.resultat(requete);
                if (verbose) System.out.println(requete);
                if (r.next())
                    livre = new Livre(titre, auteur, categorie, format, editeur, prixNeuf, etat, dateParution, isbn, r.getInt(1), pdm.getNom(), prixAchat);
            }
        } catch (Exception e) { if (verbose) System.err.println(e); }
        return livre;
    }

// Requetes concernant les livres
    
    /** R�cupp�re depuis la base de donn�es le prix d'achat d'un livre, sachant
     * son id. */
    public float getPrixAchat(int id) {
        float prixAchat = 0;
        try {
            String requete = "SELECT PrixAchat FROM items WHERE ID = '" + id + "';";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            if (r.next())
                prixAchat = r.getFloat("PrixAchat");
        } catch (SQLException e) { if (verbose) System.err.println(e); }
        return prixAchat;
    }
    
    public void supprimerDerniereEnchere(Livre item) {
        try {
            String requete = "DELETE * FROM items WHERE id = '" + item.getId() + "';";
            requete(requete);
            if (verbose) System.out.println(requete);
        } catch (SQLException e) { if (verbose) System.err.println(e); }
    }
    
// Requetes concernant l'agent

    /** V�rifie si l'agent est d�j� connu dans la base de donn�es. Pour cela, la
     * m�thode regarde le nom de l'agent gr�ce � la m�thode getNomAgent().
     * Si oui, l'agent r�cupp�rera automatiquement ses valeurs depuis la base. */
    public final synchronized boolean agentPresentDansBaseDeDonnees(Agent agent) {
        boolean existe = false;
        try {
            String requete = "SELECT * FROM agents WHERE nomAgent = '" + agent.getNomAgent() + "';";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            existe = r.next();
            if (existe)
                agent.setAgent(r.getString("nomPDM"), r.getString("nomAgent"), r.getFloat("argent"), new Categorie(r.getString("categorie")));
        } catch (Exception e) { if (verbose) System.err.println(e); }
        return existe;
    }
    
    /** V�rifie que l'agent poss�de bien le livre donn�. */
    public final synchronized boolean agentPossede(Agent agent, Livre livre) {
        boolean possede = false;
        try {
            String requete = "SELECT ID FROM items WHERE Proprio = '" + agent.getNomAgent() + "' AND ID = '" + livre.getId() + "'";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            possede = r.next();
        } catch (Exception e) { if (verbose) System.err.println(e); }
        return possede;
    }

    /** Ins�re ou modifie le tuple contenant l'agent pass� en param�tre et
     * identifi� par sa m�thode getNomAgent() */
    public void inscrireAgent(Agent agent) {
        String requete;
        try {
            requete = "INSERT INTO agents (nomPdm, nomAgent, argent, categorie) VALUES ('"
            + agent.getNomPDM() + "', '"
            + agent.getNomAgent()  + "', '"
            + agent.getArgent() + "', '"
            + agent.getCategorie() + "');";
            this.requete(requete);
            if (verbose) System.out.println(requete);
        } catch (SQLException e) {
            try {
                requete = "UPDATE agents SET nomPdm = '" + agent.getNomPDM() + "' WHERE nomAgent = '" + agent.getNomAgent() + "';";
                this.requete(requete);
                if (verbose) System.out.println(requete);
            } catch (Exception ePrime) { if (verbose) System.err.println(ePrime); }
        }
    }
    
    /** Modifie le champ nomPDM pour d�sinscrire l'agent de la place de march�.
     * L'agent est identifi� dans la table gr�ce � son nom, r�cupp�r� par la 
     * m�thode getNomAgent(). */
    public void desinscrireAgent(Agent agent) {
        try {
            // Si l'agent n'est pas pr�sent dans la base, la requete renverra �chouera, et c'est tr�s bien comme �a ;)
            String requete = "UPDATE agents SET nomPDM = 'HOME' WHERE nomAgent = '" + agent.getNomAgent() + "';";
            this.requete(requete);
            if (verbose) System.out.println(requete);
        } catch (SQLException e) { System.err.println(e); }
    }
    
    /** Effectue la transaction effective d'un livre entre deux entit�s. */
    public final synchronized boolean transaction(int id, String vendeur, String acheteur, float prixVente) {
        boolean succes = false;
        try {
            // Changement de proprietaire
            String requete = "UPDATE items SET Proprio = '" + acheteur + "', PrixAchat = '" + prixVente + "' WHERE ID = '" + id + "' AND Proprio = '" + vendeur + "';";
            succes = (this.requete(requete) == 1);
            if (verbose) System.out.println(requete);
            if (succes) {
                // Retrait et d�bit des sommes d'argents sur les agents. Remarque : si le vendeur est une place de march�, la requ�te ne modifiera rien.
                requete = "UPDATE agents SET argent = argent + " + prixVente + " WHERE nomAgent = '" + vendeur +"';";
                this.requete(requete);
                if (verbose) System.out.println(requete);
                requete = "UPDATE agents SET argent = argent - " + prixVente + " WHERE nomAgent = '" + acheteur + "';";
                this.requete(requete);
                if (verbose) System.out.println(requete);
            }
        } catch (SQLException e) { if (verbose) System.err.println(e); }
        return succes;
    }

    /** R�cupp�re l'adresse d'une place de march�.
     * @return l'adresse de cette place de march�; ou une chaine vide si la place
     * de march� n'existe pas dans la table. */
    public final synchronized String getAdressePdm(String nomPdm) {
        String resultat = "";
        try {
            String requete = "SELECT adresse FROM pdms WHERE nom = '" + nomPdm + "';";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            if (r.next())
                resultat = r.getString("adresse");
        } catch (Exception e) { if (verbose) System.err.println(e); }
        return resultat;
    }
    
    /** R�cupp�re les adresses de toutes les places de march� d�clar�es dans la
     * base de donn�es.
     * @return une LinkedList remplie de PDMPro. */
    public final synchronized LinkedList getAdressesPdm() {
        LinkedList liste = new LinkedList();
        try {
            String requete = "SELECT nom, adresse FROM pdms ORDER BY nom ASC;";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            while (r.next())
                liste.add(new PDMPro(r.getString("nom"), r.getString("adresse")));
        } catch (SQLException e) { if (verbose) System.err.println(e); }
        return liste;
    }

    /** @return le solde de d�part d'un agent, calcul bas� sur la moyenne du prix
     * neuf des livres du march� multipli� par un nombre de livres th�oriquement
     * acqu�ris par chaque agent. */
    public float soldeDeDepart(){
        float argentInitial = 1;
        try {
            String requete = "SELECT AVG(PrixN) FROM livres;";
            ResultSet r = this.resultat(requete);
            if (verbose) System.out.println(requete);
            if (r.next())
                argentInitial = r.getFloat(1);
            argentInitial *= bourse.protocole.Protocole.LIVRES_NEUFS_PAR_AGENTS;
        } catch (Exception e) { if (verbose) System.err.println(e); }
        return argentInitial;
    }

    public static final void main(String args[]) {
        bourse.placeDeMarche.Requetes r = null;
        try {
            r = new bourse.placeDeMarche.Requetes(null, true);
            System.out.println(r.getLivre(25));
            System.out.println(r.creerItem());
        } catch (Exception e) { System.out.println(e); }
    }
}
