package src.main;

import java.nio.charset.StandardCharsets;
import java.security.*;

import java.security.cert.Certificate;
import java.util.Base64;

//This class is used to verify digital signature of value incoming for indexing.

public class Verif {

 //This method performs verification and return boolean value as per result.

    public boolean Verify_Digital_Signature(Certificate c, String s) {

 // Creating object of Cryptography class for accessing keystore and its methods for cryptographic functions.

        Cryptography S1 = Cryptography.getInstance();
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

}


