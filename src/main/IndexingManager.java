package src.main;

import jdk.internal.org.xml.sax.SAXException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.sql.*;
import java.util.ArrayList;

/** This is main class of IndexingManager API. Glue code will interact with this class for adding and searching an index.It has methods
 * for doing these tasks. Also, updation and deletion will be done as per timer type and timer associated with it.
 */
public class IndexingManager {
    /**
     * Creating a private object of class.
     */
    private static IndexingManager indexManager;
    /**
     * Private variable to receive key.
     */
    private static String origkey;
    /**
     * Private variable to receive Value.
     */
    private static String origvalue;
    /**
     * Private variable to receive Timer(time for which key value pair to be stored).
     */
    private static String origtimer;
    /**
     * Private variable to receive total copies. Can be used in future when dynamic redundancy.
     */
    private static int origtotalCopies;
    /**
     * Private variable to receive Copy Number.
     */
    private static int origcopyNum;
    /**
     * Private variable to receive Timer Type(Fixed or Perpetual).
     * Fixed-Index remains for fixed time.
     * Perpetual-Index time will be updated if accessed within timer specified.
     */
    private static boolean origtimerType;
    /**
     * Private variable to receive Userid. Can be name or email id.
     */
    private static String origuserId;
    /**
     * Private variable to receive Layerid. It will tell about layer to which key value pair belongs.
     */
    private static int origLayerId;
    private static String origTime;
    /**
     * Private variable to receive User's Certificate.
     */
    private static java.security.cert.Certificate origCerti;
    /**
     * Creating pivate object of class Connection.
     */
    private Connection conn;
    /**
     * Creating pivate object of class Database Utility.
     */
    private Database_Utility utility;
    /**
     * Creating pivate object of class Indexing Manager Buffer.
     */
    private IndexingManagerBuffer IMbuffer;



    /** This method will check whether table for layerID requested exists or not.Returns true if table exists.
     * @param layerId Comes as an argument with index entry.
     * @return True if table exists.
     */
    public boolean checkTable(int layerId)

    {
        boolean isAvailable = false;
        try {

            //This statement will fetch all tables available in database.

            ResultSet rs1 = conn.getMetaData().getTables(null, null, null, null);
            while (rs1.next()) {

                String ld = rs1.getString("TABLE_NAME");

                //This statement will extract digits from table names.

                String intValue = ld.replaceAll("[^0-9]", "");
                int v;
                if (intValue != null) {
                    v = Integer.parseInt(intValue);
                    if (v == layerId) {
                        isAvailable = true;
                    }
                }
                //This statement will compare layerid with digits of table names.

            }
            rs1.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isAvailable;
    }

    /** This method will check whether index which is to be added is Copy0/Copy1/Copy2.If Copy0 it will return true.
     * @param copynum Comes as an argument with index entry.
     * @return True if copy is original.
     */
    private boolean checkiforiginal(int copynum) {
        boolean isAvailable = false;
        if (copynum == 0) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /** This method is used to add an index entry to database. Glue code will call this method and pass required arguments.
     * Following arguments are given by glue code.They have been specified above.
     * @param key
     * @param value
     * @param timer
     * @param totalCopies
     * @param copyNum
     * @param timerType
     * @param userId
     * @param layerID
     * @param time
     * @param c
     */
    public void addIndex(String key, String value, String timer, int totalCopies, int copyNum, boolean timerType, String userId, int layerID, String time, java.security.cert.Certificate c) {
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


        // Verifying Digital Signature of Value using Certificate

        Verif v = new Verif();

        boolean b1 = v.Verify_Digital_Signature(origCerti, origvalue);

        // If signature is verified

        if (b1) {

            // This statement will check whether table exists or not.

            boolean b2 = checkTable(origLayerId);
            if (b2) {

                //If table exists.Check if copy is original or not.

                boolean b3 = checkiforiginal(copyNum);
                if (!b3) {
                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                    System.out.println("Entry added");
                } else {

                // If copy is original,calculate root nodes for redundant copies and put XML files containing all details for key valu pair in buffer for Glue Code to pick up.

                    String[] s = rootcalc(origkey);
                    File f1 = XMLforRoot(s[0], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    File f2 = XMLforRoot(s[1], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    IMbuffer.addToIMOutputBuffer(f1);
                    IMbuffer.addToIMOutputBuffer(f2);
                }
            } else {

                //If table doesn't exist then create table and add entries accordingly as specified above for copy number.

                utility.createtable(origLayerId);

                boolean b4 = checkiforiginal(copyNum);
                if (!b4) {

                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                } else {
                    String[] s = rootcalc(origkey);
                    File f1 = XMLforRoot(s[0], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    File f2 = XMLforRoot(s[1], origkey, origvalue, origLayerId, origcopyNum, origtimer, origtimerType, origuserId, origTime, origCerti);
                    IMbuffer.addToIMOutputBuffer(f1);
                    IMbuffer.addToIMOutputBuffer(f2);

                }

            }

        } else {

        //If Signature is not verified entry will not be added.

            System.out.println("Signature not verified");
        }

    }

    /** This method is used to calculate root nodes for Copy1 and Copy2,returns an array containing them.Root calculation is done
     * using Consistent hashing.Hash of concatenated string of key and copy number is calculated.
     * @param key Key for which root nodes are to be calculated.
     * @return String array containing root nodes.
     */
    public String[] rootcalc(String key) {
        String s1 = "Copy1";
        String concat1 = key.concat(s1);
        String hashId1 = null;
        String hashId2 = null;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(concat1.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte bytes : digest) {
                hexString.append(String.format("%02x", bytes).toUpperCase());
            }
            hashId1 = hexString.toString();
            System.out.println(hashId1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        String s2 = "Copy2";
        String concat2 = key.concat(s2);
        MessageDigest messageDigest1 = null;
        try {
            messageDigest1 = MessageDigest.getInstance("SHA-1");
            messageDigest1.update(concat2.getBytes());
            byte[] digest1 = messageDigest1.digest();
            StringBuilder hexString1 = new StringBuilder();
            for (byte bytes : digest1) {
                hexString1.append(String.format("%02x", bytes).toUpperCase());
            }
            hashId2 = hexString1.toString();
            System.out.println(hashId2);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        String[] strArray1 = new String[2];
        strArray1[0] = hashId1;
        strArray1[1] = hashId2;
        return strArray1;

    }


    /** This method is used to create XML files containing root node,other details for key and add to output buffer for Glue code.
     * @param hashid Root node to which copy is to be transferred.
     * @param key
     * @param value
     * @param LayerId
     * @param copyNum
     * @param timer
     * @param timerType
     * @param userid
     * @param Time
     * @param Certi
     * @return XML file containing all details.
     */
    public File XMLforRoot(String hashid, String key, String value, int LayerId, int copyNum, String timer, boolean timerType, String userid, String Time, Certificate Certi) {
        String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\For Root Node.xml";
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("Root_Node_For_Copy_and_Data");
            document.appendChild(root);

            Element hashId = document.createElement("HashID");
            hashId.appendChild(document.createTextNode(hashid));
            root.appendChild(hashId);

            Element layerid = document.createElement("layerid");
            hashId.appendChild(layerid);
            layerid.setAttribute("Id", String.valueOf(LayerId));

            Element key1 = document.createElement("Key");
            hashId.appendChild(key1);
            key1.setAttribute("Key", key);

            Element Value = document.createElement("Value");
            Value.appendChild(document.createTextNode(value));
            hashId.appendChild(Value);

            Element copynum = document.createElement("copynum");
            copynum.appendChild(document.createTextNode(String.valueOf(copyNum)));
            hashId.appendChild(copynum);

            Element timer2 = document.createElement("timer");
            timer2.appendChild(document.createTextNode(String.valueOf(timer)));
            hashId.appendChild(timer2);

            Element timertype = document.createElement("timertype");
            timertype.appendChild(document.createTextNode(String.valueOf(timerType)));
            hashId.appendChild(timertype);

            Element time2 = document.createElement("time");
            time2.appendChild(document.createTextNode(String.valueOf(Time)));
            hashId.appendChild(time2);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        File file = new File(xmlFilePath);
        return file;
    }

    /** This method is used to update current time for perpetual index entry.
     * @param Key
     * @param layerID
     */
    public void updateEntry(String Key, int layerID) {

        utility.update_entry(Key, layerID);

    }

    /** This method is used to search index entry using key and layerID in database. Also it will put details of object in an output buffer as XML file.

     * @param Key
     * @param layerID
     * @return XML file containing details for Search query.
     */
    public File searchEntry(String Key, int layerID) {

        ObjReturn obj = utility.search_entry(Key, layerID);
        boolean b = obj.timerType1;

        if (!b) {
            updateEntry(Key, layerID);
        }
        File f = makeXML(Key, layerID, obj.value1, obj.time1, obj.totalCopies1, obj.copyNum1, obj.timerType1, obj.userId, obj.time);
        IMbuffer.addToIMOutputBuffer(f);
        return f;

    }

    /** This method is used to make XML file for Search Query.It contains details pertaining to key searched.
     * @param key
     * @param layerID
     * @param value1
     * @param time1
     * @param totalCopies1
     * @param copyNum1
     * @param timerType1
     * @param userId
     * @param time
     * @return
     */
    public File makeXML(String key, int layerID, String value1,String time1, int totalCopies1, int copyNum1, boolean timerType1, String userId, String time) {
        String xmlFilePath = "C:\\Users\\a\\Pictures\\IndexingManagerAPI\\Search Result for Key.xml";
        try {

            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();

            // root element
            Element key1 = document.createElement("Search_Result_for_key");
            document.appendChild(key1);
            key1.setAttribute("Key", key);

            Element layerid = document.createElement("layerid");

            key1.appendChild(layerid);
            layerid.setAttribute("Id", String.valueOf(layerID));

            Element Value = document.createElement("Value");
            Value.appendChild(document.createTextNode(value1));
            layerid.appendChild(Value);

            Element timer = document.createElement("timer");
            timer.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(timer);

            Element totcopies = document.createElement("totcopies");
            totcopies.appendChild(document.createTextNode(String.valueOf(totalCopies1)));
            layerid.appendChild(totcopies);

            Element copynum = document.createElement("copynum");
            copynum.appendChild(document.createTextNode(String.valueOf(copyNum1)));
            layerid.appendChild(copynum);

            Element timertype = document.createElement("timertype");
            timertype.appendChild(document.createTextNode(String.valueOf(timerType1)));
            layerid.appendChild(timertype);

            Element userid = document.createElement("userid");
            userid.appendChild(document.createTextNode(userId));
            layerid.appendChild(userid);

            Element time2 = document.createElement("time");
            time2.appendChild(document.createTextNode(String.valueOf(time1)));
            layerid.appendChild(time2);

            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            System.out.println("Done creating XML File");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        File file = new File(xmlFilePath);
        return file;
    }


    /**
     *  This method is used to delete entries which are of type fixed as per timer associated with it.
     *  This thread will run continuously after every 15 minutes.
     */
    public void maintenancethread() {
            while(true) {
                Thread maintenanceThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread());
                        PreparedStatement pst = null;
                        int rowid;
                        long timer = 0;
                        long time = 0;
                        String key;


                        try {
                            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);

                            while (rs.next()) {

                                String ld = rs.getString("TABLE_NAME");
                                String intValue = ld.replaceAll("[^0-9]", "");
                                int layerid = Integer.parseInt(intValue);
                                String filename = "Table" + layerid;
                                pst = conn.prepareStatement("SELECT * FROM " + filename);
                                ResultSet rs2 = pst.executeQuery();
                                while (rs2.next()) {
                                    timer = Long.parseLong(rs2.getString("timer"));
                                    time = Long.parseLong(rs2.getString("time"));
                                    key = rs2.getString("Key");

                                    if (!(timer == 0)) {
                                        if (System.currentTimeMillis() - time > timer) {
                                            utility.delete_entry(layerid, key);
                                        }
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
                    }
                });
                maintenanceThread.start();
                try {
                    Thread.sleep(900000);
                    System.out.println("Thread going to sleep");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    /**
     * This thread will run continuously after every 30 minutes to ascertain for which nodes I am root.
     * For which I am not, will be transferred to Purge table whose layerid is 100.
     */
    public void maintenancethread1() {
        while(true) {
            Thread maintenanceThread1 = new Thread(new Runnable() {
                @Override
                public void run() {

                    // This statement will request Routing manager to ascertain keys for which I am root node.

                    QueryForRoutingManager();
                    File f=IMbuffer.fetchFromIMInputBuffer();
                    transfertopurge(f);
                }
                });
        maintenanceThread1.start();
        try {
            Thread.sleep(1800000);
            System.out.println("Thread going to sleep");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    }

    /** This method will receive response from Routing manager specifying keys for which I am root.
     * For which I am not,will be transferred to purge table.
     * @param file Response from Routing manager.
     */
    public void transfertopurge(File file){
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        String selfNodeID = null;
        try {
            documentBuilder = builderFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(file);
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
                    String hashid= String.valueOf(element.getElementsByTagName("HASHID").item(0));
                    if(!(hashid =="Root Node")){
                        transfer(key,layerID);
                        utility.delete_entry(layerID,key);
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File("ResponseToIndexM.xml"));
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | IOException | TransformerException e) {

        } catch (org.xml.sax.SAXException e) {
            e.printStackTrace();
        }
    }

    /** This method takes layer id and key and transfer index entry to purge table.
     * @param key
     * @param layerid
     */
    public void transfer(String key,int layerid){
        ObjReturn obj1 =utility.search_entry(key,layerid);
        utility.add_entry(100,obj1.key1,obj1.value1,obj1.time1,obj1.totalCopies1,obj1.copyNum1,obj1.timerType1,obj1.userId,obj1.time,obj1.cert);

    }
    /**
     *  Private Constructor of Main class.
     */
    private IndexingManager() {
        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        utility = Database_Utility.getInstance();
        conn = utility.getConnection();
        IMbuffer = IndexingManagerBuffer.getInstance();

       // This statement is to create purge table. I have kept its layer id as 100.

        boolean k=checkTable(100);

        if(!k) {
            utility.createtable(100);
        }

      //  This statement is to run maintenance thread on loading of class to purge entries whose timer has expired.

        maintenancethread();

        //  This statement is to run maintenance thread on loading of class to ascertain root nodes.
        maintenancethread1();
    }

    /** Creating Singleton object of class.
     * @return Object of class.
     */
    public static synchronized IndexingManager getInstance() {
        if (indexManager == null) {
            indexManager = new IndexingManager();
            return indexManager;
        } else {
            return indexManager;
        }
    }

    /**This method is used to generate xml file for routing manager to ascertain for which nodes I am root or not.
     * After file generation objects of file are made and added to output buffer.
     */

    public void QueryForRoutingManager() {
        PreparedStatement stmt = null;

        try {
            //This statement will fetch all tables available in database.
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.newDocument();

                String ld = rs.getString("TABLE_NAME");
                System.out.println(ld);

                //This statement will extract digits from table names.

                String intValue = ld.replaceAll("[^0-9]", "");
                int v = Integer.parseInt(intValue);
                System.out.println(v);

                //create root Element

                Element root = doc.createElement("CheckingRootNodeForIndex");
                doc.appendChild(root);

                root.setAttribute("LayerID", intValue);

                int i =1;
                    stmt = conn.prepareStatement("select key from " + ld);
                    ResultSet rs2 = stmt.executeQuery();
                    while (rs2.next()) {
                        Element row1 = doc.createElement("DATA");
                        root.appendChild(row1);
                        row1.setAttribute("INDEX", "[" + i + "]");

                        Element nodeID = doc.createElement("KEY");
                        nodeID.appendChild(doc.createTextNode(rs2.getString("key")));
                        row1.appendChild(nodeID);

                        Element nodePub = doc.createElement("NEXTHOP");
                        nodePub.appendChild(doc.createTextNode(""));
                        row1.appendChild(nodePub);
                        i+=1;
                    }
                    rs2.close();

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(doc);

                StreamResult streamResult = new StreamResult(new File(ld + "_RootNodeCheck" + ".xml"));
                transformer.transform(domSource, streamResult);
                System.out.println("Root Node checking file is generated");
                File f=new File(ld + "_RootNodeCheck" + ".xml");
                IMbuffer.addToIMOutputBuffer(f);
            }
            rs.close();

        } catch (TransformerException | SQLException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
}
