package uwb;

import java.awt.Color;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Test {
	public static void main(String[] args) throws UnknownHostException{
		RadioLocation rl = new RadioLocation(2,"radio2",DataType.ONEPOINT,
				new Color(0,0,255),5,3);
		
	}
}
