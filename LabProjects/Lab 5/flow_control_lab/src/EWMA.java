public class EWMA {
	
	private static  float EstimatedRTT = 0;
	private static float DevRTT = 0;
	
	
	
	private static final float  Alpha = 0.125F;
	private static final float Beta = 0.25F;
	
	
	public static int getTimeOutInterval(float SampleRTT){
		EstimatedRTT = (1-Alpha)*EstimatedRTT + Alpha*SampleRTT;
		
		DevRTT = (1 - Beta)*DevRTT + Beta*Math.abs(EstimatedRTT - SampleRTT);
		
		Integer timeOutInterval = (int) (EstimatedRTT + 4*DevRTT);
		
		return timeOutInterval;
	}
	
	
	
}
