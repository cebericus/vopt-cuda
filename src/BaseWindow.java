/** Copyright 2012 Cole Nelson  */

import java.util.ArrayList;
import java.util.List;

import javax.management.BadAttributeValueExpException;

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
import org.eclipse.swt.graphics.Color;
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



/**
 * Main() and window shell written in SWT for visual profiler tool
 * 
 * Abstract:
 * The Compute Unified Device Architecture (CUDA) is a combined hardware and software 
 * model for general-purpose graphics processing unit (GP-GPU) parallel computing.  
 * Successful utilization of GP-GPU computing requires an understanding of the 
 * interaction between the underlying platform-specific hardware and multiple levels 
 * of software functionality.  To aid the programmer, numerous detailed reference 
 * materials and tools are provided by the manufacturer.  However, integrating the 
 * multiplicity of resources can prove to be a daunting task for the CUDA programmer.
 *   
 * An important criterion for correct, efficient parallel programs in CUDA is occupancy.
 * The transfer of program instructions and data from the host device to the CUDA-enabled 
 * device is relatively slow and incurs a large overhead in time and physical resources. 
 * For this reason, it is desirable to utilize the CUDA-enabled hardware to a maximal 
 * extent and leverage the benefits of parallel execution.  The first step is to 
 * examine the “occupancy”, meaning compute-cycle and resource utilization.  To analyze 
 * device occupancy there are three basic, yet separate, tools that the CUDA programmer 
 * has available.  These are deviceQuery which enumerates the CUDA model for the 
 * underlying hardware platform, the Occupancy Calculator which is a spreadsheet 
 * based form that requires the output of deviceQuery and the command-line profiler 
 * log file which provides run-time information.  A programmer is faced with transitioning 
 * between these three separate resources and some of the 30 or so written reference 
 * materials in order to analyze a developing CUDA program.
 * 
 * Therefore, I have designed and implemented the Visual Occupancy and Performance 
 * Tool (VOPT) for CUDA.  VOPT combines 
 * the basic CUDA resources: deviceQuery, Occupancy Calculator and Command-Line Profiler 
 * log files into a unified, visual reference and analysis tool.  This removes several 
 * practical obstacles for the CUDA programmer and facilitates the analysis and
 * optimization of CUDA software.  
 * 
 * @author nelsoncs 2012-May-14.
 *  
 */
public class BaseWindow {
	
	/** main window shell */
	protected Shell shell;

	/** left hand panel and bottom panel first tab */
	private ProfileMap profile_map;
	private ProfileTable profileTable;
	private Text txtProfileData;
	
	/** widgets for left hand panel */
	private Combo comboMethods;
	private Combo comboOptions;

	/** device query for title bar and canvasGPU data */
	private DeviceQuery devices;
	
	/** center canvas for occupancy calculator integration */
	private Canvas canvasOccupancy;

	/** movable shape for center canvas */
	private MetricShapeBase m;
	
	/** color palette */
	public Palette palette;
	
	/** Canvas class for occupancy calculator */
	CanvasGPU canvasGPU;


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
		
		this.shell = new Shell();
		
		this.palette = new Palette( this.shell.getDisplay() );
		
		this.profile_map = new ProfileMap();
	}
	
	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	
	/**
	 * destructor for SWT must dispose of all resources created with new.
	 */
	private void exit() {
		
		this.profileTable.dispose();

	    this.shell.dispose();
	    System.exit(0);
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		
		/** BEGIN Main Shell */
		
		shell.setSize(1350, 900);
		shell.setForeground(SWTResourceManager.getColor(76, 76, 76));
		
		String devs_title = new String();
		
		try {
			/** query for GPU(s) and put info in title bar */
			this.devices = new DeviceQuery();
			
			for( int i = 0; i < devices.getDeviceCount(); ++i ){
				
				devs_title = devs_title + " -  Devices found: " + "(" + i + ") " + devices.get()[i].getName();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			devs_title = " - DeviceQuery Exception - CUDA Devices not found.";
		}
		
		
		shell.setText("VOPT " + devs_title );
		GridLayout gl_shlVisualProfiler = new GridLayout(2, false);
		shell.setLayout(gl_shlVisualProfiler);
		
		/** END Main Shell */
		
		
		
		/** BEGIN Menu */
		
		Menu menuMain = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menuMain);

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
		Composite compositeProgressBars = new Composite(shell, SWT.V_SCROLL);
		compositeProgressBars.setLayout(new GridLayout(1, false));
		GridData gd_compositeProgressBars = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gd_compositeProgressBars.heightHint = 600;
		gd_compositeProgressBars.widthHint =274;
		compositeProgressBars.setLayoutData(gd_compositeProgressBars);
		
		/** Title and profile log file dialog */
		Composite compositeProfileData = new Composite(compositeProgressBars, SWT.BORDER);
		
		GridData gd_compositeProfileData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeProfileData.widthHint = 265;
		compositeProfileData.setLayoutData(gd_compositeProfileData);
		compositeProfileData.setLocation(0, 0);
		GridLayout gl_compositeProfileData = new GridLayout(3, false);
		gl_compositeProfileData.horizontalSpacing = 1;
		compositeProfileData.setLayout(gl_compositeProfileData);
		
		Label lblProfileData = new Label(compositeProfileData, SWT.CENTER);
		GridData gd_lblProfileData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		gd_lblProfileData.widthHint = 250;
		
		lblProfileData.setLayoutData(gd_lblProfileData);
		lblProfileData.setFont(SWTResourceManager.getFont("Calibri", 11, SWT.BOLD));
		lblProfileData.setAlignment(SWT.CENTER);
		lblProfileData.setText("Profile Data");
		
		txtProfileData = new Text(compositeProfileData, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.CANCEL);
		txtProfileData.setToolTipText("Command line profile data file.  Default name is cuda_profile_0.log.");
		txtProfileData.setText("*.log");
		GridData gd_txtProfileData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gd_txtProfileData.widthHint = 186;
		txtProfileData.setLayoutData(gd_txtProfileData);
		
		Button buttonProfileLog = new Button(compositeProfileData, SWT.NONE);
		
		/** TODO need an application wide color palette with Color management */
		Color c = new Color( this.shell.getDisplay(), 212, 220, 186 );
		buttonProfileLog.setBackground(c);
		//buttonProfileLog.setForeground(c);
		c.dispose();
		
		buttonProfileLog.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		buttonProfileLog.setText("Load");
		
		/** Methods combo selector box */
		/** placed BEFORE log file dialog so that file dialog can populate coms */
		Composite compositeMethodsCombo = new Composite(compositeProgressBars, SWT.BORDER);
		GridData gd_compositeMethodsCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeMethodsCombo.widthHint = 261;
		compositeMethodsCombo.setLayoutData(gd_compositeMethodsCombo);
		Label lblComboMethods = new Label(compositeMethodsCombo, SWT.CENTER);
		lblComboMethods.setLocation(0, 0);
		lblComboMethods.setAlignment(SWT.CENTER);
		lblComboMethods.setSize(250, 20);
		lblComboMethods.setText("Methods");
		
		this.comboMethods = new Combo(compositeMethodsCombo, SWT.NONE);
		comboMethods.setBounds(0, 20, 261, 27);
		//comboMethods.setItems(items)
		
		/** Options combo selector box */
		Composite compositeOptionsCombo = new Composite(compositeProgressBars, SWT.BORDER);
		GridData gd_compositeOptionsCombo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_compositeOptionsCombo.heightHint = 58;
		compositeOptionsCombo.setLayoutData(gd_compositeOptionsCombo);
		
		Label lblOptions = new Label(compositeOptionsCombo, SWT.CENTER);
		lblOptions.setLocation(0, 7);
		lblOptions.setAlignment(SWT.CENTER);
		lblOptions.setSize(250, 20);
		lblOptions.setText("Options");
		
		this.comboOptions = new Combo(compositeOptionsCombo, SWT.NONE);
		comboOptions.setLocation(0, 27);
		comboOptions.setSize(260, 27);
		
		/** composite to hold label and bar graph for "occupancy" */
		/** TODO move this construct to a separate class in order to reuse for 
		 * each profile statistic
		 */
		Composite compositeOccupancy = new Composite(compositeProgressBars, SWT.NONE);
		
		/** make a small canvas to hold bar graph for "occupancy" */
		canvasOccupancy = new Canvas(compositeOccupancy, SWT.NONE);
		canvasOccupancy.setBounds(0, 10, 265, 50);
		
		
		
		/** END Profile Data Panel */
		

		/** BEGIN Canvas Drawing Area */
		this.canvasGPU = new CanvasGPU( shell, devices, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		canvasGPU.setLayout(null);
		GridData gd_canvasGPU = new GridData(SWT.LEFT, SWT.FILL, true, true, 1, 1);
		gd_canvasGPU.widthHint = 1010;
		gd_canvasGPU.heightHint = 720;

		gd_canvasGPU.minimumWidth = 200;
		gd_canvasGPU.minimumHeight = 250;
		canvasGPU.setLayoutData(gd_canvasGPU);
		canvasGPU.setVisible(true);
		canvasGPU.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		/** END Canvas Drawing Area */
		
		/** BEGIN lower panel */
		
		/** BEGIN first tab of lower panel - profile data table */
		profileTable = new ProfileTable(shell, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);

		GridData gd_profileTable = new GridData(SWT.LEFT, SWT.FILL, true, true, 2, 1);
		gd_profileTable.heightHint = 200;
		gd_profileTable.widthHint = 1250;
		//profileTable.setLayoutData(gd_profileTable);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		/** END first tab of lower panel - profile data table */

		/** END lower panel */


		/** BEGIN Listeners */
		
		/** BEGIN Profile log listener */
		/** Title and profile log file dialog */
		buttonProfileLog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileHandler fh = new FileHandler( BaseWindow.this.shell );

				/** file dialog */
				String tmp_str = fh.onLoadMap( profile_map );

				if( tmp_str != null ){

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
					/** TODO this section should be moved to a new class so that
					 * several things can be done: 1. show possible profile metrics as
					 * greyed out ( setAlpha( 63 ) ) bars 2. populate those that match
					 * profile log options when file is loaded  3. list should be 
					 * greyed out in response to combo box filtering
					 */

					if( BaseWindow.this.profile_map.contains( "occupancy" ) ){
						
						/** create a label */
						Label l = new Label( BaseWindow.this.canvasOccupancy, SWT.BOLD );
						l.setFont(SWTResourceManager.getFont("Calibri", 11, SWT.BOLD));
						l.setText("Occupancy (" 
								+ Double.toString( profile_map.average( "occupancy" ) ).subSequence(0, 5) 
								+ ") ");
						l.setLocation(0, 0);
						l.setAlignment(SWT.CENTER);
						l.setSize(260, 20);

						/** create a polygon shape for indicator */
						MetricShapeDecoratorBorder i = new MetricShapeDecoratorBorder( 

								/**  set length to 260 px */
								( new MetricShapeBase( BaseWindow.this.canvasOccupancy, 1, 0, 258, SWT.NULL) )

								/** border width 1 px */
								, 1 
								);

						/** set background for bar graph */
						i.base.setColor( palette.getOr_calm() );

						/** set border color */
						i.setColor( palette.getBlk() );

						i.draw();
						
						try {
							i.setBar( profile_map.average( "occupancy" ), palette.getRed_angry() );
						} catch (BadAttributeValueExpException e1) {
							e1.printStackTrace();
						}
					}

					
					/** TODO move this set to ControllerCanvasGPU */
					/** populate canvasGPU threads/block with data from profile log*/
					if( BaseWindow.this.profile_map.contains( "threadblocksizeX" ) )
						BaseWindow.this.canvasGPU.setThreadsPerBlockX( profile_map.average( "threadblocksizeX" ) );

					if( BaseWindow.this.profile_map.contains( "threadblocksizeY" ) )
						BaseWindow.this.canvasGPU.setThreadsPerBlockY( profile_map.average( "threadblocksizeY" ) );
					
					if( BaseWindow.this.profile_map.contains( "threadblocksizeZ" ) )
						BaseWindow.this.canvasGPU.setThreadsPerBlockZ( profile_map.average( "threadblocksizeZ" ) );
					
					/** populate canvasGPU registers/thread with data from profile log*/
					if( BaseWindow.this.profile_map.contains( "regperthread" ) )
						BaseWindow.this.canvasGPU.setRegsPerThread( profile_map.average( "regperthread" ) );
				
					/** populate canvasGPU shared mem/block with data from profile log*/
					if( BaseWindow.this.profile_map.contains( "stasmemperblock" ) )
						BaseWindow.this.canvasGPU.setSharedPerBlock( profile_map.average( "stasmemperblock" ) );

					/** populate table in bottom panel with data from profile log*/			
					BaseWindow.this.profileTable.set( profile_map );
				}
			}
		});
		/** END Profile log listener */
		
		
		/** END Listeners */

	}
}
