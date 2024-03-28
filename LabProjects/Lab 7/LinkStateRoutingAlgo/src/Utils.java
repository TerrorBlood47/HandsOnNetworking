public class Utils {
	
	
	public static int getPort(String ROUTER_NAME){
		System.out.println("router name " + ROUTER_NAME);
		
		if (ROUTER_NAME.equals("A")){
			return NodeRouter_A.PORT;
		}
		else if(ROUTER_NAME.equals("B")){
			
			return NodeRouter_B.PORT;
		}
		else if(ROUTER_NAME.equals("C")){
			
			return NodeRouter_C.PORT;
		}
		else if(ROUTER_NAME.equals("D")){
			
			return NodeRouter_D.PORT;
		}
		else if(ROUTER_NAME.equals("E")){

			return NodeRouter_E.PORT;
		}else if(ROUTER_NAME.equals("F")){

			return NodeRouter_F.PORT;
		}
		else {
			System.out.println("No Router for this PORT");
			return Integer.MAX_VALUE;
		}
	}
}
