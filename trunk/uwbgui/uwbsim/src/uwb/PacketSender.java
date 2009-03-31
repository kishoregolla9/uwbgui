package uwb;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class PacketSender {
	InetAddress theirAddress, myAddress;
	int myPort,theirPort;
	DatagramSocket clientSocket,serverSocket;
	
	public PacketSender() throws UnknownHostException{
		byte[] a = new byte[] {18,111,11,-11};
		
		theirAddress = InetAddress.getByAddress(a);
		theirPort = 9095;
		myAddress = InetAddress.getLocalHost();
		myPort = 9090;
		
		
		try {
			clientSocket = new DatagramSocket(myPort);
			//serverSocket = new DatagramSocket(myPort,myAddress);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendRadioInfo(int id, int update, int datatype, double x, double y, 
			double rad, double var) throws IOException{
		
		DatagramPacket packet = generatePacket(id, update, datatype, x, y, rad, var);
		
		clientSocket.send(packet);
	}
	
	public void sendRadioInfo(int id, int update, int datatype, double x1, double y1, 
			double rad1, double var1, double x2, double y2, 
			double rad2, double var2) throws IOException{
		
		DatagramPacket packet = generatePacket(id, update, datatype, x1, y1, 
				rad1, var1, x2, y2, rad2, var2);
		
		clientSocket.send(packet);
	}
	
	public DatagramPacket generatePacket(int id, int update, int datatype, double x, 
			double y, double rad, double var){
		
		int n = 7;
		byte[][] byteArrsToConcat = new byte[n][];
		byteArrsToConcat[0] = Converter.toByta(id);
		byteArrsToConcat[1] = Converter.toByta(update);
		byteArrsToConcat[2] = Converter.toByta(datatype);
		byteArrsToConcat[3] = Converter.toByta(x);
		byteArrsToConcat[4] = Converter.toByta(y);
		byteArrsToConcat[5] = Converter.toByta(rad);
		byteArrsToConcat[6] = Converter.toByta(var);
		
		int length = 0;
		for(int i = 0; i<n; i++){
			length = length + byteArrsToConcat[i].length;
		}
		
		byte[] concat = new byte[length];
		
		int start = 0;
		for(int i=0; i<n; i++){
			System.arraycopy(byteArrsToConcat[i],0,concat,start,byteArrsToConcat[i].length);
			start = start + byteArrsToConcat[i].length;
		}
		
		return new DatagramPacket(concat, concat.length, theirAddress, theirPort);
	}
	
	public DatagramPacket generatePacket(int id, int update, int datatype, double x, 
			double y, double z){
		String str = id + "," + update + "," + datatype + "," + x + "," + y + "," + z;
		byte[] concat = Converter.toByta(str.getBytes());
		return new DatagramPacket(concat, concat.length, theirAddress, theirPort);
	}
	
	public DatagramPacket generatePacket(int id, int update, int datatype, double x, 
			double y, double z, int id2, double x2, double y2, double z2){
		String str = id + "," + update + "," + datatype + "," + x + "," + y + "," + z + 
						"," + id2 + "," + x2 + "," + y2 + "," + z2;
		byte[] concat = Converter.toByta(str.getBytes());
		return new DatagramPacket(concat, concat.length, theirAddress, theirPort);
	}
	
	public DatagramPacket generatePacket(int id, int update, int datatype, double x1, double y1, 
			double rad1, double var1, double x2, double y2, double rad2, double var2){
		
		int n = 11;
		byte[][] byteArrsToConcat = new byte[n][];
		byteArrsToConcat[0] = Converter.toByta(id);
		byteArrsToConcat[1] = Converter.toByta(update);
		byteArrsToConcat[2] = Converter.toByta(datatype);
		byteArrsToConcat[3] = Converter.toByta(x1);
		byteArrsToConcat[4] = Converter.toByta(y1);
		byteArrsToConcat[5] = Converter.toByta(rad1);
		byteArrsToConcat[6] = Converter.toByta(var1);
		byteArrsToConcat[7] = Converter.toByta(x2);
		byteArrsToConcat[8] = Converter.toByta(y2);
		byteArrsToConcat[9] = Converter.toByta(rad2);
		byteArrsToConcat[10] = Converter.toByta(var2);
		
		int length = 0;
		for(int i = 0; i<n; i++){
			length = length + byteArrsToConcat[i].length;
		}
		
		byte[] concat = new byte[length];
		
		int start = 0;
		for(int i=0; i<n; i++){
			System.arraycopy(byteArrsToConcat[i],0,concat,start,byteArrsToConcat[i].length);
			start = start + byteArrsToConcat[i].length;
		}
		
		return new DatagramPacket(concat, concat.length, theirAddress, theirPort);
		
	}
	
	
	/*

	*/
	
	public void drawPath(int idno, int[] xcoords, int[] ycoords) throws IOException, InterruptedException{
		if(xcoords.length != ycoords.length){
			System.out.println("length of xcoords and ycoords do not match!");
			return;
		}
		for(int i=0;i<xcoords.length; i++){
			sendRadioInfo(idno,i+1,1,xcoords[i],ycoords[i],0,0);
			Thread.currentThread();
			Thread.sleep(50);
		}	
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		PacketSender ps = new PacketSender();
		int[] xcoords = {1,1,1,2,3,4,3,4,5,6};
		int[] ycoords = {5,4,3,3,4,3,4,5,6,7};
		ps.drawPath(12,xcoords,ycoords);
		ps.clientSocket.close();
	}
	
	
}
