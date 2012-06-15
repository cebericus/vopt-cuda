/** Copyright 2012 Cole Nelson  */

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
 * @author nelsoncs
 *
 */
public class ProfileTable {
	
	private Table table;


	public ProfileTable(Composite parent, int style) {
		
		//super(parent, style);
		
		this.table = new Table( parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION  );
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

		/** make rows */
		for( int i = 0; i < profileMap.n_overall(); ++i ){
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText( 0, Integer.toString(i) );
		}
		
		/** column index for option keywords */
		int c = 1;
		
		/** get options and use as columns in table */
		for( Iterator<String> options = profileMap.options().iterator() ; options.hasNext(); ){
			
			String next = options.next();
			
			/** column heading */
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText ( next );
	
			/** access data for the current option keyword and populate column */
			for( Map.Entry<Integer, String> col : profileMap.get().get( next ).entrySet() ){
				
				//System.out.println( c + " " + next + " " + col + " " + col.getKey() + " " + col.getValue() );
				
				/** the line matching the key for the current data point */
				TableItem line = table.getItem( col.getKey() );
				
				/** TODO see if linked HashMap or other structure would be faster/easier */
				/** set the cell */
				line.setText( c, col.getValue() );
					
			}
			
			/** move over one column */
			++c;
		}	


		for (int i=0; i<table.getColumnCount(); i++) {
			table.getColumn(i).pack();
		}	
		
		
	}
	
	public void dispose(){
		this.table.dispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
