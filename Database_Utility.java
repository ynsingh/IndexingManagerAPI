//This class has funtionality to interact with SQL database and perform assigned task by IndexingManager Class


import static java.awt.Event.INSERT;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
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
    
   public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\a\\Documents\\NetBeansProjects\\IndexManagerAPI\\src\\KeyValuePairs.db");
                    
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection; 
       
   }
   
   
   public static void createtable(){
       try {
           Connection conn = getConnection();
           String sql = "CREATE TABLE keyvalue1 ("
    +"[Key] STRING (30) PRIMARY key NOT NULL,"
    +"value STRING (255),"
    +"timer TIME,"
    +"totalCopies INTEGER,"
    +"copyNum INTEGER,"
    +"timerType STRING(30),"
    +"userId INTEGER ," 
    +" time TEXT(520) NOT NULL "+")";
          

           PreparedStatement pstmt = conn.prepareStatement(sql) ;
           pstmt.executeUpdate();
           conn.close();
       } catch (SQLException ex) {
           Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
       }
   
   
   }
   
   
 // This method is used to add index entry to database.    
   
   public static void add_entry(String key,String value,int timer,int totalCopies,int copyNum,String timerType,int userId,Long time){
       try {
           Connection conn = getConnection();
           String sql = "INSERT INTO keyvalue1 (key,value,timer,totalCopies,copyNum,timerType,userId,time) VALUES(?,?,?,?,?,?,?,?)";
           PreparedStatement pstmt = conn.prepareStatement(sql) ;
           pstmt.setString(1,key);
           pstmt.setString(2, value);
           pstmt.setInt(3, timer);
           pstmt.setInt(4, totalCopies);
           pstmt.setInt(5, copyNum);
           pstmt.setString(6, timerType);
           pstmt.setInt(7, userId);
           
           pstmt.setLong(8,time);
           
pstmt.executeUpdate();
pstmt.close();
conn.close();
       } catch (SQLException ex) {
           Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
       }
       

}
       
  
  // This method is used to delete index entry to database. 
   
     public void delete_entry(int rid,Connection conn){
       try {
           
           PreparedStatement stmt=conn.prepareStatement("delete from keyvalue1 where rowid=?");
           
           stmt.setInt(1,rid);
 
           int i=stmt.executeUpdate();
           System.out.println(rid+"Deleted succesfully");
           stmt.close();
           
           
       } catch (SQLException ex) {
           Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
       }
      }

     // This method is used to update index entry to database.
     
     public void update_entry(String Key){
     
       try {
           Connection conn = getConnection();
           PreparedStatement stmt=conn.prepareStatement("update keyvalue set Value = ? where Key = ?");
           
           stmt.setString(1, Key);
           stmt.setString(2,Key);
           stmt.executeUpdate();
       } catch (SQLException ex) {
           Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
       }

}
     // This method is used to search index entry with key in database. 
     
   public static ObjReturn search_entry(String Key){
       ObjReturn obj1 = new ObjReturn();
       try {
           Connection conn = getConnection();
         
           PreparedStatement stmt=conn.prepareStatement("select value,timer,totalCopies,copyNum,timerType,userId,time from keyvalue1 where Key=?");
           stmt.setString(1, Key);
           ResultSet rs =stmt.executeQuery();
//           SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           while(rs.next()) {
                   
               String val = rs.getString(1);
               System.out.println(val);
               
               
               int tim=rs.getInt(2);
              
               int totCo=rs.getInt(3);
               int copNum=rs.getInt(4);
               String timeTyp=rs.getString(5);
               int hashId=rs.getInt(6);
               Long time=rs.getLong(7);
               
               
               //                   java.util.Date d =formatter.parse(text1);
//
//                   
//                   System.out.println(d);
//                   
//                   Long t=d.getTime();
//                   
//                   System.out.println(t);
//
//
//                   System.out.println(System.currentTimeMillis());






obj1.setValue1(val);
obj1.setTime1(tim);
obj1.setTotalCopies1(totCo);
obj1.setCopyNum1(copNum);
obj1.setTimerType1(timeTyp);
obj1.setUserId(hashId);
obj1.setTime(time);
                   
               
           }} 
       
       catch (SQLException ex) {
           Logger.getLogger(Database_Utility.class.getName()).log(Level.SEVERE, null, ex);
       }    
       
       return obj1;
}
   
   
    public static void main(String[] args){
        
        


   
//createtable();    
//add_entry("hiii","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());
//add_entry("iiiii","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());
//add_entry("ooooo","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());
//add_entry("mmmmm","hooooo",6,1,8,"iiii",9,System.currentTimeMillis());


//    ObjReturn obj2 =search_entry("hiii");
//    
//    
//      
//  System.out.println(obj2.getTime());


}
}















  




