package src.Testing;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;

public class Testing {

    public static void main(String argv[]) {
        IndexingManager IM= IndexingManager.getInstance();
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

IM.maintenancethread();
        System.out.println(Thread.currentThread()+"This is main");
        //Database_Utility utility=Database_Utility.getInstance();

    }
}


























































