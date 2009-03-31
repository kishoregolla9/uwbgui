package uwb;

enum DataType{
	TWOPOINTS,
	CIRCLE,
	SIMPLE,
	TWORADIOS,
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
		else if(i==3){
			return TWORADIOS;
		}
		else{
			return NONE;
		}
	}
}