package src.main;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;

import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

import java.security.cert.X509Certificate;
import java.util.Date;



/**Purpose of this class is testing.It is used for generating public and private key,keystore,dummy certificate,
 * putting private key and certificate in the keystore, retrieval of private key from the keystore.
 * In short it performs cryptographic functions.
 */
public class Cryptography {

    /**
     * Creating variable of Interface Public Key.
     */
    private static PublicKey publicKey;
    /**
     * Creating variable of Interface Private Key.
     */
    private static PrivateKey privateKey;
    /**
     * Creating object of Class KeyStore.
     */
    private static KeyStore keyStore;
    /**
     * Creating object of Class Cryptography.
     */
    private static Cryptography signatureVerif;
    /**
     * Variable to store password for Key Store.
     */
    private static final char[] password = "abc@123".toCharArray();

    /**
     * This is constructor of class used to call required functions as per availability of keystore.
     */
    private Cryptography() {

  // Adding BouncyCastle provider by adding a jar file.

        Provider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        try {
            keyStore = KeyStore.getInstance("JCEKS");
            String filePath = "keyStore.ks";
            File f = new File(filePath);
            boolean b = f.exists();
            if (!b) {
                keyStore.load(null, null);
                keyPairGeneration();
                saveToKeyStore();
                saveKeyStore();

            } else {
                loadKeyStore();
                KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
                KeyStore.PrivateKeyEntry privateKeyEntry = null;
                privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry("Private Key", protectionParameter);
                privateKey = privateKeyEntry.getPrivateKey();
                java.security.cert.Certificate certificate =  keyStore.getCertificate("Certificate");

                publicKey = certificate.getPublicKey();
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }
    }



    /** Creating Singleton object of Cryptography Class.
     * @return Object of Cryptography Class.
     */
    public static synchronized Cryptography getInstance() {
        if (signatureVerif == null) {
            signatureVerif = new Cryptography();
        }
        return signatureVerif;
    }

    /**
     * This method is used for key pair generation.
     */
    private void keyPairGeneration() {
        try {
            String ALGORITHM = "RSA";
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, "BC");
            keyPairGenerator.initialize(1024, new SecureRandom());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
            System.out.println("Key Pair Generated");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {

        }
    }

    /**
     * This method is used for loading keystore to package.
     */
    private void loadKeyStore() {
        try {

            FileInputStream fis = new FileInputStream("keyStore.ks");
            //keyStore.load(null,null);
            keyStore.load(fis, password);
            fis.close();
            System.out.println("Key Store Loaded");
        } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used for saving Keystore.
     */
    private void saveKeyStore() {
        FileOutputStream fos;
        try {

            fos = new FileOutputStream("keyStore.ks");
            keyStore.store(fos, password);
            fos.flush();
            fos.close();
        } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used for saving private key to Keystore.
     */

    @SuppressWarnings("deprecation")
    private void saveToKeyStore() {
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
        X509Certificate certificate = generateCertificate();
        java.security.cert.Certificate[] certChain = (java.security.cert.Certificate[]) new Certificate[1];
        certChain[0] = certificate;
        KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, certChain);
        try {
            keyStore.setEntry("Private Key", privateKeyEntry, protectionParameter);
            keyStore.setCertificateEntry("Certificate", certChain[0]);
            System.out.println("Private key stored to KeyStore");
        } catch (KeyStoreException e) {

        }
    }

    /** This method is used to get private key from Key Store.
     * @return Private Key.
     */
      PrivateKey getFromKeyStore() {
         KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
         KeyStore.PrivateKeyEntry privateKeyEntry = null;
         try {
             privateKeyEntry = (KeyStore.PrivateKeyEntry) getKeyStore().getEntry("Private Key", protectionParameter);
          } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException e) {
              e.printStackTrace();
        }
        assert privateKeyEntry != null;
        return privateKeyEntry.getPrivateKey();
       }

    /** This method is used for generating certificate.
     * @return Certificate.
     */

    @SuppressWarnings("deprecation")
    private X509Certificate generateCertificate() {
        // build a certificate generator
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        String CERTIFICATE_DN = "CN = cn , O = o, L =L ,ST = i1, C = c";
        X500Principal dnName = new X500Principal(CERTIFICATE_DN);

        // add some options
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName);
        // Set not before Yesterday
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        // Set not after 2 years
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 2L * 365 * 24 * 60 * 60 * 1000));
        certGen.setPublicKey(publicKey);
        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        //certGen.addExtension(X509Extensions.ExtendedKeyUsage, true,new ExtendedKeyUsage(KeyPurposeId.id_kp_timeStamping));

        // Finally, sign the certificate with the private key
        X509Certificate cert = null;
        try {
            cert = certGen.generate(privateKey, "BC");
            System.out.println("Certificate Generated");
        } catch (CertificateEncodingException | InvalidKeyException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException e) {
        }
        return cert;
    }

    /**As methods and object of class is private this method is used to get public key. and keystore.
     * @return Public Key
     */
    PublicKey getPublicKey() {
        return publicKey;
    }

    /** As methods and object of class is private this method is used to get keystore.
     * @return KeyStore
     */
    public KeyStore getKeyStore() {
        return keyStore;
    }

}
