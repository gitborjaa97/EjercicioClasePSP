
package ejercicioclasepsp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

public class DatosServidorCliente {
    
    
    private SecretKeySpec key;

    public DatosServidorCliente(byte [] claveBase64, byte [] hash) {
        claveBase64 =  limpiarMensaje(claveBase64);
        boolean valido = validarHash(limpiarMensaje(hash), claveBase64);
        if(valido){
            key = generarClave(claveBase64);
        }
    }
    
    public SecretKeySpec getKey(){
        return key;
    }
    
    private SecretKeySpec generarClave(byte [] claveBase64){
        byte[] clave = Base64.getDecoder().decode(claveBase64);
        return new SecretKeySpec(clave, "AES");
    }
    
    private byte [] limpiarMensaje(byte [] mensaje){
        int longitud = 0;
        for (int i = mensaje.length -1; i >= 0 && longitud == 0; --i) {
            if(mensaje[i] != 0){
                longitud = i +1;
            }
        }
        byte[] mensajeLimpio = new byte[longitud];
        for (int i = 0; i < longitud; i++) {
            mensajeLimpio[i] = mensaje[i];
        }
        return mensajeLimpio;
    }
    
    private boolean validarHash(byte [] hash, byte [] clave){
        String sHash = Base64.getEncoder().encodeToString(hash);        
        String sClave = Base64.getEncoder().encodeToString(obtenerHash(clave));
        
        return sHash.equals(sClave);
    }
    
    private byte [] obtenerHash(byte [] recurso){
        byte [] hash =null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(recurso);
            hash = md.digest();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }
}
