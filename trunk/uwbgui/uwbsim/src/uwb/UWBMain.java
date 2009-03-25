package uwb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UWBMain implements ActionListener {
	// VARIABLES YOU CAN MODIFY
	int myPort = 9096;
	int theirPort = 9090;
	
	UWBShow gui;
	JPanel sidebar;
	JPanel bottom;
	JTextArea textArea;
	JTextArea outputArea;
	DatagramSocket serverSocket,clientSocket;
	InetAddress myAddress;
	InetAddress theirAddress;
	boolean listen;
	
	
	public UWBMain() throws HeadlessException, IOException{
		gui = initGUI();
		myAddress = InetAddress.getLocalHost();
		theirAddress = InetAddress.getByAddress(new byte[] {18,24,1,38});
		//clientSocket = new DatagramSocket(myPort,myAddress);
		serverSocket = new DatagramSocket(myPort);
		serverSocket.setSoTimeout(200);
		gui.clearRadioLocs();
		listen = true;
	}
	
	public UWBShow initGUI() throws HeadlessException, IOException {
		//setMaximizedBounds(new java.awt.Rectangle(250, 250, 400, 400));
		
		UWBShow comp = new UWBShow();
        comp.setBorder(BorderFactory.createTitledBorder("Ultra-Wide Band Network Simulation"));
        comp.setPreferredSize(new java.awt.Dimension(700,700));
        
        sidebar = new JPanel();
        sidebar.setPreferredSize(new java.awt.Dimension(250,700));
        
        bottom = new JPanel();
        bottom.setPreferredSize(new java.awt.Dimension(950,100));

        // configure the applet's frame
        JFrame f = new JFrame("UWBSim");
        f.setPreferredSize(new java.awt.Dimension(950,800));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout( new BorderLayout() );
        f.getContentPane().add(comp,BorderLayout.CENTER);
        f.getContentPane().add(sidebar,BorderLayout.EAST);
        f.getContentPane().add(bottom,BorderLayout.SOUTH);
        /*
        f.addComponentListener(new java.awt.event.ComponentAdapter() {
        	public void componentResized(ComponentEvent e) {
        		
        		JFrame tmp = (JFrame)e.getSource();
        		//print(tmp.getWidth() + " " + tmp.getHeight());
        		if (tmp.getWidth() != tmp.getHeight()) {
        	    	int min = Math.min(tmp.getWidth()-250, tmp.getHeight());
        	    	tmp.setSize(min+250, min);
        		}
        	}
        });
        */
        // set up the sidebar
        textArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(textArea); 
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);

        // set up the output area
        outputArea = new JTextArea(5, 80);
        JScrollPane scrollOutput = new JScrollPane(outputArea); 
        outputArea.setEditable(false);
        outputArea.setWrapStyleWord(true);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        sidebar.add(scrollPane, c);
        bottom.add(scrollOutput,c);

        // set up the command panel
        JPanel commPanel = new JPanel();
        commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.PAGE_AXIS));

        
        JButton b = new JButton("Toggle packet listening on/off");
        b.setActionCommand("listening");
        b.addActionListener(this);
        b.setToolTipText("Click this button when the sender has stopped sending packets.");
        commPanel.add(b);
        
        JButton anchorButton = new JButton("Toggle anchors");
        anchorButton.setActionCommand("anchors");
        anchorButton.addActionListener(this);
        commPanel.add(anchorButton);
        
        JButton waypointButton = new JButton("Toggle waypoints");
        waypointButton.setActionCommand("waypoints");
        waypointButton.addActionListener(this);
        commPanel.add(waypointButton);
        
        JButton wallButton = new JButton("Toggle walls");
        wallButton.setActionCommand("walls");
        wallButton.addActionListener(this);
        commPanel.add(wallButton);
        
        sidebar.add(commPanel,BorderLayout.PAGE_END);
        
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        return comp;
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getActionCommand().equals("listening")){
			if(listen){
				listen = false;
			}
			else{
				listen = true;
			}
		}
		else if(e.getActionCommand().equals("anchors")){
			if(this.gui.anchorsShow == true){
				this.gui.anchorsShow = false;
			}
			else{
				this.gui.anchorsShow = true;
			}
			gui.repaint();
		}
		else if (e.getActionCommand().equals("waypoints")){
			if(this.gui.waypointsShow == true){
				this.gui.waypointsShow = false;
			}
			else{
				this.gui.waypointsShow = true;
			}
			gui.repaint();
		}
		else if (e.getActionCommand().equals("walls")){
			if(this.gui.wallsShow == true){
				this.gui.wallsShow = false;
			}
			else{
				this.gui.wallsShow = true;
			}
			gui.repaint();
		}
	}
	
	public void extractDataFromPacket(DatagramPacket p) throws IOException{
		ByteArrayInputStream bin = new ByteArrayInputStream(p.getData() );
		BufferedReader d = new BufferedReader(new InputStreamReader(bin));
		String[] tokens = d.readLine().split(",");
		for(int i = 0; i< tokens.length; i++){
			print(tokens[i]);
		}
		int id = Integer.parseInt(tokens[0]);
		int update = Integer.parseInt(tokens[1]);
		DataType datatype = DataType.convert(Integer.parseInt(tokens[2]));
		
		RadioLocation rl;
		if(datatype==DataType.TWOPOINTS){
			double x1 = Double.parseDouble(tokens[3]);
			double y1 = Double.parseDouble(tokens[4]);
			double rad1 = Double.parseDouble(tokens[5]);
			double var1 = Double.parseDouble(tokens[6]);
			double x2 = Double.parseDouble(tokens[7]);
			double y2 = Double.parseDouble(tokens[8]);
			double rad2 = Double.parseDouble(tokens[9]);
			double var2 = Double.parseDouble(tokens[10]);
			
			print(x1);
			print(y1);
			print(rad1);
			print(var1);
			print(x2);
			print(y2);
			print(rad2);
			print(var2);
			
			rl = new RadioLocation(id,"radio"+id,DataType.TWOPOINTS,
    				new Color(0,0,255),x1,y1,rad1,var1,x2,y2,rad2,var2);
		}
		
		else if(datatype==DataType.SIMPLE){
			double x1 = Double.parseDouble(tokens[3]);
			double y1 = Double.parseDouble(tokens[4]);
			double z1 = Double.parseDouble(tokens[5]);
			print("(" + x1 + "," + y1 + ")");
			rl = new RadioLocation(id,"radio"+id,DataType.SIMPLE,
    				new Color(0,0,255),x1,y1);
	    	// update the sidebar
	    	textArea.append(x1 + ", " + y1 + "\n");
	        //Make sure the new text is visible, even if there
	        //was a selection in the text area.
	        textArea.setCaretPosition(textArea.getDocument().getLength());
	    	
	    	print("hello");
		}
		
		else if(datatype==DataType.CIRCLE){
			double x1 = Double.parseDouble(tokens[3]);
			double y1 = Double.parseDouble(tokens[4]);
			double z1 = Double.parseDouble(tokens[5]);
			double rad1 = Double.parseDouble(tokens[6]);
			double var1 = Double.parseDouble(tokens[7]);
			
			print("(" + x1 + "," + y1 + "):" + " radius " + rad1 + ", variance " + var1);
			
			rl = new RadioLocation(id,"radio"+id,DataType.CIRCLE,
    				new Color(0,0,255),x1,y1,rad1,var1);
		}
		else{
			return;
		}
    	gui.addRadioLoc(rl,update);
    	gui.repaint();

    	return;
	}
	
	public void listenForPacketsAndUpdate() throws IOException{
		DatagramPacket packet = new DatagramPacket(new byte[256], 256);
		
		// will this wait for a packet?
		try{
			print("listening for packet");
			serverSocket.receive(packet);
		}catch(SocketTimeoutException e){
			//ignore; this is normal
			return;
		}
		print("received packet.");
        extractDataFromPacket(packet);
	}
	
	public void print(String str){
		outputArea.append(str+"\n");
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
        System.out.println(str);
	}
	
	public void print(double d){
		print(Double.toString(d));
	}
	
	
    public static void main(String[] args) throws InterruptedException, HeadlessException, IOException {
        UWBMain uwbm = new UWBMain();
        //print("initialize object");
        
        /*
        //testing code: generate packets on this computer and read them
        PacketSender ps = new PacketSender();
        DatagramPacket packet1 = ps.generatePacket(2, // id number
        		1, // update number 
        		0, // data type 
        		15,27,0); // x,y,z coords
        uwbm.extractDataFromPacket(packet1);
        
        packet1 = ps.generatePacket(2, // id number
        		2, // update number 
        		0, // data type 
        		18,31,0); // x,y,z coords
        uwbm.extractDataFromPacket(packet1);
        */
        System.out.println(System.getProperty("user.dir"));
        
        while(true){
        	if(uwbm.listen){
        		if(uwbm.serverSocket.isClosed()){
        			uwbm.serverSocket = new DatagramSocket(uwbm.myPort);
					uwbm.serverSocket.setSoTimeout(200);
        		}
        		uwbm.listenForPacketsAndUpdate();
        	}
        	else{
        		if(!uwbm.serverSocket.isClosed()){
    				uwbm.serverSocket.close();
            		uwbm.print("Socket closed.");
        		}
        	}
        }
    }
}
