package src.main;

import java.nio.charset.StandardCharsets;
import java.security.*;

import java.security.cert.Certificate;
import java.util.Base64;


//This class is used to verify digital signature of incoming value for indexing.

public class Verif {

 //This method performs verification and return boolean value as per result.

    public boolean Verify_Digital_Signature(Certificate c, String s) {

 // Creating object of SignatureVerif class for accessing keystore and its methods for cryptographic functions.

        SignatureVerif S1 = SignatureVerif.getInstance();
        Signature signature = null;
        String signatureData = null;
        byte[] sigData = null;
        try {
            signature = Signature.getInstance("SHA1WithRSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

 // Following block is signing value using private key from keystore for testing purpose.

        try {
            signature.initSign(S1.getFromKeyStore());
            signature.update(s.getBytes());
            sigData = signature.sign();
            byte[] base1 = Base64.getEncoder().encode(sigData);
            signatureData = new String(base1);
            System.out.println("Value is signed using Private Key");
        } catch (InvalidKeyException | SignatureException e) {
            System.out.println("In catch");
        }

   //    Following block is verifying digital signature using public key from Certificate.

        PublicKey pk = c.getPublicKey();
        boolean verify = false;
        byte[] baseSign = Base64.getDecoder().decode(signatureData);
        byte[] data = s.getBytes(StandardCharsets.UTF_8);
        try {

            signature.initVerify(pk);
            signature.update(data);
            verify = signature.verify(baseSign);
        } catch (InvalidKeyException | SignatureException e) {

        }
        return verify;
    }

    public static void main(String args[]) {

        java.security.cert.Certificate c1 = null;

        try {
            SignatureVerif S2 = SignatureVerif.getInstance();
            KeyStore k = S2.getKeyStore();
            c1 = k.getCertificate("Certificate");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        boolean v = new Verif().Verify_Digital_Signature( c1, "Hello");
        System.out.println(v);


    }
}


