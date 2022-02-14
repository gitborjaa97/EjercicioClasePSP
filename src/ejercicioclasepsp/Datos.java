
package ejercicioclasepsp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class Datos {
    
    private byte[] encodedKeyBase64;
    private byte[] keyHash;

    public Datos() {        
        encodedKeyBase64 = generarClave("AES");
        keyHash = generarHash(encodedKeyBase64);
    }
    
    private byte [] generarClave(String algoritmo){
        byte[] claveBase = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128, null);
            SecretKey sk = kg.generateKey();
            byte[] claveLisa = sk.getEncoded();
            claveBase = Base64.getEncoder().encode(claveLisa);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ServidorClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
        return claveBase;
    }
    
    private byte [] generarHash(byte [] recurso){
        byte[] hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(recurso);
            hash = md.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Datos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }

    public byte[] getEncodedKey() {
        return encodedKeyBase64;
    }

    public void setEncodedKey(byte[] encodedKey) {
        this.encodedKeyBase64 = encodedKey;
    }

    public byte[] getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(byte[] keyHash) {
        this.keyHash = keyHash;
    }
    
    
}
