
package ejercicioclasepsp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiloComunicacionesClaves implements Runnable{

    Datos datos;
    private InetAddress ip;
    private int puerto;

    public HiloComunicacionesClaves(Datos datos, DatagramPacket dp) {
        this.datos = datos;
        ip = dp.getAddress();
        puerto = dp.getPort();
    }
    
    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket();
            
            //Enviamos clave
            System.out.println(datos.getEncodedKey().length);
            System.out.println(datos.getKeyHash().length);
            enviarMensaje(socket, datos.getEncodedKey());
            //RecibirOK
            recibirMensaje(socket);
            //Enviamos Hash
            enviarMensaje(socket, datos.getKeyHash());
            //Recibimos Ok
            recibirMensaje(socket);
            
        } catch (SocketException ex) {
            Logger.getLogger(HiloComunicacionesClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private  void enviarMensaje(DatagramSocket socket, byte [] mensaje){        
        try {
            DatagramPacket paquete = new DatagramPacket(mensaje, mensaje.length, ip, puerto);
            socket.send(paquete);
        } catch (IOException ex) {
            Logger.getLogger(HiloComunicacionesClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void recibirMensaje(DatagramSocket s){        
        try {
            byte[] buffer = new byte[1];
            DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
            s.receive(dp);
        } catch (IOException ex) {
            Logger.getLogger(HiloComunicacionesClaves.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
