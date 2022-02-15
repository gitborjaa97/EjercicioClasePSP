
package ejercicioclasepsp;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.*;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class Cliente {
    
        
    private static InetAddress ipServidor;
    private static int puerto;
    
    
    public static void main(String[] args) {        
        DatagramSocket s = crearSocket();
        try {
            enviarMensaje(s, InetAddress.getByName("127.0.0.1"), 8600);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        DatagramPacket recibido = recibirMensaje( s);
        configurarDireccion(recibido);
        byte[] claveBase64 = limpiarMensaje(recibido.getData());
        enviarMensaje(s, ipServidor, puerto);
        recibido = recibirMensaje( s);
        byte[] hash = recibido.getData();
        if(validarHash(limpiarMensaje(hash), claveBase64)){
            Socket socket = getSocket(7800, "127.0.0.1");
            
            BufferedWriter os = getWriter(socket);
            
            String mensajeEnviar = "Hola caracola";
            byte[] mensajeCifrado = cifrarMensaje(generarClave(claveBase64),
                    mensajeEnviar.getBytes());
            enviarMensaje(os, mensajeCifrado);
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    private static void enviarMensaje(BufferedWriter os, byte [] mensaje){
        try {
            os.write(Base64.getEncoder().encodeToString(mensaje)+"\n");
            os.flush();
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static SecretKeySpec generarClave(byte [] claveBase64){
        byte[] clave = Base64.getDecoder().decode(claveBase64);
        return new SecretKeySpec(clave, "AES");
    }
    
    private static byte [] cifrarMensaje(SecretKeySpec sks, byte [] mensaje){
        byte[] cifrado = null;
        try {
            Cipher c = Cipher.getInstance(sks.getAlgorithm());
            c.init(Cipher.ENCRYPT_MODE, sks);
            cifrado = c.doFinal(mensaje);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cifrado;
    }
    
    private static  BufferedWriter getWriter(Socket s){
        BufferedWriter os = null;
        try {
            os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        } catch (IOException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return os;
    }
    
    private  static InputStream getReader(Socket s){
        InputStream is = null;
        try {
            is = s.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(HiloServidorNormal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return is;
    }
    
    private static Socket getSocket(int puerto, String ip){
        Socket s = null;
        try {
            s = new Socket(InetAddress.getByName(ip), puerto);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
    
    private static byte [] limpiarMensaje(byte [] mensaje){
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
    
    private static boolean validarHash(byte [] hash, byte [] clave){
        String sHash = Base64.getEncoder().encodeToString(hash);        
        String sClave = Base64.getEncoder().encodeToString(obtenerHash(clave));
        
        return sHash.equals(sClave);
    }
    
    private static byte [] obtenerHash(byte [] recurso){
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
    
    private static DatagramSocket crearSocket(){
        DatagramSocket s = null;
        try {
            s = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
    }
    
    private static void enviarMensaje(DatagramSocket s, InetAddress dir, int puerto){        
        try {
            DatagramPacket p = new DatagramPacket(new byte[1],
                    1,dir, puerto);
            s.send(p);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static DatagramPacket recibirMensaje( DatagramSocket s){
        DatagramPacket dp = null;
        try {  
            byte[] buffer = new byte[200];
            dp = new DatagramPacket(buffer, 200);
            s.receive(dp);
        } catch (IOException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dp;
    }
    
    private static void configurarDireccion(DatagramPacket dp){
        ipServidor = dp.getAddress();
        puerto = dp.getPort();
    }

}
