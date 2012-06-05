import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.forms.widgets.FormToolkit;


/**
 * Main() and window shell written in SWT for visual profiler tool
 * 
 * Abstract:
 * 
 * 
 * 
 * @author nelsoncs 2012-May-14.   
 */
public class BaseWindow {

	protected Shell shlVisualProfiler;
	
	protected ProfileMap profile_map;
	
	private Combo comboMethods;
	private Combo comboOptions;
	private MetricShapeBase m;
	private Canvas canvasOccupancy;
	
	private ProfileTable profileTable;
	private Text ptxCode;
	private Text textSearchCode;
	private Text textSearchPtx;
	private Text txtcu;
	private Text txtptx;
	private Text txtProfileData;
	private Text text;


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			BaseWindow window = new BaseWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** constructor
	 * 
	 */
	public BaseWindow(){
		
		this.profile_map = new ProfileMap();
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlVisualProfiler.open();
		shlVisualProfiler.layout();
		while (!shlVisualProfiler.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * destructor for SWT must dispose of all resources created with new.
	 */
	private void exit() {
		//this.profileTable.dispose();
		this.ptxCode.dispose();
	    this.shlVisualProfiler.dispose();
	    System.exit(0);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		/** BEGIN Main Shell */
		
		shlVisualProfiler = new Shell();
		shlVisualProfiler.setSize(1387, 720);
		shlVisualProfiler.setForeground(SWTResourceManager.getColor(76, 76, 76));
		
		String devs_title = new String();
		
		try {
			/** query for GPU(s) and put info in title bar */
			DeviceQuery devices = new DeviceQuery();
			
			for( int i = 0; i < devices.getDeviceCount(); ++i ){
				
				devs_title = devs_title + " - Devices found: " + "(" + i + ") " + devices.get()[i].getName();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			devs_title = " - DeviceQuery Exception - CUDA Devices not found.";
		}
		
		
		shlVisualProfiler.setText("VOT - Visual Occupancy Tool" + devs_title );
		GridLayout gl_shlVisualProfiler = new GridLayout(2, false);
		shlVisualProfiler.setLayout(gl_shlVisualProfiler);
		
		/** END Main Shell */
		
		
		
		/** BEGIN Menu */
		
		Menu menuMain = new Menu(shlVisualProfiler, SWT.BAR);
		shlVisualProfiler.setMenuBar(menuMain);

		MenuItem mntmExit = new MenuItem(menuMain, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				exit();
			}
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				exit();
			}
		});
		mntmExit.setText("Exit");

		MenuItem mntmHistory = new MenuItem(menuMain, SWT.CASCADE);
		mntmHistory.setText("History");
		
		Menu menuHistory = new Menu(mntmHistory);
		mntmHistory.setMenu(menuHistory);
		
		MenuItem mntmPrevious = new MenuItem(menuHistory, SWT.NONE);
		mntmPrevious.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		mntmPrevious.setText("Previous");
		
		MenuItem mntmNext = new MenuItem(menuHistory, SWT.NONE);
		mntmNext.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		mntmNext.setText("Next");
		
		/** END Menu */
		
		
		
		/** BEGIN Profile Data panel */
		
		/** Composite set-up for the panel */
		Composite compositeProgressBars = new Composite(shlVisualProfiler, SWT.V_SCROLL);
		compositeProgressBars.setLayout(new GridLayout(1, false));
		GridData gd_compositeProgressBars = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd_compositeProgressBars.heightHint = 356;
		gd_compositeProgressBars.widthHint =275;
		compositeProgressBars.setLayoutData(gd_compositeProgressBars);
		
		/** Title and profile log file dialog */
		Composite compositeProfileData = new Composite(compositeProgressBars, SWT.BORDER);
		compositeProfileData.setLocation(0, 0);
		GridLayout gl_compositeProfileData = new GridLayout(3, false);
		gl_compositeProfileData.horizontalSpacing = 1;
		compositeProfileData.setLayout(gl_compositeProfileData);
		Label lblProfileData = new Label(compositeProfileData, SWT.CENTER);
		GridData gd_lblProfileData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_lblProfileData.widthHint = 246;
		lblProfileData.setLayoutData(gd_lblProfileData);
		lblProfileData.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblProfileData.setAlignment(SWT.CENTER);
		lblProfileData.setText("Profile Data");
		txtProfileData = new Text(compositeProfileData, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.CANCEL);
		txtProfileData.setToolTipText("Command line profile data file.  Default name is cuda_profile_0.log.");
		txtProfileData.setText("*.log");
		GridData gd_txtProfileData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txtProfileData.widthHint = 186;
		txtProfileData.setLayoutData(gd_txtProfileData);
		Button buttonProfileLog = new Button(compositeProfileData, SWT.NONE);
		buttonProfileLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		buttonProfileLog.setText("Load");
		
		/** Methods combo selector box */
		/** placed BEFORE log file dialog so that file dialog can populate coms */
		Composite compositeMethodsCombo = new Composite(compositeProgressBars, SWT.BORDER);
		Label lblComboMethods = new Label(compositeMethodsCombo, SWT.CENTER);
		lblComboMethods.setLocation(0, 0);
		lblComboMethods.setAlignment(SWT.CENTER);
		lblComboMethods.setSize(260, 20);
		lblComboMethods.setText("Methods");
		
		this.comboMethods = new Combo(compositeMethodsCombo, SWT.NONE);
		comboMethods.setBounds(0, 20, 260, 27);
		//comboMethods.setItems(items)
		
		/** Options combo selector box */
		Composite compositeOptionsCombo = new Composite(compositeProgressBars, SWT.BORDER);
		GridData gd_compositeOptionsCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeOptionsCombo.heightHint = 58;
		compositeOptionsCombo.setLayoutData(gd_compositeOptionsCombo);
		//compositeOptionsCombo.setLayoutData(new RowData(260, 59));
		Label lblOptions = new Label(compositeOptionsCombo, SWT.CENTER);
		lblOptions.setLocation(0, 7);
		lblOptions.setAlignment(SWT.CENTER);
		lblOptions.setSize(260, 20);
		lblOptions.setText("Options");

		
		this.comboOptions = new Combo(compositeOptionsCombo, SWT.NONE);
		comboOptions.setLocation(0, 27);
		comboOptions.setSize(260, 27);
		
		Composite compositeOccupancy = new Composite(compositeProgressBars, SWT.NONE);
		
		canvasOccupancy = new Canvas(compositeOccupancy, SWT.NONE);
		canvasOccupancy.setBounds(0, 0, 260, 50);

		
		
		/** Title and profile log file dialog */
		buttonProfileLog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileHandler fh = new FileHandler( BaseWindow.this.shlVisualProfiler );
				
				/** Brings up file dialog */
				String tmp_str = fh.onLoadMap( profile_map );
				
				/** set UI file name displayed */
				txtProfileData.setText( tmp_str );
				
				/** Set the combo box controls */
				/** get available method names */
				List<String> l_meth = new ArrayList<String>();
				l_meth.add( "All" ); 
				l_meth.addAll( profile_map.methods() );
				
				/** check for a healthy result */
				if( l_meth.isEmpty() == false ){
				
					/** set combo items */
					comboMethods.setItems( l_meth.toArray( new String[0] ) );
					
					/** default to first item in list "All" */
					comboMethods.select(0);
				}
				
				/** get available option names */
				List<String> l_opt = new ArrayList<String>();
				l_opt.add( "All" ); 
				l_opt.addAll( profile_map.options() );
				
				/** check for a healthy result */
				if( l_opt.isEmpty() == false ){
				
					/** set combo items */
					comboOptions.setItems( l_opt.toArray( new String[0] ) );
					
					/** default to first item in list "All" */
					comboOptions.select(0);
				}
				
				/** populate the progress bars with "All" as default */
				/** create a polygon shape for indicator */
				MetricShapeDecorator i = new MetricShapeDecoratorBorder( 
							( new MetricShapeBase( BaseWindow.this.canvasOccupancy, 0, 0, SWT.NULL) )  
						);
				
				i.setColor(243, 213, 185);
				i.draw();
				
				BaseWindow.this.profileTable.set( profile_map );

			}
		});




		/** END Profile Data Panel */

		/** BEGIN Canvas Drawing Area */

		Canvas canvasGPU = new CanvasGPU(shlVisualProfiler, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_canvasGPU = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_canvasGPU.widthHint = 1087;
		gd_canvasGPU.heightHint = 600;
		//gd_canvasGPU.heightHint = 320;
		gd_canvasGPU.minimumWidth = 200;
		gd_canvasGPU.minimumHeight = 250;
		canvasGPU.setLayoutData(gd_canvasGPU);
		canvasGPU.setVisible(true);
		canvasGPU.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		FillLayout fl_canvasGPU = new FillLayout(SWT.HORIZONTAL);
		fl_canvasGPU.spacing = 1;
		canvasGPU.setLayout(fl_canvasGPU);

		profileTable = new ProfileTable(shlVisualProfiler, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		//gd_sourceCode.widthHint = 366;
		GridData gd_profileTable = new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1);
		gd_profileTable.heightHint = 50;
		gd_profileTable.widthHint = 1376;
		//profileTable.setLayoutData(gd_profileTable);
		new Label(shlVisualProfiler, SWT.NONE);





//		textSearchCode = new Text(shlVisualProfiler, SWT.BORDER | SWT.H_SCROLL | SWT.SEARCH | SWT.CANCEL);
//		GridData gd_textSearchCode = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//		gd_textSearchCode.widthHint = 332;
//		textSearchCode.setLayoutData(gd_textSearchCode);
//		textSearchCode.addListener(SWT.Activate, new Listener() {
//			public void handleEvent(Event e) {
//
//			}
//		});




	}
}
