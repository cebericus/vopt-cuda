import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Set of basic file utilities for visual profiler tool.
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-14 
 */
public class FileHandler {

	private Shell shell;


	/**
	 * constructor
	 */
	public FileHandler( Shell shell) {

		this.shell = shell;

	}

	/**
	 * 
	 */
	public void onLoad( Text text ) {

		FileDialog fileChooser = new FileDialog( this.shell, SWT.OPEN );

		fileChooser.setText("Load source code files");

		fileChooser.setFilterPath( System.getProperty("user.dir" + "code") );

		fileChooser.setFilterExtensions( new String[] { "*.cu", "*.c", "*.ptx" } );
		
		/** newlines */
		char eol = System.getProperty("line.separator").charAt(0); 

		try {
			String filename = fileChooser.open();

			FileReader in_file = new FileReader( filename );

			/** open file with buffered io */
			BufferedReader in_f_read = new BufferedReader( in_file );	

			/** give file handle to text widget */
			/** TODO This will break !!!!!! when the Text box is overrun */
			StringBuffer str_buf = new StringBuffer();
			
			String str = new String();
			str = null;
			
			do{
				
				if( str != null )
				{
					str_buf.append( str + eol  );
				}
				
			} while( (str = in_f_read.readLine()) != null ) ;
			
			
			text.setText( str_buf.toString() );
		

		} catch (Exception e) {
			System.out.println( "onLoad exception: " + e.getMessage() );
			e.printStackTrace();
		}
	
		
	}
	
	/**
	 * loads, parses a cuda_profile.log file into HashMap of HashMaps where
	 * each sub-Map contains all data points for a given profiler option
	 * HashMap<option, HashMap> --> HashMap<
	 * @param hashMap
	 * @return
	 */
	public String onLoadMap( ProfileMap profileMap ) {

		FileDialog fileChooser = new FileDialog( this.shell, SWT.OPEN );

		fileChooser.setText("Load source code files");

		fileChooser.setFilterPath( System.getProperty("user.dir" + "code") );

		fileChooser.setFilterExtensions( new String[] { "*.log" } );
		
		/** newlines */
		char eol = System.getProperty("line.separator").charAt(0); 

		String filename = null;
		
		try {
			filename = fileChooser.open();

			FileReader in_file = new FileReader( filename );

			/** open file with buffered io */
			BufferedReader in_f_read = new BufferedReader( in_file );	
			
			String str = new String();
			str = null;
			
			/** search for line containing "method," */
			String sch = new String( "method," );
			
			while( (str = in_f_read.readLine()) != null ){
				
				if( str.startsWith( sch ) ){

					profileMap.parse( in_f_read );
					
					break;
				}
			} 
			

		} catch (Exception e) {
			System.out.println( "onLoad exception: " + e.getMessage() );
			e.printStackTrace();
		}
	
		return filename;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
