package uwb;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class UWBMainMATLAB {
	public static UWBShow initGUI() throws IOException, HeadlessException {
		UWBShow comp = new UWBShow();
        comp.setBorder(BorderFactory.createTitledBorder("Ultra-Wide Band Network Simulation"));

        // configure the applet's frame
        JFrame f = new JFrame("UWBSim");
        f.setBounds(0,0,300,300);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout( new BorderLayout() );
        f.getContentPane().add(comp,BorderLayout.CENTER);
        f.addComponentListener(new java.awt.event.ComponentAdapter() {
        	public void componentResized(ComponentEvent e) {
        		JFrame tmp = (JFrame)e.getSource();
        	    if (tmp.getWidth()<100 || tmp.getHeight()<100) {
        	    	tmp.setSize(100, 100);
        	    }
        	}
        });
        
        JPanel commPanel = new JPanel();
        commPanel.add(new JButton("this does nothing"));
        f.getContentPane().add(commPanel,BorderLayout.PAGE_END);
        
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        return comp;
	}
	
    public static void main(String[] args) throws IOException, InterruptedException {
        UWBShow gui = initGUI();
        int revision = 0;
        File file = new File("C:\\uwblocs.txt");
        BufferedReader br;
        
        while(true){
        	br = new BufferedReader(new FileReader(file));
        	String line = br.readLine();
        	
        	if (!line.substring(0,23).equals("UWB locations revision ")){
        		System.out.println("uh oh");
        		continue;
        	}
            int newrev = Integer.parseInt(line.substring(23,line.length()));
            if (newrev <= revision){
            	continue;
            }
            System.out.println(newrev);

            revision = newrev;
            gui.clearRadioLocs();
            RadioLocation rl;
            
            line = br.readLine();
            System.out.println(line);

            while(line != null){            	
            	String[] tokens = line.split(",");
            	for (int i = 0; i < tokens.length; i++){
            		System.out.println(tokens[i]);
            	}
            	int id = Integer.parseInt(tokens[0]);
            	int x = Integer.parseInt(tokens[1]);
            	int y = Integer.parseInt(tokens[2]);
            	rl = new RadioLocation(id,"radio"+id,new Color(0,0,255),x,y,
            			new double[][] {});
            	gui.addRadioLoc(rl);
            	line = br.readLine();
            	System.out.println(line);
            }
            gui.repaint();
        }
        /*
         * RadioLocation radio1 = new RadioLocation(1,"radio1",new Color(0,255,255),7,3,
        		new double[][] {{0.2,0.5},{0.45,1.5},{0.9,3}});
           RadioLocation radio2 = new RadioLocation(2,"radio2",new Color(128,128,255),1,9,
        		new double[][] {{0.3,0.5},{0.85,1}});
         */
        
        //gui.addRadioLoc(radio1);
        //gui.addRadioLoc(radio2);
        //f.repaint();
        
    }
}
