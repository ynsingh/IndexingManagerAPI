import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PublicKey;
import java.security.cert.Certificate;

public class Testing {
    public static void main(String[] args) {
        IndexingManager IM=IndexingManager.getInstance();
        SignatureVerif S2 = SignatureVerif.getInstance();
        KeyStore k = S2.getKeyStore();
        Certificate c1 = null;
        try {
            c1 =  k.getCertificate("Certificate");
            PublicKey Key= c1.getPublicKey();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        //Database_Utility utility1 = Database_Utility.getInstance();
        //utility1.add_entry("HIIiiiiiiiii","hooo", (long) 1600,6,7,true ,"Mohsin",System.currentTimeMillis(), c1);
        IM.addIndex("index1","hooo", (long) 1600,6,7,true ,"Mohsin",13,System.currentTimeMillis(), c1);
    }
}
