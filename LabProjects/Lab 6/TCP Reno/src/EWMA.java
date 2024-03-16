public class EWMA {
	
	private static  long EstimatedRTT = 0;
	private static long DevRTT = 0;
	
	
	
	private static final double  Alpha = 0.125F;
	private static final double Beta = 0.25F;
	
	
	public static long getTimeOutInterval(long SampleRTT){
		EstimatedRTT = (long) ((1-Alpha)*EstimatedRTT + Alpha*SampleRTT);
		
		DevRTT = (long) ((1 - Beta)*DevRTT + Beta*Math.abs(EstimatedRTT - SampleRTT));
		
		long timeOutInterval = (long) (EstimatedRTT + 4*DevRTT);
		
		return timeOutInterval;
	}
	
	
	
}
