package bourse.agent;

/*
 * Visualisation.java
 *
 * Created on 28 janvier 2004, 19:49
 */

/**
 *
 * @author  eric
 */
public class Visualisation extends javax.swing.JFrame {
    
    /** Creates new form Visualisation */
    public Visualisation() {
        initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nom = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        categorie = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        solde = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        environnement = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        protocole = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        resultat = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        hote = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        etat = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        action = new javax.swing.JTextField();

        setTitle("Agent");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        getAccessibleContext().setAccessibleName("test");
        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 400));
        jPanel8.setLayout(new java.awt.GridLayout(3, 2));

        jLabel1.setText("Nom :");
        jPanel8.add(jLabel1);

        nom.setEditable(false);
        jPanel8.add(nom);
        nom.getAccessibleContext().setAccessibleName("nom");

        jLabel6.setText("Categorie :");
        jPanel8.add(jLabel6);

        categorie.setEditable(false);
        jPanel8.add(categorie);

        jLabel5.setText("Solde :");
        jPanel8.add(jLabel5);

        solde.setEditable(false);
        jPanel8.add(solde);

        jPanel2.add(jPanel8);

        jTabbedPane1.addTab("Agent", jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(3, 1));

        jScrollPane1.setBorder(new javax.swing.border.TitledBorder("Places de march\u00e9"));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nom", "Adresse", "Active", "Visitée", "Enchères gérées", "Numero du tour"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel3.add(jScrollPane1);

        jScrollPane4.setBorder(new javax.swing.border.TitledBorder("Agents"));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nom", "Catégorie", "Présent", "Fréquence catégorie"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(jTable2);

        jPanel3.add(jScrollPane4);

        jScrollPane5.setBorder(new javax.swing.border.TitledBorder("Possessions"));
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Id", "Propriétaire", "Catégorie", "Prix", "Etat", "Prix d'achat", "Titre", "Auteur", "Date de parution", "Editeur", "Format", "ISBN"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(jTable3);

        jPanel3.add(jScrollPane5);

        jTabbedPane1.addTab("Memoire", jPanel3);

        jPanel5.setLayout(new java.awt.BorderLayout());

        environnement.setEditable(false);
        jScrollPane2.setViewportView(environnement);

        jPanel5.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Environnement", jPanel5);

        jPanel6.setLayout(new java.awt.BorderLayout());

        protocole.setLayout(new java.awt.GridLayout(0, 1));

        protocole.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane3.setViewportView(protocole);

        jPanel6.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jLabel7.setForeground(java.awt.Color.blue);
        jLabel7.setText("message re\u00e7u");
        jPanel7.add(jLabel7);

        jLabel8.setForeground(java.awt.Color.red);
        jLabel8.setText("message envoy\u00e9");
        jPanel7.add(jLabel8);

        jPanel6.add(jPanel7, java.awt.BorderLayout.NORTH);

        jTabbedPane1.addTab("Protocole", jPanel6);

        jPanel9.setLayout(new java.awt.BorderLayout());

        jScrollPane6.setViewportView(resultat);

        jPanel9.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("R\u00e9sultats", jPanel9);

        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel3.setText("Hote :");
        jPanel4.add(jLabel3);

        hote.setColumns(10);
        hote.setEditable(false);
        jPanel4.add(hote);

        jLabel2.setText("Etat :");
        jPanel4.add(jLabel2);

        etat.setColumns(10);
        etat.setEditable(false);
        jPanel4.add(etat);

        jLabel4.setText("Action :");
        jPanel4.add(jLabel4);

        action.setColumns(10);
        action.setEditable(false);
        jPanel4.add(action);

        getContentPane().add(jPanel4, java.awt.BorderLayout.NORTH);

        pack();
    }//GEN-END:initComponents
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new Visualisation().show();
    }
    
    /** Accéder au tableau d'affichage de la liste des pdms de la mémoire */
    public javax.swing.JTable getTableauPdmMemoire() { return this.jTable1; }
    /** Accéder au tableau d'affichage de la liste des agents de la mémoire */
    public javax.swing.JTable getTableauAgentMemoire() { return this.jTable2; }
    /** Accéder au tableau d'affichage de la liste des possessions de la mémoire */
    public javax.swing.JTable getTableauPossessionMemoire() { return this.jTable3; }
    /** Accéder au conteneur de la liste des pdms de la mémoire.  */
    public javax.swing.JScrollPane getJScrollPanePdmMemoire() { return this.jScrollPane1; }
    /** Accéder au conteneur de la liste des agents de la mémoire.  */
    public javax.swing.JScrollPane getJScrollPaneAgentMemoire() { return this.jScrollPane4; }
    /** Accéder au conteneur de la liste des possessions de la mémoire.  */
    public javax.swing.JScrollPane getJScrollPanePossessionMemoire() { return this.jScrollPane5; }
    /** Modifie le nom. */
    public void setNom(String nom) { this.nom.setText(nom); }
    /** Modifie le solde. */
    public void setSolde(String solde) { this.solde.setText(solde); }
    /** Modifie la catégorie. */
    public void setCategorie(String categorie) { this.categorie.setText(categorie); }
    /** Modifie le nom de l'hôte. */
    public void setHote(String hote) { this.hote.setText(hote); }
    /** Modifie l'état. */
    public void setEtat(String etat) { this.etat.setText(etat); }
    /** Modifie l'action. */
    public void setAction(String action) { this.action.setText(action); }
    /** Modifie l'état de l'environnement. */
    public void setEnvironnement(String env) { this.environnement.setText(env); }
    /** Ajout d'un message entrant à la fenêtre du protocole. */
    public void addInputMessage(String m) {
        javax.swing.JLabel l = new javax.swing.JLabel(m + "\n");
        l.setForeground(java.awt.Color.BLUE);
        this.protocole.add(l);
        this.protocole.validate();
    }
    /** Ajout d'un message sortant à la fenêtre de protocole. */
    public void addOutputMessage(String m) {
        javax.swing.JLabel l = new javax.swing.JLabel(m + "\n");
        l.setForeground(java.awt.Color.RED);
        this.protocole.add(l);
        this.protocole.validate();
    }
    /** Affichage des résultats. */
    public void setResultat(String resultat) { this.resultat.setText(resultat); }    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField action;
    private javax.swing.JTextField categorie;
    private javax.swing.JTextArea environnement;
    private javax.swing.JTextField etat;
    private javax.swing.JTextField hote;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField nom;
    private javax.swing.JPanel protocole;
    private javax.swing.JTextArea resultat;
    private javax.swing.JTextField solde;
    // End of variables declaration//GEN-END:variables
    
}
