import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * Creates a Hashmap< String option, HashMap<Integer line_number, String data> > 
 * for each profiler option keyword as the primary key and a sub-hashmap as the value.
 * 
 * Each of the sub- HashMap<Integer, String>'s is keyed with the line number and 
 * holds values of the profile data.
 * 
 * Profiler options currently tested (supported, others could fail):
 * 
 * gridsize3d, threadblocksize, stasmemperblock, dynmemperblock, regperthread
 * memtransferdir, memtransfersize, memtransferhostmemtype, cacheconfigrequested
 * cacheconfigexecuted, countermodeaggregate, warp_serialize, gld_incoherent,
 * gst_incoherent, method.
 * 
 * Note that "method" is treated as just another profiler option.
 * 
 * Pretty much any class function that is called before a profiler log 
 * file is loaded will throw an exception, likely NaN or NullPointer.
 * 
 * The exception is ProfileMap.parse(), which should be called before all other
 * functions. 
 * 
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-20. 
 * 
 * TODO does not yet account for a badly formed profile.log file.
 * TODO 1. it probably was a mistake to not use the csv format log file. 2. if 
 * nvidia changes the profile options this will probably blow up and have to be 
 * rewritten.
 * TODO integrate (something like) org.apache.commons.math.stat.*, and deprecate 
 * current stat functions.
 */
public class ProfileMap {
	
	/**
	 * Structure used to store profile data.  
	 * HashMap< key:option_name, value:HashMap< key:line_number, value:string_value> >
	 * 
	 * Where option_name is one of: method, cputime, gputime, etc.
	 *              line_number is: one-based count of profile data lines, 
	 *                              not file lines
	 *             string_value is: profile data.
	 */
	protected HashMap<String, HashMap<Integer, String> > profileMap;
	
	
	/**
	 * constructor
	 * TODO this probably should be a singleton
	 */
	public ProfileMap() {
		
		profileMap = new HashMap<String, HashMap<Integer, String> >();
	}
	
	
	/**
	 * Locates first line of profile data and reads file into data structure, 
	 * line-by-line.  This function needs to be called before using any of the 
	 * other functions in this class.
	 * 
	 * @param in_f_read
	 */
	public void parse(BufferedReader in_f_read ){
		
		String str = new String();
		str = null;

		/** search for line containing "method," */
		String sch = new String( "method," );

		try {
			while( (str = in_f_read.readLine()) != null ){

				//System.out.println( "str: " + str );

				if( str.startsWith( sch ) ){

					this.parseOptions( in_f_read, str );

					break;
				}
			} 

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	/** foreach options found on the line containing "method," create a HashMap
	 *  keyed for the given option
	 *  
	 * @param in_f_read
	 * @param str_options the 5th or 6th line comprising comma separated profiler
	 * options, ie. method,cputime,tec.
	 */
	private void parseOptions( BufferedReader in_f_read, String str_options ){

		/** make a series HashMap s for each option */
		while( str_options.length() > 0){
			
			//System.out.println( "parseOptions str: " + str_options );
			
			/** length of option word, delimited by comma */
			int option_len = str_options.indexOf( ',', 0 );
			
			/** check for no comma's left, have not tested whether zero ever occurs */
			if( option_len == 0 || option_len == -1 ){

				/** check for end of string */
				if( str_options.length() > 0 )
					/** put last option */
					this.profileMap.put( str_options, ( new HashMap<Integer, String>() ) );
			
					/** set string to null */
					str_options = "";
			}
			else{
				
				/** make a new sub-HashMap for each option */
				this.profileMap.put( str_options.substring( 0, option_len ), 
														( new HashMap<Integer, String>() ) );

				/** truncate string, "+ 1" to get rid of comma */
				str_options = str_options.substring( option_len + 1  );
			}
		}
		
		/** now read the rest of file and submit data by line number */
		try {
			
			int line_num = 0;

			String str_data = new String( "" );
			
			/** matches option ie. method= [, cputime= [ */
			Pattern p_opt = Pattern.compile( "([a-z]|[_])+=\\[\\s" );
			
			/** matches data  "=[ numbers, etc. without spaces ]" OR "[ number, number, number ]" */
			Pattern p_dat = 
					Pattern.compile( "(=\\[\\s([a-z]|[A-Z]|[0-9]|[,._])+\\s\\])|(=\\[\\s[0-9]+\\,\\s[0-9]+\\,\\s[0-9]+\\s\\])" );
			
			Pattern p_xyz = Pattern.compile( "[0-9]+" );

			/** read file one line at time into buffer */
			while( (str_data = in_f_read.readLine()) != null ){

				Matcher m_opt = p_opt.matcher( str_data );
				
				Matcher m_dat = p_dat.matcher( str_data );

				/** match tokens thoughout current line and put cognate data into map */
				while( m_opt.find() && m_dat.find() ){

					/** current option String */
					//System.out.println( str_data.substring( m_fore.start(), m_fore.end() - 3 ) );
					String option = str_data.substring( m_opt.start(), m_opt.end() - 3 );

					/** data tokens matching current option string */
					//System.out.println( str_data.substring( m_aft.start() + 3, m_aft.end() - 2 ) );
					String data = str_data.substring( m_dat.start() + 3, m_dat.end() - 2 );

					/** check for xyz style option strings */
					if( option.matches( "gridsize" ) ){
						
						Matcher m_xyz = p_xyz.matcher( data );
						
						/** crude but it works */
						m_xyz.find();
						this.profileMap.get( "gridsizeX" ).put( line_num, m_xyz.group() );
						
						m_xyz.find();
						this.profileMap.get( "gridsizeY" ).put( line_num, m_xyz.group() );
						
						m_xyz.find();
						this.profileMap.get( "gridsizeZ" ).put( line_num, m_xyz.group() );
					}
					else
						if( option.matches( "threadblocksize" ) ){
							
							Matcher m_xyz = p_xyz.matcher( data );
							
							/** crude but it works */
							m_xyz.find();
							this.profileMap.get( "threadblocksizeX" ).put( line_num, m_xyz.group() );
							
							m_xyz.find();
							this.profileMap.get( "threadblocksizeY" ).put( line_num, m_xyz.group() );
							
							m_xyz.find();
							this.profileMap.get( "threadblocksizeZ" ).put( line_num, m_xyz.group() );
						}
						else 
							/** load line number and data into sub-element */
							this.profileMap.get( option ).put( line_num, data );

				}

				//System.out.println( "line_num " + line_num + " " + tmp.get(line_num) );
				++line_num;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * queries all elements of profile map and assembles a list of method names
	 * 
	 * @return array of Strings, each element is name of CUDA method
	 */
	public List<String> methods(){
		
		List<String> methods = new ArrayList<String>();
		
		String tmp_str;
		
		try {
			/** compose a list of all method records */
			for (Map.Entry<Integer, String> entry : 
									this.profileMap.get("method").entrySet()) {

				tmp_str = entry.getValue();

				if (methods.contains(tmp_str) == false) {

					methods.add(tmp_str);

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return methods;
	}
	
	
	/**
	 * queries all elements of profile map and assembles a list of option names
	 * 
	 * @return ArrayList<String>, each element is a profiler option from the 
	 * current log file
	 */
	public List<String> options(){
		
		List<String> options = new ArrayList<String>();

		options.addAll( this.profileMap.keySet() );
		
		return options;
	}
	
	
	/**
	 * takes in a (valid) list of profiler option names and filters them by the 
	 * requested method. so, for instance, any method starting with "memcpy"
	 * only needs the options of cputime, gputime, and memory related options.
	 * 
	 * 
	 * @param method
	 * @param options
	 * @return filtered list of options
	 */
	public List<String> filterByMethod( String method, List<String> options ){
		
		List<String> l = new ArrayList<String>();
		
		String tmp_str;
		
		try {
			/** filter for memcpy methods */
			if (method.matches("memcpy.+")) {

				/** remove all but *times and mem* */
				for (Iterator<String> it = options.iterator(); it.hasNext();) {

					tmp_str = it.next();
					
					if ( tmp_str.matches( "([cg]putime)|(mem.+)" ) == true ) {

						l.add( tmp_str );
					}
				}
			}
			//else ?? - need to examine usage before using else
			// TODO remove else if can test for (individual) mangled kernel names
			else
			{
				/** remove all  mem*, grid*, thread* and method */
				for (Iterator<String> it = options.iterator(); it.hasNext();) {

					tmp_str = it.next();

					if ( tmp_str.matches( "(mem.+)|(grid.+)|(thread.+)|(method)" ) == false ) {

						l.add( tmp_str );
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}
	
	
	/**
	 * count occurrences of the requested option 
	 * 
	 * @param option
	 * @return
	 */
	public double n_option( String option ){
		
		/** stat group size */
		int n = 0;
		
		try {
			
			/** count occurrences of the requested option */
			for (Map.Entry<Integer, String> entry : 
										this.profileMap.get(option).entrySet()) {
				++n;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (double) n;
	}
	
	
	/**
	 * count occurrences of the requested method record
	 * 
	 * @param method
	 * @return
	 */
	public double n_method( String method ){
		
		double n = 0;
		
		String tmp_str;
		
		try {
			/** count occurrences of the requested method record */
			for (Map.Entry<Integer, String> entry : 
									this.profileMap.get("method").entrySet()) {

				tmp_str = entry.getValue();

				if ( method.contentEquals( tmp_str) ) {

					++n;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return n;
	}
	
	
	/**
	 * count all observations (number of profile lines)
	 * 
	 * @return
	 */
	public double n_overall(){

		return (double) this.profileMap.get("method").size();

	}
	
	
	/**
	 * queries all elements of profile map, across all methods and calculates
	 * an average value (mean over all conditions)
	 * TODO should be deprecated, replaced by separate statistics class(es)
	 * 
	 * @param option
	 * @return mean over all conditions
	 */
	public double average( String option ){
		
		double sum = 0;
		
		/** stat group size */
		int n = 0;
		
		try {
			
			/** compose a list of all values for the requested option */
			for (Map.Entry<Integer, String> entry : 
										this.profileMap.get(option).entrySet()) {

				sum = sum + Double.valueOf( entry.getValue() );

				++n;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ( sum / (double) n );
	}
	
	/**
	 * queries all elements of profile map, across all methods and calculates
	 * a sum (over all conditions)
	 * TODO should be deprecated, replaced by separate statistics class(es)
	 * 
	 * @param option
	 * @return sum over all conditions
	 */
	public double sum( String option ){
		
		double sum = 0;
		
		try {
			
			/** compose a list of all values for the requested option */
			for (Map.Entry<Integer, String> entry : 
										this.profileMap.get(option).entrySet()) {

				sum = sum + Double.valueOf( entry.getValue() );
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return sum;
	}
	
	/**
	 * queries all elements of profile map for method and calculates an average
	 * value for the option associated with that method
	 * TODO should be deprecated, replaced by separate statistics class(es)
	 * 
	 * Throws a NaN exception if called with a Kernel function as the 
	 * method argument and an unavailable option is requested, ie the option 
	 * memtransfer....
	 * Alternatively, same exception if method is a memcpy function and a
	 * kernel specific option ie. gridsizeX is asked for.
	 * 
	 * @param method
	 * @param option
	 * @return mean over single condition
	 */
	public double average( String method, String option ){

		double sum = 0;

		/** stat group size */
		int n = 0;

		try {

			/** compose a list of values for the requested option by method */
			for( Map.Entry<Integer, String> entry : 
									this.profileMap.get( "method" ).entrySet() ) {
				
				if( entry.getValue().matches( method ) ){

					sum = sum + Double.valueOf( this.profileMap.get( option ).get( entry.getKey() ) );

					++n;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ( sum / (double) n );
	}

	/**
	 * queries all elements of profile map for method and calculates a sum
	 * for the option associated with that method
	 * TODO should be deprecated, replaced by separate statistics class(es)
	 * 
	 * Throws a NaN exception if called with a Kernel function as the 
	 * method argument and an unavailable option is requested, ie the option 
	 * memtransfer....
	 * Alternatively, same exception if method is a memcpy function and a
	 * kernel specific option ie. gridsizeX is asked for.
	 * 
	 * @param method
	 * @param option
	 * @return sum over single condition
	 */
	public double sum( String method, String option ){

		double sum = 0;

		try {

			/** compose a list of values for the requested option by method */
			for( Map.Entry<Integer, String> entry : 
									this.profileMap.get( "method" ).entrySet() ) {
				
				if( entry.getValue().matches( method ) ){

					sum = sum + Double.valueOf( this.profileMap.get( option ).get( entry.getKey() ) );
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sum;
	}
	
	/**
	 * Takes the name of a valid profiling option as an argument and gives back
	 * a sorted list of paired line numbers and values
	 * 
	 * @param option
	 * @return pairs of data points, rejects "method" and returns empty data
	 * structure, invalid option string will give a null pointer exception.
	 */
	public LinkedHashMap<Integer, Double> getLinkedMap( String option ){
		
		LinkedHashMap<Integer, Double> lhm = new LinkedHashMap<Integer, Double>();
		
		if( option.matches("method") == false ){
			
			try {

				/** compose a list of all values for the requested option */
				for (Map.Entry<Integer, String> entry : 
					this.profileMap.get(option).entrySet()) {

					lhm.put(entry.getKey(), Double.valueOf( entry.getValue() ) );
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lhm;
	}
	
	
	/**
	 * Takes the name of a valid method (function) and a valid profiling option 
	 * as an argument and gives back a sorted list of paired line numbers and values
	 * 
	 * @param method
	 * @param option
	 * @return pairs of data points, see warnings for sibling function.
	 */
	public LinkedHashMap<Integer, Double> getLinkedMap( String method, String option ){
		
		LinkedHashMap<Integer, Double> lhm = new LinkedHashMap<Integer, Double>();
		
		/* line count */
		Integer n = 0;

		if( option.matches("method") == false ){

			try {

				/** compose a list of all values for the requested option */
				for (Map.Entry<Integer, String> entry : 
					this.profileMap.get(option).entrySet()) {

					if( this.profileMap.get("method").get( entry.getKey() ).matches(method) ){

						++n;

						/** to validate, comment out"lhm.put( n, ..." and uncomment 
						 * line with "put.(entry.getkey()...."
						 */
						//lhm.put(entry.getKey(), Double.valueOf( entry.getValue() ) );
						lhm.put( n, Double.valueOf( entry.getValue() ) );
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return lhm;
	}

	
	/**
	 * TODO Note: this may be removed or made protected/private. Alternatively,
	 * it may be altered to return a deep copy of the map to preserve data
	 * integrity.
	 * 
	 * @return the profileMap
	 */
	public HashMap<String, HashMap<Integer, String>> get() {
		return profileMap;
	}


	/**
	 * test: note is is best to use a fairly short log file or the console
	 * output buffer is overrun.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		final Shell sh = new Shell();
		Display display = Display.getDefault();
		sh.open();

		
		final ProfileMap p = new ProfileMap();

		
		FileDialog fileChooser = new FileDialog( sh, SWT.OPEN );

		fileChooser.setText("Load profile log");

		fileChooser.setFilterPath( System.getProperty("user.dir" + "code") );

		fileChooser.setFilterExtensions( new String[] { "*.log" } );		

		String filename = null;
		
		
		try {
			filename = fileChooser.open();

			FileReader in_file = new FileReader( filename );

			/** open file with buffered io */
			BufferedReader in_f_read = new BufferedReader( in_file );	

			p.parse( in_f_read );

		} catch (Exception e) {
			System.out.println( "file exception: " + e.getMessage() );
			e.printStackTrace();
		}
		
		/** avoid overwriting exception print out */
		System.out.println(  "  " );
		
		/** test hash map structures, a long log fiel will overrun the consoel buffer */
		for( Map.Entry<String, HashMap<Integer, String> > entry : p.get().entrySet() ){
			
			System.out.println( "map: " + entry.getKey() );
			
			for( Map.Entry<Integer, String> data : entry.getValue().entrySet() ){
				
				//System.out.println( "data: " + data.getKey() + " " + data.getValue() );
				
			}

		}
		
		/** test composition of available method names */
		List<String> l = p.methods();
		
		for( Iterator<String> it = l.iterator(); it.hasNext(); ){
			System.out.println( it.next() );
		}
		
		/** test n_method */
		System.out.println( "memcpyHtoD - n = " + p.n_method( "memcpyHtoD" ) );
		System.out.println( "memcpyDtoH - n = " + p.n_method( "memcpyDtoH" ) );
		System.out.println( "memcpyDtoD - n = " + p.n_method( "memcpyDtoD" ) );
		
		/** test n_option */
		System.out.println( "cputime - n = " + p.n_option( "cputime" ) );
		System.out.println( "gputime - n = " + p.n_option( "gputime" ) );
		System.out.println( "memtransferdir - n = " + p.n_option( "memtransferdir" ) );
		//System.out.println( "memcpyDtoD - n = " + p.n_option( "trash2384Y_" ) );
		
		/** test n_overall */
		System.out.println( "n = " + p.n_overall() );
		
		/** test average
		 * TODO 1. not an exhaustive test of all options, 
		 * TODO 2. not actually validated by hand calculation
		 */
		System.out.println( "Average cputime over all observations: " + p.average("cputime") );
		System.out.println( "Average occupancy over all observations: " + p.average("occupancy") );
		
		/** List of options */
		System.out.println( p.options() );
		
		/** test average for specific method/option combination */
		/** agreed with a spot check on short example file, n = 3 */
		System.out.println( "Average CPUtime for memcpyDtoH: " + p.average("memcpyDtoH", "cputime") );
		
		/** TODO gives exception NaN because the option is not available for the given method */
		//System.out.println( "Average threadblocksizeX for memcpyDtoH: " + p.average("memcpyDtoH", "threadblocksizeX") );
		
		/** test filtering */
		System.out.println( "unfiltered: " + p.options() );
		System.out.println( "filtered: " + p.filterByMethod( "memcpyDtoH", p.options() ) );
		
		System.out.println( "unfiltered: " + p.options() );
		System.out.println( "filtered: " + p.filterByMethod( "_ZtrashP_", p.options() ) );
		
		/** test extraction of paired data points by option, all methods */
		LinkedHashMap<Integer, Double> lhm = new LinkedHashMap<Integer, Double>();
		lhm = p.getLinkedMap("gputime");
		System.out.println( "lhm : " + lhm );
		lhm = p.getLinkedMap("method");
		System.out.println( "lhm : " + lhm );
		lhm = p.getLinkedMap("trash");
		System.out.println( "lhm : " + lhm );
		
		/** test extraction of paired data points by option, specific methods */
		lhm = p.getLinkedMap( "memcpyDtoD",  "gputime");
		System.out.println( "lhm : " + lhm );
		
		/** partial test of sum functions - note still a problem with identfying
		 * the kernel name
		 */
		System.out.println( "Sum cputime over all observations: " + p.sum("cputime") );
		System.out.println( "Sum occupancy over all observations: " + p.sum("occupancy") );
		System.out.println( "Sum gputime over memcpyDtoH observations: " + p.sum( "memcpyDtoH", "gputime") );
		
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

}
