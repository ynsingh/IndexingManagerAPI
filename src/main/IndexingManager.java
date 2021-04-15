package src.main;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static src.Testing.Testing.xmlFilePath;

//This is main class of IndexingManager API. Glue code will interact with this class for
// adding,deleting,updating and searching an index.It has methods for doing these tasks.

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
    private IndexingManagerBuffer IMbuffer;

    //This method will check whether table for layerID requested exists or not.Returns true if table exists.


    private boolean checkTable(int layerId)

    {
        boolean isAvailable = false;
        try {

            //This statement will fetch all tables available in database.

            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {

                String ld = rs.getString("TABLE_NAME");

                //This statement will extract digits from table names.

                String intValue = ld.replaceAll("[^0-9]", "");
                int v = Integer.parseInt(intValue);

                //This statement will compare layerid with digits of table names.

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

    private boolean checkiforiginal(int copynum){
        boolean isAvailable=false;
        if(copynum==0){
            isAvailable=true;
        }
        return isAvailable;
    }


    // This method is used to add an index entry to database. Glue code will call this method and pass required arguments.

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


        //Verifying Digital Signature of Value using Certificate

        Verif v = new Verif();

        boolean b1 = v.Verify_Digital_Signature(origCerti, origvalue);

        // If signature is verified
        if (b1) {

            // This statement will check whether table exists or not.

            boolean b2 = checkTable(origLayerId);
            if (b2) {

                //If table exists

                boolean b3=checkiforiginal(copyNum);
                if(!b3) {
                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                    System.out.println("Entry added");
                }
                else{
                    String s1="Copy1";
                    String concat1=origkey.concat(s1);

                    MessageDigest messageDigest = null;
                    try {
                        messageDigest = MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    messageDigest.update(concat1.getBytes());
                    String hashId1 = new String(messageDigest.digest());
                    System.out.println(hashId1);
                   // int hashId1= concat1.hashCode();
                    String s2="Copy2";
                    String concat2=origkey.concat(s2);
                    MessageDigest messageDigest1 = null;
                    try {
                        messageDigest1 = MessageDigest.getInstance("SHA-1");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    messageDigest.update(concat2.getBytes());
                    String hashId2 = new String(messageDigest.digest());
                    System.out.println(hashId2);
                    //int hashId2=concat2.hashCode();

                }
            } else {

                //If table doesn't exist then create table and then add entry.

                utility.createtable(origLayerId);

                boolean b4=checkiforiginal(copyNum);
                if(!b4) {

                utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
            }
            }

        } else {
            System.out.println("Signature not verified");
        }

    }


    // This method is used to delete index entry to database.
   
 /*public void deleteEntry( String Key){

 utility.delete_entry(Key);


  }*/

    // This method is used to update an index entry in database.User will pass Value which is to be updated and layer id to which user belongs.

    public void updateEntry(String Key,int layerID) {

        utility.update_entry(Key,layerID);

    }

    // This method is used to search index entry using key and layerID in database. Also it will put details of object in an output buffer as XML file.

    public File searchEntry(String Key, int layerID) {

        ObjReturn obj = utility.search_entry(Key, layerID);
        boolean b=obj.timerType1;

        if(!b)
        {
            updateEntry(Key,layerID);
        }
        File f=makeXML(Key,layerID,obj.value1,obj.time1,obj.totalCopies1,obj.copyNum1,obj.timerType1,obj.userId,obj.time);
        IMbuffer.addToIMOutputBuffer(f);
        return f;

    }

    public File makeXML(String key, int layerID, String value1, Long time1, int totalCopies1, int copyNum1, boolean timerType1, String userId, Long time){
        String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\return.xml";
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element key1 = document.createElement("Search_Result_for_key");
            document.appendChild(key1);
            key1.setAttribute("Key",key);

            // employee element
            Element layerid = document.createElement("layerid");

            key1.appendChild(layerid);
            layerid.setAttribute("Id", String.valueOf(layerID));

            // firstname element
            Element Value = document.createElement("Value");
            Value.appendChild(document.createTextNode(value1));
            layerid.appendChild(Value);

            // second element
            Element timer = document.createElement("timer");
            timer.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(timer);

            // third element
            Element totcopies = document.createElement("totcopies");
            totcopies.appendChild(document.createTextNode(String.valueOf(totalCopies1)));
            layerid.appendChild(totcopies);

            // fourth element
            Element copynum= document.createElement("copynum");
            copynum.appendChild(document.createTextNode(String.valueOf(copyNum1)));
            layerid.appendChild(copynum);

            // fifth element
            Element timertype= document.createElement("timertype");
            timertype.appendChild(document.createTextNode(String.valueOf(timerType1)));
            layerid.appendChild(timertype);

            // sixth element
            Element userid= document.createElement("userid");
            userid.appendChild(document.createTextNode(userId));
            layerid.appendChild(userid);

            // seventh element
            Element time2= document.createElement("time");
            time2.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(time2);

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
        }

        File file=new File(xmlFilePath);
   return file;
    }


//    This method is used to delete entries which are of type Fixed as per timer associated with it.

    public void maintenancethread() {
        PreparedStatement pst = null;
        int rowid;
        Long timer = Long.valueOf(0);
        long time = 0;
        ResultSet rs = null;
        try {
            rs = conn.getMetaData().getTables(null, null, null, null);

            while (rs.next()) {

                String ld = rs.getString("TABLE_NAME");
                String intValue = ld.replaceAll("[^0-9]", "");
                int layerid = Integer.parseInt(intValue);
                String filename = "Table" + layerid;
                pst = conn.prepareStatement("SELECT timer,time FROM " + filename);
                ResultSet rs2 = pst.executeQuery();
                timer = rs2.getLong("timer");
                time = rs2.getLong("time");
                rowid = rs2.getRow();
                if (!(timer == 0)) {
                    if (System.currentTimeMillis() - time > timer) {
                        utility.delete_entry(rowid);

                    }
                }
                rs2.close();
            }
            pst.close();
            rs.close();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        /*try {
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
                            utility.delete_entry(rowid);

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
        }*/

    }


//    Constructor function of Main class.

    private IndexingManager() {
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        utility = Database_Utility.getInstance();
        conn = utility.getConnection();
        IMbuffer=IndexingManagerBuffer.getInstance();
        //utility.createtable(0);
        //utility.createtable(100);


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

}
