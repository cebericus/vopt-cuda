import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * Creates a Hashmap< String, HashMap<Integer, String> > for each profiler 
 * option keyword as the key and a subhashmap as the value.
 * Each of the sub- HashMap<Integer, String>'s is keyed with the line number and 
 * holds values of the profile data.
 * 
 * Profiler options currently tested (supported, others probably will fail):
 * 
 * gridsize3d, threadblocksize, stasmemperblock, dynmemperblock, regperthread
 * memtransferdir, memtransfersize, memtransferhostmemtype, cacheconfigrequested
 * cacheconfigexecuted, countermodeaggregate, warp_serialize, gld_incoherent,
 * gst_incoherent
 * 
 * 
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-20. 
 * 
 * TODO does not yet account for a badly formed profile.log file.
 * TODO 1. it was a mistake to not use the csv format log file. 2. if nvidia
 * changes the profile options this will probably blow up and have to be 
 * rewritten
 * 
 */
public class ProfileMap {
	
	protected HashMap<String, HashMap<Integer, String> > profileMap;
	
	/**
	 * constructor
	 * TODO this probably should be a singleton
	 */
	public ProfileMap() {
		
		profileMap = new HashMap<String, HashMap<Integer, String> >();
	}
	
	
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
		
		List<String> methods;
		
		
		
		
		return methods;
	}
	
	/**
	 * queries all elements of profile map, across all methods and calculates
	 * an average value
	 * 
	 * @param option
	 * @return
	 */
	public double average( String option ){
		
		double avg = 0;
		
		
		return avg;
	}
	
	/**
	 * queries all elements of profile map for method and calculates an average
	 * value for the option associated with that method
	 * 
	 * @param method
	 * @param option
	 * @return
	 */
	public double average( String method, String option ){
		
		double avg = 0;
		
		
		return avg;
	}

	/**
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
		
		for( Map.Entry<String, HashMap<Integer, String> > entry : p.get().entrySet() ){
			
			System.out.println( "map: " + entry.getKey() );
			
			for( Map.Entry<Integer, String> data : entry.getValue().entrySet() ){
				
				System.out.println( "data: " + data.getKey() + " " + data.getValue() );
				
			}

		}
		
		
		
		
		while (!sh.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

}
