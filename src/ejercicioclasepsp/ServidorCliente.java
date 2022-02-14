
package ejercicioclasepsp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

public class ServidorCliente {
    
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
        byte[] hash = limpiarMensaje(recibido.getData());
        boolean valido = validarHash(hash, claveBase64);
        
           
    }
    
    private static SecretKeySpec reconstruirClave(){
        
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
