
package ejercicioclasepsp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class ServidorClaves {
    
    public static void main(String[] args) {
        
        try {
            DatagramSocket socket = new DatagramSocket(8600);
            Datos datos = new Datos();
            while(true){
                DatagramPacket paquete = new DatagramPacket(new byte[1], 1);
                socket.receive(paquete);
                
                Thread hilo = new Thread(new HiloComunicacionesClaves(datos, paquete));
                hilo.start();
            }
        } catch (SocketException ex) {
            Logger.getLogger(ServidorClaves.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServidorClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
