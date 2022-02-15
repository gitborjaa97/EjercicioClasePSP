
package ejercicioclasepsp;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.IOUtils;

public class HiloServidorNormal implements Runnable{
    
    SecretKeySpec key;
    Socket s;

    public HiloServidorNormal(Socket s, DatosServidorCliente datos) {
        this.s = s;
        key = datos.getKey();
    }
    
    @Override
    public void run() {
        System.out.println("Hilo arrancado");
        OutputStream enviar = getWriter(s);
        BufferedReader recibir = getReader(s);
        try {
            Cipher cifrador = Cipher.getInstance(key.getAlgorithm());
            cifrador.init(Cipher.DECRYPT_MODE, key);
            
            byte[] mensajeSinCifrar = cifrador.doFinal(recibirMensaje(recibir));
            System.out.println("Mensaje cliente:");
            System.out.println(new String(mensajeSinCifrar));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private byte [] recibirMensaje(BufferedReader br){
        byte[] mensaje = null;
        try {
            String mensajeBase64 = br.readLine();
            mensaje = Base64.getDecoder().decode(mensajeBase64);
        } catch (IOException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensaje;
    }
    
    private BufferedReader getReader(Socket s){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return br;
    }
    
    private OutputStream getWriter(Socket s){
        OutputStream os = null;
        try {
            os = s.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return os;
    }
    
//    private InputStream getReader(Socket s){
//        InputStream is = null;
//        try {
//            is = s.getInputStream();
//        } catch (IOException ex) {
//            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return is;
//    }
    
}
