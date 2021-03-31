

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.*;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

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
    private static int origLayerId;
    private static Long origTime;
    private static java.security.cert.Certificate origCerti;
    static PublicKey pub;

    
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
    
   public static void addEntry(String key, String value, int timer, int totalCopies, int copyNum, String timerType, int userId,Long time, java.security.cert.Certificate c){
       origkey=key;
       origvalue=value;
       origtime=timer;
       origtotalCopies=totalCopies;
       origcopyNum=copyNum;
       origtimerType=timerType;
       origuserId=userId;
       origLayerId=0;
       origTime=time;
       origCerti=c;

       //if(utility.search_userId(origuserId)){

          Connection conn = utility.getConnection();
           PreparedStatement stmt = null;
           try {
               stmt = conn.prepareStatement("select Certificate from keyvalue1 where userId=?");

               stmt.setInt(1,origuserId );

                ResultSet rs = null;

               rs = stmt.executeQuery();
               while(rs.next()){

                  byte[] t =rs.getBytes("Certificate");
                   CertificateFactory cf = CertificateFactory.getInstance("X.509");
                   Certificate cert = cf.generateCertificate(new ByteArrayInputStream(t));
                   System.out.println("Done");
                    pub = cert.getPublicKey();
                   System.out.println(pub);
           }

           } catch (SQLException e) {
               e.printStackTrace();
           } catch (CertificateException e) {
               e.printStackTrace();
           }

       //}

       Verif v=new Verif();
       boolean b = v.Verify_Digital_Signature(c, origvalue);
       if(b)

       {
           utility.add_entry(origkey, origvalue, origtime, origtotalCopies, origcopyNum, origtimerType, origuserId,origLayerId, origTime,origCerti);
       }
       else{

           System.out.println("Signature not verified");

       }
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
    
  private IndexingManager() {
      Provider provider = new BouncyCastleProvider();
      Security.addProvider(provider);
  }
//  Creating Singleton object of IndexingManager class.
    
   public  synchronized IndexingManager getIndexingManager(){
      if(indexManager==null) {
         indexManager=new IndexingManager();
           return indexManager;
       }
      else{
         return indexManager;
      }
    }
    
    
    public static void main(String[] args) {
        SignatureVerif S2 = SignatureVerif.getInstance();
        KeyStore k = S2.getKeyStore();
        Certificate c1 = null;
        try {
            c1 =  k.getCertificate("Certificate");
            PublicKey Key= c1.getPublicKey();
            if(Key==pub){
                System.out.println("hiiiiiiiiiiiiii");
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        addEntry("hiii","hooo",5,6,7,"F",8,System.currentTimeMillis(), c1);
        //maintenancethread();
    }
    
    
    
    
    
}
