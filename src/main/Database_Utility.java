package src.main;


import java.io.ByteArrayInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class has functionality to interact with SQLite database and perform assigned task by IndexingManager Class
 */
public class Database_Utility {

    /**
     * Creating object of Class Database Utility.
     */
    public static Database_Utility utility;
    /**
     * Creating Object of Class Connection for making connection to database.
     */
    private Connection conn;

    /**
     * Constructor of Database Utilty class made private.Connection is created in Constructor itself.
     */
    private Database_Utility() {
        conn = getConnection();
    }


    /**
     * Creating Singleton Object.
     *
     * @return Object of Database Utility class.
     */
    public static synchronized Database_Utility getInstance() {
        if (utility == null) {
            utility = new Database_Utility();
            return utility;
        } else {
            return utility;
        }
    }

    /**
     * This method is used to create connection with database.
     *
     * @return Object of class Connection.
     */
    public Connection getConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            //conn= DriverManager.getConnection("jdbc:sqlite:C:\\Users\\a\\Documents\\NetBeansProjects\\IndexManagerAPI\\src\\KeyValuePairs.db");
            conn = DriverManager.getConnection("jdbc:sqlite:KeyValuePairs.db");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;

    }


    /**
     * This method is used to create table in database as per layerid.
     *
     * @param layerid This argument is provided by user while adding an index entry.
     */

    public void createtable(int layerid) {
        try {
            String fileName = "Table" + layerid;

            String sql = "CREATE TABLE " + fileName + "("
                    + "[Key] STRING (30) PRIMARY key NOT NULL,"
                    + "value STRING (255),"
                    + "timer STRING(30),"
                    + "totalCopies INTEGER,"
                    + "copyNum INTEGER,"
                    + "timerType INTEGER,"
                    + "userId STRING(30) ,"
                    + "LayerId INTEGER ,"
                    + " time STRING(30) NOT NULL ,"
                    + " Certificate VARCHAR NOT NULL " + ")";


            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method is created to create table for userid to certificate mapping.Table number for this is kept as 101.
     */
    public void createtable1(){
        try {
            String fileName = "Table" + 101;
            String sql = "CREATE TABLE " + fileName + "("
                    + "[userId] STRING (30) PRIMARY key NOT NULL,"
                    + " Certificate VARCHAR NOT NULL " + ")";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

        }

    /**
     * This method is used to add index entry to database as per layer id.
     *  @param layerID     This argument is provided by user while adding an index entry.
     * @param key         This argument is provided by user while adding an index entry.
     * @param value       This argument is provided by user while adding an index entry.
     * @param timer       This argument is provided by user while adding an index entry.  It tells time for which entry is to be stored.
     * @param totalCopies This argument is provided by user while adding an index entry.
     * @param copyNum     This argument is provided by user while adding an index entry. It tells whether copy is original or not.
     * @param timerType   This argument is provided by user while adding an index entry. It tells whether timer is fixed or perpetual.
     * @param userId      This argument is provided by user while adding an index entry. This can be name or mail id.
     * @param time        This argument is provided by user while adding an index entry.
     * @param c           This argument is provided by user while adding an index entry. It is user's certificate.
     */
    public void add_entry(int layerID, String key, String value, String timer, int totalCopies, int copyNum, boolean timerType, String userId, String time, Certificate c) {
        try {
            String b = java.util.Base64.getEncoder().encodeToString(c.getEncoded());

            String tableName = "Table" + layerID;
            String sql = "INSERT INTO " + tableName + " (key,value,timer,totalCopies,copyNum,timerType,userId,time,Certificate) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setString(3, timer);
            pstmt.setInt(4, totalCopies);
            pstmt.setInt(5, copyNum);
            pstmt.setBoolean(6, timerType);
            pstmt.setString(7, userId);
            pstmt.setString(8, time);
            pstmt.setString(9, b);

            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }

    }


    /**
     * This method is used to search an index entry in database.
     *
     * @param Key     Key which is to be searched.
     * @param layerId Layer id to which key belongs.
     * @return Returns an object which contain all details related to key.
     */
    public ObjReturn search_entry(String Key, int layerId) {
        ObjReturn obj1 = new ObjReturn();

        try {

            String filename = "Table" + layerId;
            PreparedStatement stmt = conn.prepareStatement(" select value,timer,totalCopies,copyNum,timerType,userId,time,Certificate from " + filename + " where Key=? ");
            stmt.setString(1, Key);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {


                String val = rs.getString(1);
                String tim = rs.getString(2);
                int totCo = rs.getInt(3);
                int copNum = rs.getInt(4);
                boolean timeTyp = rs.getBoolean(5);
                String hashId = rs.getString(6);
                String time = rs.getString(7);
                String s=rs.getString(8);


                byte[] decodedByte = java.util.Base64.getMimeDecoder().decode(s);
                CertificateFactory cf=CertificateFactory.getInstance("X.509");
                ByteArrayInputStream bis=new ByteArrayInputStream(decodedByte);
                Certificate cert=cf.generateCertificate(bis);

                obj1.setValue1(val);
                obj1.setTime1(tim);
                obj1.setTotalCopies1(totCo);
                obj1.setCopyNum1(copNum);
                obj1.setTimerType1(timeTyp);
                obj1.setUserId(hashId);
                obj1.setTime(time);
                obj1.setCert(cert);

            }
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return obj1;
    }


    /**
     * This method is used to delete entry in particular table depending on layer id.
     *
     * @param layerid Layer id to which key belongs.
     * @param key     Key which is to be deleted.
     */
    public void delete_entry(int layerid, String key) {

        try {
            String filename = "Table" + layerid;
            PreparedStatement stmt1 = conn.prepareStatement("delete from " + filename + " where Key=?");

            stmt1.setString(1, key);

            stmt1.executeUpdate();
            System.out.println(key + " Deleted succesfully");
            stmt1.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * This method is used to update time for perpetual entries.
     *
     * @param Key     Key for which time is to be updated.
     * @param layerID Layerid to which key belongs.
     */
    public void update_entry(String Key, int layerID) {

        try {

            String filename = "Table" + layerID;
            PreparedStatement stmt = conn.prepareStatement(" update " + filename + " set time = ? where Key = ? ");
            stmt.setString(1, String.valueOf(System.currentTimeMillis()));
            stmt.setString(2, Key);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}















  




