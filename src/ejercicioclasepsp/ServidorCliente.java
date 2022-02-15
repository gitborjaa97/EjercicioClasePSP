
package ejercicioclasepsp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
        byte[] claveBase64 = recibido.getData();
        enviarMensaje(s, ipServidor, puerto);
        recibido = recibirMensaje( s);
        byte[] hash = recibido.getData();
        DatosServidorCliente datos = new DatosServidorCliente(claveBase64, hash);
        
        ServerSocket sSocket = getSSocket(7800);
        
        while (true) {            
            Socket socket = aceptarSocket(sSocket);
            Thread hilo = new Thread(new HiloServidorNormal(socket, datos));
            hilo.start();
        }
    }
    
    private static Socket aceptarSocket(ServerSocket ss){
        Socket s = null;
        try {
            s = ss.accept();
        } catch (IOException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }        
        return s;
    }
    
    private static ServerSocket getSSocket(int port){
        ServerSocket s = null;
        try {
            s = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(ServidorCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return s;
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
