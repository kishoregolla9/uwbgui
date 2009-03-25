package uwb;

import java.io.*;
import java.net.*;

public class DataThread extends Thread{
	protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected int port;
    
    public void run(){
    	try {
			socket = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        byte[] buf = new byte[256];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
			socket.receive(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Output: " + received);

        socket.close();
    }


}
