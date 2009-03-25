package uwb;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;
 
public class UWBShow extends JPanel {
	// VARIABLES YOU CAN MODIFY
	String paramFileLoc = "params.txt";

	// default values
	double minX = 0;
    double maxX = 20;
    double minY = 0;
    double maxY = 20;
    double gridSizeX = 5;
    double gridSizeY = 5;
	Color WAYPOINT_COLOR = new Color(124,252,0); //lawn green
	Color ANCHOR_COLOR = new Color(255, 140, 0); //dark orange
	Color WALL_COLOR = new Color(0,0,0); // black
	
	// other internal variables
    int canvasWidth, canvasHeight, x0, y0;
    double[][] waypoints, anchors;
    ArrayList<ArrayList<Double>> walls;
    
    boolean waypointsShow = true, anchorsShow = true, wallsShow = true, anchorCoordsShow = true;
    
    // testing variables
    //RadioLocation radio0 = new RadioLocation(0,"radio0",new Color(255,255,0),4,5,
    //		new double[][] {{0.6,1},{0.7,2},{0.9,3.5}});
    
    // three synchronized ArrayLists
    private ArrayList<RadioLocation> rls = new ArrayList<RadioLocation>(10);
    private ArrayList<Integer> rIds = new ArrayList<Integer>(10);
    private ArrayList<Integer> rRevisionNos = new ArrayList<Integer>(10);
 
    public UWBShow() throws IOException {
    	// populate lists of anchors and waypoints
    	updateStartParams();
    }
    
    public int getIndexNo(int id){
    	// search through rIds and check each to see if it matches id
    	for(int i=0; i<rIds.size(); i++){
    		if(rIds.get(i).intValue() == id){
    			return i;
    		}
    	}
    	return -1;
    }
    
    public int getRevisionNo(int id){
    	int index = getIndexNo(id);
		return rRevisionNos.get(index).intValue();
    }
    
    public boolean addRadioLoc(RadioLocation rl, int revNum){
    	int index = getIndexNo(rl.idno);
    	
    	if(index == -1){ // so the id number of this radio location has not been added yet
    		rls.add(rls.size(),rl);
    		rIds.add(rl.idno);
    		rRevisionNos.add(revNum);
    		System.out.println("adding radio; was not here before.");
    		return true;
    	}
    	else{
    		System.out.println("Index: " + index);
        	System.out.println("ID number: " + this.rIds.get(index));
        	System.out.println("Revision number: " + this.rRevisionNos.get(index));
        	
    		// find where rl is in rls by searching by index
    		int r = getRevisionNo(rl.idno);
    		if (r < revNum){
    			System.out.println("Data provided by packet is more recent.  Update!");
    			this.rls.set(index, rl);    						// replace the RadioLocation with a new one
    			this.rRevisionNos.set(index,new Integer(revNum));	// change the revision number.
    			return true;
    		}
    		else{
    			System.out.println("Stale data in packet.  Ignore.");
    			return false;
    		}
    	}
    	
    }
    
    public void remRadioLoc(RadioLocation rl){
    	this.rls.remove(rl);
    }
    
    public void clearRadioLocs(){
    	this.rls.clear();
    }
 
    // graphics methods
    
    public int getDispX(double worldX){
    	// returns a value between 0 and canvasWidth
    	return (int) Math.floor(canvasWidth*((worldX - minX)/(maxX-minX)))+x0;
    }
    
    public int getDispY(double worldY){
    	// returns a value between 0 and canvasHeight
    	return (int) Math.floor(canvasHeight*(1-(worldY - minY)/(maxY-minY)))+y0;
    }
    
    public String doubleToString(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
    	return Double.toString(Double.valueOf(twoDForm.format(d)));
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

    	// set initial size of canvas
    	Insets insets = getInsets();
        canvasWidth = getWidth() - insets.left - insets.right;
        canvasHeight = getHeight() - insets.top -insets.bottom;
        x0 = insets.left;
        y0 = insets.top;
        //System.out.println(y0);
        
        g2.setPaint(Color.WHITE);
        g2.fillRect(x0,y0,canvasWidth, canvasHeight);
        
        g2.setPaint(new Color(200,200,200));
        
        // draw axes: thicker stroke
        /*
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(x0+getDispX(minX), y0, x0+getDispX(minX), 0+canvasHeight);
        g2.drawLine(x0, getDispY(0), x0+canvasWidth, getDispY(minY));
        g2.setStroke(new BasicStroke(1));
        */
        
        // draw vertical grid lines      
        for(double count = 0; count <= (maxX-minX)/gridSizeX; count++){
        	g2.drawLine(getDispX(minX+count*gridSizeX), y0, getDispX(minX+count*gridSizeX), y0+canvasHeight);
        	char[] label = doubleToString(minX+count*gridSizeX).toCharArray();
        	g2.drawChars(label, 0, label.length, x0+getDispX(minX+count*gridSizeX), y0+canvasHeight-2);
        }
        
        // draw horizontal grid lines
        for(double count = 0; count <= (maxY-minY)/gridSizeY; count++){
        	g2.drawLine(x0, getDispY(minY+count*gridSizeY), x0+canvasWidth, getDispY(minY+count*gridSizeY));
        	if (count != 0){
        		char[] label = doubleToString(minY+count*gridSizeY).toCharArray();
        		g2.drawChars(label, 0, label.length, x0+7, getDispY(minY+count*gridSizeY)-2);
        	}
        }

        g2.setPaint(Color.BLACK);
        
        // add anchors and waypoints
        if(anchorsShow){
        	addAnchors(g2, ANCHOR_COLOR);
        }
        if(waypointsShow){
        	addWaypoints(g2, WAYPOINT_COLOR);
        }
        if(wallsShow){
        	addWalls(g2,WALL_COLOR);
        }
        
        for(int i = 0; i<rls.size(); i++){
        	plotRadio(g2,rls.get(i));
        }
    }
 
    public void plotPoint(Graphics2D g2, double x, double y){
    	// convert to coordinates in frame
    	if((x < minX)||(x > maxX)||(y<minY)||(y>maxY)){
    		return;
    	}
    	int radius = Math.min(canvasWidth,canvasHeight)/100;
    	int realX = getDispX(x)-radius;
    	int realY = getDispY(y)-radius;
    	g2.fill(new Ellipse2D.Double(realX, realY, 2*radius, 2*radius));
    }
    
    public void plotCross(Graphics2D g2, double x, double y){
    	// convert to coordinates in frame
    	if((x < minX)||(x > maxX)||(y<minY)||(y>maxY)){
    		return;
    	}
    	int radius = Math.min(canvasWidth,canvasHeight)/100;
    	int realX = getDispX(x);
    	int realY = getDispY(y);
    	g2.drawLine(realX - radius, realY - radius, realX + radius, realY + radius);
    	g2.drawLine(realX + radius, realY - radius, realX - radius, realY + radius);
    }
    
    public Shape circle(double x, double y, double radius){
    	// convert to coordinates in frame
    	double realRadiusX = Math.floor(radius*canvasWidth/(maxX-minX));
    	double realRadiusY = Math.floor(radius*canvasHeight/(maxY-minY));
    	double realX = getDispX(x)-realRadiusX;
    	double realY = getDispY(y)-realRadiusY;
    	return new Ellipse2D.Double(realX, realY, 2*realRadiusX, 2*realRadiusY);
    }
    
    public void plotRadio(Graphics2D g2, RadioLocation rl){
    	DataType dt = rl.getDataType();
    	Color tempColor = g2.getColor();
    	if(dt == DataType.CIRCLE){
    		g2.setColor(rl.getColor());
    		g2.draw(circle(rl.x1,rl.y1,rl.rad1-rl.var1));
    		g2.draw(circle(rl.x1,rl.y1,rl.rad1+rl.var1));
    	}
    	else if(dt == DataType.TWOPOINTS){
    		g2.setColor(rl.getColor());
    		double[] intersections = rl.twoPointsReturn();
    		plotPoint(g2,intersections[0],intersections[1]);
    		plotPoint(g2,intersections[2],intersections[3]);
    	}
    	else if(dt == DataType.SIMPLE){
    		g2.setColor(rl.getColor());
    		plotPoint(g2, rl.x1, rl.y1);
    	}
    	g2.setColor(tempColor);
    }
    
    
    public Color certaintyToColor(double certainty){
    	return new Color( (float) (1-certainty), 0, (float) certainty);
    }
    public void addAnchors(Graphics2D g2, Color c){
    	Color tempColor = g2.getColor();
    	g2.setColor(c);
    	for(int i=0; i<anchors.length; i++){
    		plotPoint(g2, anchors[i][0], anchors[i][1]);
    		if(anchorCoordsShow){
    			String label = "(" + doubleToString(anchors[i][0])+ "," + doubleToString(anchors[i][1]) + ")";
    			g2.drawChars(label.toCharArray(),0,label.length(),getDispX(anchors[i][0]), getDispY(anchors[i][1]));
    		}
    	}
    	g2.setColor(tempColor);
    }
    
    public void addWaypoints(Graphics2D g2, Color c){
    	Color tempColor = g2.getColor();
    	g2.setColor(c);
    	g2.setStroke(new BasicStroke(2));
    	for(int i=0; i<waypoints.length; i++){
    		plotCross(g2, waypoints[i][0], waypoints[i][1]);
    	}
    	g2.setColor(tempColor);
    	g2.setStroke(new BasicStroke(1));
    }
    public void addWalls(Graphics2D g2, Color c){
    	Color tempColor = g2.getColor();
    	g2.setColor(c);
    	g2.setStroke(new BasicStroke(3));
    	//Math.floor(canvasWidth*((worldX - minX)/(maxX-minX)))+x0;
    	//    public int getDispX(double worldX){
    	// returns a value between 0 and canvasWidth
    	//return (int) Math.floor(canvasWidth*((worldX - minX)/(maxX-minX)))+x0;
    	//}
    	for(int i=0; i<walls.size(); i=i+2){
    		for(int j=0; j<walls.get(i).size(); j++){
    			//plotPoint(g2, walls.get(i).get(j), walls.get(i+1).get(j));
    			if(j<walls.get(i).size()-1){
    				g2.drawLine(getDispX(walls.get(i).get(j).doubleValue()),
    						getDispY(walls.get(i+1).get(j).doubleValue()),
    						getDispX(walls.get(i).get(j+1).doubleValue()),
    						getDispY(walls.get(i+1).get(j+1).doubleValue()));
    			}
    		}
    	}
    	g2.setStroke(new BasicStroke(1));
    	g2.setColor(tempColor);
    }
    
    public void updateStartParams(){
    	try {
			BufferedReader in = new BufferedReader(new FileReader(paramFileLoc));
			while(in.ready()){
				String infoType = in.readLine().trim();
				if(infoType.contains("grid")){
					while(in.ready()){
						String line = in.readLine().trim();
						if(line.equals("}")){
							break;
						}
						String[] tokens = line.split(" ");
						if(tokens[0].equals("minX")){
							minX = Double.parseDouble(tokens[1]);
						}
						if(tokens[0].equals("maxX")){
							maxX = Double.parseDouble(tokens[1]);
						}	
						if(tokens[0].equals("gridSizeX")){
							gridSizeX = Double.parseDouble(tokens[1]);
						}
						if(tokens[0].equals("minY")){
							minY = Double.parseDouble(tokens[1]);
						}
						if(tokens[0].equals("maxY")){
							maxY = Double.parseDouble(tokens[1]);
						}
						if(tokens[0].equals("gridSizeY")){
							gridSizeY = Double.parseDouble(tokens[1]);
						}
					}
				}
				if(infoType.contains("anchors")){
					int len = Integer.parseInt(in.readLine().trim());
					this.anchors = new double[len][2];
					for(int i = 0; i < len; i++){
						String[] tokens = in.readLine().split(" ");
						/*
						System.out.println(tokens[0]);
						System.out.println(tokens[1]);
						System.out.println(tokens[2]);
						*/
						anchors[i][0] = Double.parseDouble(tokens[0]);
						anchors[i][1] = Double.parseDouble(tokens[1]);
						//String label = tokens[2];
					}
				}
				else if(infoType.contains("waypoints")){
					int len = Integer.parseInt(in.readLine().trim());
					this.waypoints = new double[len][2];
					for(int i = 0; i < len; i++){
						String[] tokens = in.readLine().split(" ");
						/*
						System.out.println(tokens[0]);
						System.out.println(tokens[1]);
						System.out.println(tokens[2]);
						*/
						waypoints[i][0] = Double.parseDouble(tokens[0]);
						waypoints[i][1] = Double.parseDouble(tokens[1]);
						//String label = tokens[2];
					}
				}
				else if(infoType.contains("walls")){
					this.walls = new ArrayList<ArrayList<Double>>();
					
					ArrayList<Double> currentWallX =  new ArrayList<Double>();
					ArrayList<Double> currentWallY =  new ArrayList<Double>();
					walls.add(currentWallX);
					walls.add(currentWallY);
					
					while(in.ready()){
						String line = in.readLine().trim();
						if(line.equals("}")){
							break;
						}
						
						if (line.equals("---")){
							// new sequence of wall-points
							currentWallX = new ArrayList<Double>();
							currentWallY = new ArrayList<Double>();
							walls.add(currentWallX);
							walls.add(currentWallY);
						}
						else if (line.equals("")){
							continue;
						}	
						else{
							String[] tokens = line.split(" ");
							currentWallX.add(new Double(tokens[0]));
							currentWallY.add(new Double(tokens[1]));
						}
					}
				}
			}
			in.close();
		} catch (Exception e) {
			System.out.println("Error reading information file.");
			e.printStackTrace();
		}
    }
}
