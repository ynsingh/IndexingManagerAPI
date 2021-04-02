//This class has funtionality to interact with SQL database and perform assigned task by IndexingManager Class


import org.bouncycastle.asn1.x509.Certificate;

import static java.awt.Event.INSERT;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Timestamp;
import java.security.cert.CertificateEncodingException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.util.Duration.millis;

class Database_Utility {

// This method is used to create connection with database.   

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\a\\Documents\\NetBeansProjects\\IndexManagerAPI\\src\\KeyValuePairs.db");

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;

    }


    public static void createtable() {
        try {
            Connection conn = getConnection();
            String sql = "CREATE TABLE keyvalue1 ("
                    + "[Key] STRING (30) PRIMARY key NOT NULL,"
                    + "value STRING (255),"
                    + "timer TIME,"
                    + "totalCopies INTEGER,"
                    + "copyNum INTEGER,"
                    + "timerType INTEGER,"
                    + "userId STRING(30) ,"
                    + "LayerId INTEGER ,"
                    + " time TEXT(520) NOT NULL ,"
                    + " Certificate VARCHAR NOT NULL " + ")";
                    //+"   UNIQUE(userId,LayerId) "



            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    // This method is used to add index entry to database.

    public static void add_entry(String key, String value, Long timer, int totalCopies, int copyNum, boolean timerType, String userId, int LayerId, Long time, java.security.cert.Certificate c) {
        try {
            Connection conn = getConnection();
            String sql = "INSERT INTO keyvalue1 (key,value,timer,totalCopies,copyNum,timerType,userId,LayerId,time,Certificate) VALUES(?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setLong(3, timer);
            pstmt.setInt(4, totalCopies);
            pstmt.setInt(5, copyNum);
            pstmt.setBoolean(6, timerType);
            pstmt.setString(7, userId);
            pstmt.setInt (8,LayerId);
            pstmt.setLong(9, time);

            pstmt.setBytes(10,c.getEncoded());

            pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }


    }


    // This method is used to delete index entry to database.

    public static ObjReturn search_entry(String Key) {
        ObjReturn obj1 = new ObjReturn();
        try {
            Connection conn = getConnection();

            PreparedStatement stmt = conn.prepareStatement("select value,timer,totalCopies,copyNum,timerType,userId,time from keyvalue1 where Key=?");
            stmt.setString(1, Key);
            ResultSet rs = stmt.executeQuery();
//           SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            while (rs.next()) {

                String val = rs.getString(1);
                System.out.println(val);


                Long tim = rs.getLong(2);

                int totCo = rs.getInt(3);
                int copNum = rs.getInt(4);
                boolean timeTyp = rs.getBoolean(5);
               String hashId = rs.getString(6);
                Long time = rs.getLong(7);


                obj1.setValue1(val);
                obj1.setTime1(tim);
                obj1.setTotalCopies1(totCo);
                obj1.setCopyNum1(copNum);
                obj1.setTimerType1(timeTyp);
                obj1.setUserId(hashId);
                obj1.setTime(time);


            }
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

        return obj1;
    }

    // This method is used to update index entry to database.

    public static void main(String[] args) {


        createtable();


       /* SignatureVerif S2 = SignatureVerif.getInstance();
        KeyStore k = S2.getKeyStore();
        try {
            add_entry("hiii","hooooo",6,1,8,"iiii",9,System.currentTimeMillis(), k.getCertificate("Certificate"));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }*/
//add_entry("iiiii","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());
//add_entry("ooooo","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());
//add_entry("mmmmm","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());


//    ObjReturn obj2 =search_entry("hiii");
//
//
//
//  System.out.println(obj2.getTime());


    }
    // This method is used to search index entry with key in database.

    public void delete_entry(int rid, Connection conn) {
        try {

            PreparedStatement stmt = conn.prepareStatement("delete from keyvalue1 where rowid=?");

            stmt.setInt(1, rid);

            int i = stmt.executeUpdate();
            System.out.println(rid + "Deleted succesfully");
            stmt.close();


        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void update_entry(String Key) {

        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement("update keyvalue1 set Value = ? where Key = ?");

            stmt.setString(1, Key);
            stmt.setString(2, Key);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public boolean search_userId(String userId){
     boolean b = false;
        Connection conn = getConnection();

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select userId from keyvalue1 where userId=?");

            stmt.setString(1,userId);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return b;
    }
}















  




