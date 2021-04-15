package src.Testing;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import src.main.IndexingManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class Testing {
    /*public static void main(String[] args) {
        IndexingManager IM=IndexingManager.getInstance();
        SignatureVerif S2 = SignatureVerif.getInstance();
        KeyStore k = S2.getKeyStore();
        Certificate c1 = null;
        try {
            c1 =  k.getCertificate("Certificate");
            PublicKey Key= c1.getPublicKey();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        //Database_Utility utility1 = Database_Utility.getInstance();
        //utility1.add_entry("HIIiiiiiiiii","hooo", (long) 1600,6,7,true ,"Mohsin",System.currentTimeMillis(), c1);
        IM.addIndex("index1","hooo", (long) 1600,6,7,true ,"Mohsin",13,System.currentTimeMillis(), c1);
    }*/

    public static final String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\xmlfile.xml";
    public static void main(String argv[]) {
        IndexingManager IM= IndexingManager.getInstance();
        IM.makeXML("hiii",2,"hoooo",System.currentTimeMillis(),2,2,true,"ccccc",System.currentTimeMillis());

       /* try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("RootNodes");
            document.appendChild(root);

            // root1 element
            Element root1 = document.createElement("Root1");

            root.appendChild(root1);

            // set an attribute to staff element
            Attr attr = document.createAttribute("HashId1");
            attr.setValue("10");
            root1.setAttributeNode(attr);


            // root2 element
            Element root2 = document.createElement("Root2");

            root.appendChild(root2);

            // set an attribute to staff element
            Attr attr1 = document.createAttribute("HashId2");
            attr1.setValue("10");
            root2.setAttributeNode(attr1);

            //you can also use staff.setAttribute("id", "1") for this

            // create the xml file

            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }*/
    }
}


























































