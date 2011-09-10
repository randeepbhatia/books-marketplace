package bourse.placeDeMarche;

import java.util.LinkedList;
import java.util.ListIterator;
import java.io.IOException;

/** G�re l'enregistrement et le d�part des agents. */
public class SalleDesVentes {
    
    private LinkedList agentsIdentifies;
    private PlaceDeMarche placeDeMarche;
    
    public SalleDesVentes (PlaceDeMarche placeDeMarche) {
        this.agentsIdentifies = new LinkedList();
        this.placeDeMarche = placeDeMarche;
    }
    
    /** Retourne la connexion de l'agent identifi� par le nom de l'agent. */
    public ConnexionAgent getConnexionAgent(String nomAgent) {
        ConnexionAgent agent = null;
        ListIterator i = agentsIdentifies.listIterator();
        while (i.hasNext()) {
            agent = (ConnexionAgent)i.next();
            if (agent.getAgent().getNomAgent().equalsIgnoreCase(nomAgent))
                return agent;
        }
        return null;
    }

    /** Supprime l'agent de la salle des ventes. */
    public void supprimerAgent(ConnexionAgent agent) {
        this.agentsIdentifies.remove(agent);
    }
    
    /** Supprimer tous les agents de la salle des ventes. */
    public void supprimerTousLesAgents() {
        ListIterator i = agentsIdentifies.listIterator();
        while (i.hasNext())
            supprimerAgent((ConnexionAgent)i.next());
    }
    
    /** Indentifie l'agent et r�veille le commissaire priseur afin qu'il d�marre
     * une ench�re si le nombre d'agents identifi�s n'�tait pas suffisant jusqu'�
     * maintenant. */
    public synchronized void identifierAgent(ConnexionAgent agent) {
        this.agentsIdentifies.add(agent);
        placeDeMarche.getCommissairePriseur().reveilleToi();
    }
    
    /** Lib�re tous les agents bloqu�s */
    public synchronized void libererAgents() {
        ListIterator parcours = this.agentsIdentifies.listIterator();
        while (parcours.hasNext())
            ((ConnexionAgent)parcours.next()).getAgent().setBloque(false);
    }
    
    /** @return true si le nombre d'agents identifies est sup�rieur ou �gal � 3. */
    public synchronized boolean plusDeTroisAgentsIdentifies() { return agentsIdentifies.size() >= 3; }
    
    /** @return Vrai si au moins 3 agents autre que l'agent donn� en param�tre
     * sont identifi�s sur la place de march�.
     */
    public synchronized boolean plusDeTroisAgentsIdentifies(Agent agentVendeur) {
        int borne = 3;
        if (estIdentifie(agentVendeur))
            borne = 4; 
        return (agentsIdentifies.size() >= borne);
    }
    
    /** D�termine si un agent est identifi� dans la salle des ventes. Cette m�thode
     * est une approximation de r�sultat sur la vraie structure de donn�es stockant
     * les agents connect�s car elle ne compare l'existence que sur le nom de l'agent
     * alors que le vrai test inclue aussi un num�ro de port. Cependant, on s'est
     * assur� lors de l'enregistrement de l'agent que la connexion vers un nom d'agent
     * est unique.
     * @return true si l'agent est identifi�, false sinon.
     */
    public boolean estIdentifie(String nomAgent) {
        ListIterator parcours = this.agentsIdentifies.listIterator();
        boolean trouve = false;
        while (parcours.hasNext() && !trouve)
            trouve = ((ConnexionAgent)parcours.next()).getAgent().getNomAgent().equalsIgnoreCase(nomAgent);
        return trouve;
    }
    
    /** D�termine si un agent est identifi� dans la salle des ventes. Utilise la
     * m�thode bourse.placeDeMarche.Agent.equals(Agent) qui compare � la fois le
     * nom de l'agent et son port de connexion.
     * @return true si l'agent est identifi�, false sinon.
     */  
    public synchronized boolean estIdentifie(Agent agent) {
        ListIterator parcours = this.agentsIdentifies.listIterator();
        boolean trouve = false;
        while (parcours.hasNext() && !trouve)
            trouve = ((ConnexionAgent)parcours.next()).getAgent().equals(agent);
        return trouve;
    }
    
    /** D�termine si une connexion identifi�e par son port communique avec un agent
     * qui est identifi�.
     */
    public boolean estIdentifie(int port) {
        ListIterator parcours = agentsIdentifies.listIterator();
        boolean trouve = false;
        while (parcours.hasNext() && !trouve)
            trouve = ((ConnexionAgent)parcours.next()).getPort() == port;
        return trouve;
    }

    /** Envoie un message � tous les agents identifi�s dans la salle des ventes. */
    public synchronized void envoyerIdentifies(String message) {
        ListIterator parcours = agentsIdentifies.listIterator();
        while (parcours.hasNext())
            try { ((ConnexionAgent)parcours.next()).ecrire(message); } catch (java.io.IOException e) { e.printStackTrace(System.err); }
    }
    
    /** R�cupp�re la liste des agents identifi�s � la place de march�. Utile notamment
     * pour g�nerer une requ�te contenant la liste des agents connect�s.
     * @return une liste cha�n�e remplie de cha�nes de caract�res et repr�sentant
     * les noms des agents. */
    public synchronized LinkedList agentsToListeDeNoms() {
        LinkedList resultat = new LinkedList();
        ListIterator iterateur = agentsIdentifies.listIterator();
        while (iterateur.hasNext())
            resultat.add(((ConnexionAgent)iterateur.next()).getAgent().getNomAgent());
        return resultat;
    }

    public String toHtml() {
        String sortie = "<h2>SalleDesVentes</h2>\n<h3>Agents identifi&eacute;s</h3>\nIl y a " + agentsIdentifies.size() + " agents identifi&eacute;s : <ol>\n";
        if (agentsIdentifies.size() > 0) {
            ListIterator i = agentsIdentifies.listIterator();
            while (i.hasNext())
                sortie += "<li>" + ((ConnexionAgent)i.next()).toHtml() + "</li>\n";
        }
        return sortie;
    }
}