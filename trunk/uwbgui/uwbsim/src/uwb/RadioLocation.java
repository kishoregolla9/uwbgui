package uwb;

public class RadioLocation {
	private DataType dt;
	int idno;
	double x1, y1, rad1, var1, x2, y2, rad2, var2;
	String label;
	//Color color; // deprecated!
	
	public RadioLocation(int idno, String label, DataType dt, double x1,
			double y1, double rad1, double var1, double x2, double y2, double rad2, double var2){
		this.dt = DataType.TWOPOINTS;
		this.x1 = x1;
		this.y1 = y1;
		this.rad1 = rad1;
		this.var1 = var1;
		this.x2 = x2;
		this.y2 = y2;
		this.rad2 = rad2;
		this.var2 = var2;
		this.label = label;
		this.idno = idno;
	}
	
	public RadioLocation(int idno, String label, DataType dt, double x1,
			double y1, double rad1, double var1){
		this.dt = DataType.CIRCLE;
		this.x1 = x1;
		this.y1 = y1;
		this.rad1 = rad1;
		this.var1 = var1;
		this.label = label;
		this.idno = idno;
	}
	
	public RadioLocation(int idno, String label, DataType dt, double x1,
			double y1){
		this.dt = DataType.SIMPLE;
		this.x1 = x1;
		this.y1 = y1;
		this.label = label;
		this.idno = idno;
	}
	
	public String getLabel(){
		return this.label;
	}
	
	public DataType getDataType(){
		return this.dt;
	}
	
	// not used.  computes the intersection of two circles
	public double[] twoPointsReturn(){
		if(this.dt != DataType.TWOPOINTS){
			return null;
		}
		// compute locations
		double dist = Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
		double aPlusB = dist;
		double aMinusB = (Math.pow(rad1,2) - Math.pow(rad2, 2))/dist;
		double a = 0.5*(aPlusB + aMinusB);
		// double b = 0.5*(aPlusB - aMinusB);
		double perp = Math.sqrt(Math.pow(rad1,2)-Math.pow(a,2));
		double i1x = x1 + a*(x2-x1)/dist - perp*(y2-y1)/dist;
		double i1y = y1 + a*(y2-y1)/dist + perp*(x2-x1)/dist;
		double i2x = x1 + a*(x2-x1)/dist + perp*(y2-y1)/dist;
		double i2y = y1 + a*(y2-y1)/dist - perp*(x2-x1)/dist;
		double[] output = {i1x, i1y, i2x, i2y};
		return output;
	}
}
