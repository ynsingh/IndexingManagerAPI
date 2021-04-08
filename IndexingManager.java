import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

//This is main class of IndexingManager API. Central code will interact with this class for
// adding,deleting,updating and searching an index.It has methods for doing those tasks.

public class IndexingManager {
    private static IndexingManager indexManager;
    private static String origkey;
    private static String origvalue;
    private static Long origtimer;
    private static int origtotalCopies;
    private static int origcopyNum;
    private static boolean origtimerType;
    private static String origuserId;
    private static int origLayerId;
    private static Long origTime;
    private static java.security.cert.Certificate origCerti;
    private Connection conn;
    private Database_Utility utility;


    
    /*public void readXML(File InputFile){
        //creating a constructor of file class and parsing an XML file  
        
        File file = InputFile;
        
//Creating an instance of factory that gives a document builder  
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        
// Creating an instance of builder to parse the specified xml file  
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        
        Document doc = null;
        
            doc = (Document) db.parse(file);
         
        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        NodeList nodeList = doc.getElementsByTagName("Key");
        
// nodeList is not iterable, so we are using for loop  
        for (int itr = 0; itr < nodeList.getLength(); itr++)
        {
            Node node = nodeList.item(itr);
            System.out.println("\nNode Name :" + node.getNodeName());
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                Element eElement = (Element) node;
                System.out.println("key "+ eElement.getElementsByTagName("key").item(0).getTextContent());
                System.out.println("value "+ eElement.getElementsByTagName("value").item(0).getTextContent());
                System.out.println("timer "+ eElement.getElementsByTagName("timer").item(0).getTextContent());
                System.out.println("totalCopies "+ eElement.getElementsByTagName("totalCopies ").item(0).getTextContent());
                System.out.println("copyNum "+ eElement.getElementsByTagName("copyNum ").item(0).getTextContent());
                System.out.println("timerType "+ eElement.getElementsByTagName("timerType").item(0).getTextContent());
                System.out.println("userId "+ eElement.getElementsByTagName("userId").item(0).getTextContent());
                System.out.println("time "+ eElement.getElementsByTagName("time").item(0).getTextContent());
                System.out.println("Certificate "+ eElement.getElementsByTagName("Certificate").item(0).getTextContent());
            }
        }
    }   
catch (Exception e)
    {
        e.printStackTrace();
    }

}*/


    private boolean checkTable(int layerId)

    {
        boolean isAvailable = false;
        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                String ld = rs.getString("TABLE_NAME");
                String intValue = ld.replaceAll("[^0-9]", "");
                System.out.println(intValue);
                int v = Integer.parseInt(intValue);
                if (v == layerId) {
                    isAvailable = true;
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAvailable;
    }


    // This method is used to add index entry to database. Central code will call this method and pass required arguments.

    public void addIndex(String key, String value, Long timer, int totalCopies, int copyNum, boolean timerType, String userId, int layerID, Long time, java.security.cert.Certificate c) {
        origkey = key;
        origvalue = value;
        origtimer = timer;
        origtotalCopies = totalCopies;
        origcopyNum = copyNum;
        origtimerType = timerType;
        origuserId = userId;
        origLayerId = layerID;
        origTime = time;
        origCerti = c;


        //Verifying Digital Signature using Certificate

        Verif v = new Verif();
        boolean b1 = v.Verify_Digital_Signature(origCerti, origvalue);
        if (b1) {
            boolean b2 = checkTable(origLayerId);
            if (b2) {
                utility.add_entry(origLayerId,origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                System.out.println("Entry added");

            } else {
                utility.createtable(origLayerId);
                utility.add_entry(origLayerId,origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
            }

        } else {
            System.out.println("Signature not verified");
        }


           /*PreparedStatement stmt = null;
           try {

    //Retrieving Certificate from Base Layer DHT

               stmt = conn.prepareStatement("select Certificate from keyvalue1 where userId=?");

               stmt.setString(1,origuserId );

                ResultSet rs = null;

               rs = stmt.executeQuery();
               while(rs.next()){

                  byte[] t =rs.getBytes("Certificate");
                   CertificateFactory cf = CertificateFactory.getInstance("X.509");
                   Certificate cert = cf.generateCertificate(new ByteArrayInputStream(t));

                   System.out.println("Certificate Retrieved");


     // Retrieving Public Key from  Certificate

                   PublicKey pub = cert.getPublicKey();
                   System.out.println(pub);
                   System.out.println(" Public Key Retrieved");
           }

           } catch (SQLException e) {
               e.printStackTrace();
           } catch (CertificateException e) {
               e.printStackTrace();
           }*/

    }

    // This method is used to delete index entry to database.
   
 /*public void deleteEntry( String Key){

 utility.delete_entry(Key);


  }*/


    // This method is used to update index entry to database.

    public void updateEntry(String Key) {

        utility.update_entry(Key);

    }

    // This method is used to search index entry using key in database.

    public ObjReturn searchEntry(String Key) {

        ObjReturn obj = utility.search_entry(Key);
        return obj;
    }

//    This method is used to delete entries as per Timer Type and timer associated with it.

    public void maintenancethread() {
        PreparedStatement pst;
        int rowid;
        Long timer = Long.valueOf(0);
        long time = 0;
        String key = null;
        boolean timerType;
        try {
            pst = conn.prepareStatement("select * from keyvalue1");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                timerType = rs.getBoolean("timerType");
                rowid = rs.getRow();
                System.out.println(rowid);
                System.out.println(timerType);


                if (timerType) {
                    pst = conn.prepareStatement("SELECT timer,time,Key FROM keyvalue1 where rowid=?");
                    pst.setInt(1, rowid);
                    ResultSet rs2 = pst.executeQuery();
                    timer = rs2.getLong("timer");
                    time = rs2.getLong("time");
                    key = rs2.getString("Key");
                    rs2.close();
                    System.out.println(timer);
                    System.out.println(time);
                    System.out.println(key);


                    if (!(timer == 0)) {
                        System.out.println(System.currentTimeMillis() - time > timer);
                        if (System.currentTimeMillis() - time > timer) {
                            utility.delete_entry(rowid, conn);

                        }

                    }
                } else {


                }

            }
            pst.close();
            rs.close();
            conn.close();


        } catch (SQLException ex) {
            Logger.getLogger(IndexingManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


//    Constructor function of class.

    private IndexingManager() {
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        utility = Database_Utility.getInstance();
        conn = utility.getConnection();
    }
//  Creating Singleton object of IndexingManager class.

    public static synchronized IndexingManager getInstance() {
        if (indexManager == null) {
            indexManager = new IndexingManager();
            return indexManager;
        } else {
            return indexManager;
        }
    }


//    public static void main(String[] args) {
//        SignatureVerif S2 = SignatureVerif.getInstance();
//        KeyStore k = S2.getKeyStore();
//        Certificate c1 = null;
//        try {
//            c1 =  k.getCertificate("Certificate");
//            PublicKey Key= c1.getPublicKey();
//
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        }
//
//        addIndex("done","hooo", (long) 1600,6,7,true ,"Mohsin",5,System.currentTimeMillis(), c1);
//        //maintenancethread();
//    }
//

}
