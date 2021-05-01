package src.Testing;

import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.*;
import src.main.Database_Utility;
import src.main.IndexingManager;
import src.main.SignatureVerif;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;

public class Testing {

   // private static DocumentBuilderFactory builderFactory;
   // private static String indexFileName;

    public static void main(String argv[]) {
        /*maintenance thread 2
                static remove
IIT Kgp_MTP_Thesis template(1)*/

            //  IndexingManager IM= IndexingManager.getInstance();


              /*ArrayList<File> B=IM.resultForIndexingManager();
        System.out.println(B.get(0).getName());
        System.out.println(B.get(1).getName());*/

//
//        SignatureVerif S2 = SignatureVerif.getInstance();
//        KeyStore k = S2.getKeyStore();
//        Certificate c1 = null;
//        try {
//            c1 =  k.getCertificate("Certificate");}
//        catch (KeyStoreException e) {
//            e.printStackTrace();
//        }
//       File f=IM.XMLforRoot(s[0],"HIII","helooo",2,2, (long) 34567890,true,"hooooo",System.currentTimeMillis(),c1);

//IM.resultForIndexingManager();
        //System.out.println(Thread.currentThread()+"This is main");
        //Database_Utility utility=Database_Utility.getInstance();

        //String filepath="Table1_RootNodeCheck.xml";
        //File f=responseForIndexingManager(filepath);
    }
        /*public static File responseForIndexingManager (String indexFileName){
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = null;
            String selfNodeID = null;
            try {
                documentBuilder = builderFactory.newDocumentBuilder();
                Document doc = documentBuilder.parse(new File(indexFileName));
                doc.getDocumentElement().normalize();
                String rootElement = doc.getDocumentElement().getNodeName();
                String layerIDS = doc.getDocumentElement().getAttribute("LayerID");
                int layerID = Integer.parseInt(layerIDS);
                NodeList nodeList1 = doc.getElementsByTagName("DATA");
                for (int i = 0; i < nodeList1.getLength(); i++) {
                    Node node = nodeList1.item(i);

                    if (node.getNodeType() == node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String index = node.getAttributes().getNamedItem("INDEX").getNodeValue();

                        //Get value of all sub-Elements
                        String key = element.getElementsByTagName("KEY").item(0).getTextContent();
                        element.getElementsByTagName("HASHID").item(0).setTextContent("1252623738");
                    }
                }
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);
                StreamResult streamResult = new StreamResult(new File("ResponseToIndexM.xml"));
                transformer.transform(domSource, streamResult);
                System.out.println("ResponseToIndexM.xml" + "file updated");
            } catch (ParserConfigurationException | IOException | TransformerException e) {

            } catch (org.xml.sax.SAXException e) {
                e.printStackTrace();
            }
            return new File("ResponseToIndexM.xml");

        }*/

    }
































































