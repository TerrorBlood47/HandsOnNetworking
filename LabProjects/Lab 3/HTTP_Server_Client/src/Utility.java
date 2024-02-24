public class Utility {
	
	public static String getStringPortion(String mainStr, String search, String endStr){
		String stringPortion = null;
		int start = mainStr.indexOf(search);
		if (start != -1) {
			start += search.length();
			int end = mainStr.indexOf(endStr, start);
			if (end != -1) {
				stringPortion = mainStr.substring(start, end);
			}
		}
		
		System.out.println("in method : " + stringPortion);
		return  stringPortion;
	}
}
