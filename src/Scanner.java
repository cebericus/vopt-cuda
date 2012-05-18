import java.util.HashMap;



/**
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-18 
 */
public class Scanner {

	private HashMap<String, Float> tokens;
	
	public Scanner( String str ) {
		
		/** do something to the string */
		
		/** put them in the tokens HashMap */
		this.tokens.put(key, value);
	}

	/**
	 * @return the tokens
	 */
	public Float getToken( String key ) {
		return tokens.get( key );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String str_to_parse = new String( "paste the profiler output, output of " +
				"deviceQuery or other file we will need to tokenize here" );
		
		
		
		Scanner a_given_scanner = new Scanner( str_to_parse );
		float  profile_num;

		profile_num = a_given_scanner.getToken( "gst_incoherent" );
		
		System.out.println( profile_num );

	}

}
