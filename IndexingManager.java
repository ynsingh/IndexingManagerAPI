


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

//This is main class of IndexManager API. Central code will interact with this class for 
// adding,deleting,updating and searching an index.It has methods for doing those tasks.

public class IndexingManager {
    private static IndexingManager indexManager;
    private static String origkey;
    private static String origvalue;
    private static int origtime;
    private static int origtotalCopies;
    private static int origcopyNum;
    private static String origtimerType;
    private static int origuserId;
    private static Long origTime;
    
    private static timer t1;
    
    //  Creating Singleton object of Database_Utility class.
    
    
   public static Database_Utility utility;
    public synchronized Database_Utility utility(){
       if(utility==null) {
           utility=new Database_Utility() ;
           return utility ;
       }
       else{
           return utility;
       }
    }
   
    // This method is used to add index entry to database. Central code will call this method and pass required arguments.
    
   public void addEntry(String key,String value,int timer,int totalCopies,int copyNum,String timerType,int userId,Long time ){
       origkey=key;
       origvalue=value;
       origtime=timer;
       origtotalCopies=totalCopies;
       origcopyNum=copyNum;
       origtimerType=timerType;
       origuserId=userId;
       origTime=time;
       
       utility.add_entry(origkey, origvalue, origtime, origtotalCopies, origcopyNum, origtimerType, origuserId,origTime);
   
       
   }
   
   // This method is used to delete index entry to database.
   
//   public void deleteEntry( String Key){
//       
//   utility.delete_entry(Key);
//   
//           
//   
//   }
   
   // This method is used to update index entry to database.
   
    public void updateEntry(String Key){
    
    utility.update_entry(Key);
    
    }
    
    // This method is used to search index entry using key in database.
    
    public ObjReturn searchEntry(String Key){
    
    ObjReturn obj=utility.search_entry(Key);
return obj;
    }
    
//    This method is used to delete entries as per Timer Type and timer associated with it.
    
    public static void maintenancethread(){
        
            Database_Utility db= new Database_Utility();
            Connection conn = db.getConnection();
            PreparedStatement pst;
            int rowid;
            int timer = 0;
            long time = 0;
            String key = null;
            String timerType;
            try {
               pst=conn.prepareStatement("select * from keyvalue1");
                ResultSet rs = pst.executeQuery();
                while(rs.next()) {
                    timerType = rs.getString("timerType");
                    rowid=rs.getRow();
                    System.out.println(rowid);
                    System.out.println(timerType);
                    
                    
                    if("F".equals(timerType)){
                        pst=conn.prepareStatement("SELECT timer,time,Key FROM keyvalue1 where rowid=?");
                        pst.setInt(1,rowid);
                        ResultSet rs2 =pst.executeQuery();
                        timer = rs2.getInt("timer");
                        time=rs2.getLong("time");
                        key = rs2.getString("Key");
                        rs2.close();
                        System.out.println(timer);
                        System.out.println(time);
                        System.out.println(key);
                    
                    
                    if (!(timer==0)){
                        System.out.println(System.currentTimeMillis()- time > timer);
                        if(System.currentTimeMillis()- time > timer){
                            db.delete_entry(rowid,conn);
                            
                        }
                        
                    }}
                    else{
                    
                    
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
    
/*    private IndexingManager(){
//        Initialise function
}
*/
//  Creating Singleton object of IndexingManager class.   
    
//    public  synchronized IndexingManager getIndexingManager(){
//       if(indexManager==null) {
//           indexManager=new IndexingManager();
//           return indexManager;
//       }
//       else{
//           return indexManager;
//       }
//    }
    
    
    public static void main(String[] args) {
       
        maintenancethread();
    }
    
    
    
    
    
}
