import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 */

/**
 * @author nelsoncs
 *
 */
public class ProfileTable {
	
	private Table table;


	public ProfileTable(Composite parent, int style) {
		
		//super(parent, style);
		
		this.table = new Table( parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		data.heightHint = 200;
		this.table.setLayoutData(data);

	}
	
	public void set( ProfileMap profileMap ){
		
		/** first column is line number */
		TableColumn first_column = new TableColumn(table, SWT.NONE);
		first_column.setText ( "Line No." );

		for( Iterator<String> options = profileMap.options().iterator() ; options.hasNext(); ){
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText ( options.next() );
			
		}	
		
		/** make rows */
		for( int i = 0; i < profileMap.n_overall(); ++i ){
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText( 0, Integer.toString(i) );
		}
		

		for( Map.Entry<String, HashMap<Integer, String> > entry : profileMap.get().entrySet() ){
			
			System.out.println( "map: " + entry.getKey() );
			
			for( Map.Entry<Integer, String> data : entry.getValue().entrySet() ){
				
				//System.out.println( "data: " + data.getKey() + " " + data.getValue() );

				//item.setText( data.getKey(), data.getValue() );
				
			}

		}
			


		for (int i=0; i<table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}	
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
