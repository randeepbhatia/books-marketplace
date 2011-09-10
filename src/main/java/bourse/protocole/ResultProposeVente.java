/*
 * ResultProposeVente.java
 *
 * Created on 16 janvier 2004, 10:35
 */

package bourse.protocole;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
/**
 *
 * @author  pechot
 */
public class ResultProposeVente extends bourse.protocole.Protocole {
    
    private int idLivre;
    public int getId() { return idLivre; }
    
    /** Creates a new instance of ResultProposeVente */
    public ResultProposeVente(int idLivre) {
        super(new TypeMessage(TypeMessage.TM_RESULT_PROPOSE_VENTE));
        this.idLivre=idLivre;
        
    }
    
     public ResultProposeVente(Element type) {
        super(new TypeMessage(TypeMessage.TM_RESULT_PROPOSE_VENTE));
        this.toClass(type);
    }
    
    
    protected void toClass(Element type) {
        NodeList noeuds = type.getChildNodes();
        Element livre = (Element)noeuds.item(0);
        this.idLivre = Integer.valueOf(livre.getAttribute("ID")).intValue();
        
    }
    
    public Document toDOM() {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
            Element root = document.createElement("MSG");
            document.appendChild(root);
            Element type = document.createElement("RESULTPROPOSEVENTE");
            root.appendChild(type);
            Element livre= document.createElement("LIVRE");
            Attr id = document.createAttribute("ID");
            id.setValue(String.valueOf(idLivre));
            livre.setAttributeNode(id);
            type.appendChild(livre);
            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return document;
    }
    
    public String toXML() {
         return super.toXML(this.toDOM());
    }
    
     public static void main(String args[]) {
         int i=12;
         
        String p=new ResultProposeVente(i).toXML();
        System.out.println(p);
         Protocole message = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        // D'apr�s le tutorial JAXP, ces variables fix�es � true permettent �
        // l'application de se concentrer sur l'analyse s�mantique.
        factory.setCoalescing(true);
        factory.setExpandEntityReferences(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        try {
            // factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            
            // La d�finition de ErrorHandler est inspir�e de
            // http://java.sun.com/j2ee/1.4/docs/tutorial/doc/JAXPDOM3.html#wp64106
            builder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                // ignore fatal errors (an exception is guaranteed)
                public void fatalError(SAXParseException exception) throws SAXException { }
                // treat validation errors as fatal
                public void error(SAXParseException e) throws SAXParseException { throw e; }
                // dump warnings too
                public void warning(SAXParseException err) throws SAXParseException {
                    System.out.println("** Warning"
                    + ", line " + err.getLineNumber()
                    + ", uri " + err.getSystemId());
                    System.out.println("   " + err.getMessage());
                }
            }
            );
            Document document = builder.parse(new ByteArrayInputStream(p.getBytes("UTF-8")), Protocole.BASE_DTD);
            Element root = document.getDocumentElement();
            NodeList noeuds = root.getChildNodes();
            Element typeDOM = (Element)noeuds.item(0);
            Element typeDOME = (Element)typeDOM;
            p= new ResultProposeVente(typeDOME).toXML();
            System.out.println(p);   
        } catch (Exception e) {
            System.err.print("Protocole: ");
            e.printStackTrace(System.err);
        }
     }
}
