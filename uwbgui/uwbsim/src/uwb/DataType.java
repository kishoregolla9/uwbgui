package uwb;

enum DataType{
	TWOPOINTS,
	CIRCLE,
	SIMPLE,
	NONE;
	
	public static DataType convert(int i){
		if(i==0){
			return SIMPLE;
		}
		if(i==1){
			return CIRCLE;
		}
		else if(i==2){
			return TWOPOINTS;
		}
		else{
			return NONE;
		}
	}
}