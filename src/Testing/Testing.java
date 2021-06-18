package src.Testing;

import jdk.internal.org.xml.sax.SAXException;
import org.w3c.dom.*;
import src.main.Database_Utility;
import src.main.IndexingManager;
import src.main.Cryptography;
import src.main.ObjReturn;

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


    public static void main(String argv[]) {

        //Following block will test adding of an entry.

        // {

       IndexingManager IM= IndexingManager.getInstance();

      /*   Cryptography S2 = Cryptography.getInstance();
       KeyStore k = S2.getKeyStore();
       Certificate c1 = null;
       try {
           c1 =  k.getCertificate("Certificate");}
        catch (KeyStoreException e) {
           e.printStackTrace();
        }*/

        //IM.addIndex("mD2051C7A9CD59A1BE822F699267C42DE64C0904","peer","5000",2,0,false,"nick",0,"1621911095568",c1);
        //IM.addIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904","network","4000",1,2,false,"harry",2,"1621911095568",c1);
        //IM.addIndex("FD2051C7A9CD59A1BE822F699267C42DE64C0904","manager","3000",1,2,true,"sidhu",0,"1621911095568",c1);

        //  }

         /* Database_Utility u=Database_Utility.getInstance();
          ObjReturn obj=new ObjReturn();
          obj=u.search_entry("pDD2051C7A9CD59A1BE822F699267C42DE64C0904",6);
          System.out.println(obj.getValue1());*/



        // Following block will test searching of an entry.
        //  {

//        File f=IM.searchIndex("pDD2051C7A9CD59A1BE822F699267C42DE64C0904",6);
//        System.out.println(f.getName());

        //  }

        // Following block will test updation of an entry.
        //  {

        //IM.updateIndex("pDD2051C7A9CD59A1BE822F699267C42DE64C0904",6);

        //}

        // Following block will test deletion of an entry.
        //  {

        //IM.deleteIndex("DD2051C7A9CD59A1BE822F699267C42DE64C0904",2);

        //}


        //This block will check transfer of entry to purge table.


        //Error of braces
        //Following thread will request Routing manager every 30 minutes to ascertain for which self is root or not.
        // {

        //IM.queryForRoutingManager();

        //  }

        //{

            // Minor ERROR

        /* String filepath="ResponseToIndexM.xml";
       IM.transfertopurge(new File(filepath));
*/
        // }

        /*Certificate c=IM.fetchuserCerti("hardy");
        System.out.println(c.getPublicKey());*/

        //Following code will check if table of a layerid exists or not
        // {
        /*boolean b=IM.checkTable(1);
        System.out.println(b);
        */
        // }

        //ERROR

        //Following thread will delete entries whose timer has expired.
        // {

       // IM.maintenancethread();

        //  }

        //Following thread will delete entries from purge table every 90 minutes.
        // {

        // IM.maintenancethread2();

        //  }




//IM.resultForIndexingManager();
        //System.out.println(Thread.currentThread()+"This is main");
        //Database_Utility utility=Database_Utility.getInstance();

        //String filepath="Table1_RootNodeCheck.xml";
        //File f=responseForIndexingManager(filepath);
    }


    }
































































