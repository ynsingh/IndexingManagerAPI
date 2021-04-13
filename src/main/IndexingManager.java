package src.main;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

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

    private boolean checkiforoiginal(int copynum){
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

                boolean b3=checkiforoiginal(int origcopyNum);
                if(!b3) {
                    utility.add_entry(origLayerId, origkey, origvalue, origtimer, origtotalCopies, origcopyNum, origtimerType, origuserId, origTime, origCerti);
                    System.out.println("Entry added");
                }
                else{
                    String s1="Copy1";
                    String concat1=origkey.concat(s1);

                    MessageDigest messageDigest = null;
                    try {
                        messageDigest = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    messageDigest.update(concat1.getBytes());
                    String hashId1 = new String(messageDigest.digest());

                   // int hashId1= concat1.hashCode();
                    String s2="Copy2";
                    String concat2=origkey.concat(s2);
                    MessageDigest messageDigest1 = null;
                    try {
                        messageDigest1 = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    messageDigest.update(concat2.getBytes());
                    String hashId2 = new String(messageDigest.digest());

                    //int hashId2=concat2.hashCode();

                }
            } else {

                //If table doesn't exist then create table and then add entry.

                utility.createtable(origLayerId);

                boolean b4=checkiforoiginal(int origcopyNum);
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

    public void updateEntry(String Key, String updatedValue, int layerID) {

        utility.update_entry(Key, updatedValue, layerID);

    }

    // This method is used to search index entry using key and layerID in database.

    public ObjReturn searchEntry(String Key, int layerID) {

        ObjReturn obj = utility.search_entry(Key, layerID);
        return obj;
    }

//    This method is used to delete entries as per Timer Type and timer associated with it.

    public void maintenancethread() {
        PreparedStatement pst = null;
        int rowid;
        Long timer = Long.valueOf(0);
        long time = 0;
        String key = null;
        boolean timerType;


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
