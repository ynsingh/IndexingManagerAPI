package src.main;//This class has funtionality to interact with SQL database and perform assigned task by IndexingManager Class


import java.security.cert.Certificate;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;



class Database_Utility {

    private Connection conn;

    public static Database_Utility utility;

    private Database_Utility(){
        conn=getConnection();
    }

    public static synchronized Database_Utility getInstance() {
        if (utility == null) {
            utility = new Database_Utility();
            return utility;
        } else {
            return utility;
        }
    }

// This method is used to create connection with database.   

    public Connection getConnection() {

        try {
            Class.forName("org.sqlite.JDBC");
            //conn= DriverManager.getConnection("jdbc:sqlite:C:\\Users\\a\\Documents\\NetBeansProjects\\IndexManagerAPI\\src\\KeyValuePairs.db");
            conn= DriverManager.getConnection("jdbc:sqlite:C:\\Users\\a\\Pictures\\IndexingManagerAPI.db");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;

    }


    public void createtable(int layerid) {
        try {
            String fileName = "Table" + layerid;

            String sql = "CREATE TABLE " + fileName + "("
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


            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
            //conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    // This method is used to add index entry to database.

    public  void add_entry(int layerID, String key, String value, Long timer, int totalCopies, int copyNum, boolean timerType, String userId, Long time, Certificate c) {
        try {
            String tableName = "Table"+layerID;
            String sql = "INSERT INTO "+ tableName +" (key,value,timer,totalCopies,copyNum,timerType,userId,time,Certificate) VALUES(?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.setLong(3, timer);
            pstmt.setInt(4, totalCopies);
            pstmt.setInt(5, copyNum);
            pstmt.setBoolean(6, timerType);
            pstmt.setString(7, userId);

            pstmt.setLong(8, time);
            pstmt.setString(9, String.valueOf(c));

            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // This method is used to search an index entry in database.It needs Key and layerID as arguments.

    public ObjReturn search_entry(String Key,int layerId) {
        ObjReturn obj1 = new ObjReturn();
        try {

            String filename="Table"+layerId;
            PreparedStatement stmt = conn.prepareStatement("select value,timer,totalCopies,copyNum,timerType,userId,time from " + filename + "where Key=?");
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

    public void delete_entry(int rid) {
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

    public void update_entry(String Key,String updValue, int layerID) {

        try {

            String filename="Table"+layerID;
            PreparedStatement stmt = conn.prepareStatement("update " + filename +"set Value = ? where Key = ?");
            stmt.setString(1, updValue);
            stmt.setString(2, Key);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    public boolean search_userId(String userId) {
        boolean b = false;

        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("select userId from keyvalue1 where userId=?");

            stmt.setString(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return b;
    }
}















  




