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
	
	private Text sourceCode;
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
		this.sourceCode.dispose();
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
		shlVisualProfiler.setForeground(SWTResourceManager.getColor(76, 76, 76));
		shlVisualProfiler.setSize(1280, 720);
		
		String devs_title = new String();
		
		try {
			/** query for GPU(s) and put info in title bar */
			DeviceQuery devices = new DeviceQuery();
			
			for( int i = 0; i < devices.getDeviceCount(); ++i ){
				
				devs_title = devs_title + " - Devices found: " + "(" + i + ") " + devices.get()[i].getName();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			devs_title = "DeviceQuery Exception - CUDA Devices not found or unknown.";
		}
		
		
		shlVisualProfiler.setText("VOT - Visual Occupancy Tool" + devs_title );
		GridLayout gl_shlVisualProfiler = new GridLayout(5, true);
		shlVisualProfiler.setLayout(gl_shlVisualProfiler);
		
		/** END Main Shell */
		
		/** BEGIN Menu */
		
		Menu menuMain = new Menu(shlVisualProfiler, SWT.BAR);
		shlVisualProfiler.setMenuBar(menuMain);
		
//		MenuItem mntmFile = new MenuItem(menuMain, SWT.CASCADE);
//		mntmFile.setText("File");
//		
//		Menu menuFile = new Menu(mntmFile);
//		mntmFile.setMenu(menuFile);
//		
//		MenuItem mntmNewItem = new MenuItem(menuFile, SWT.NONE);
//		mntmNewItem.addListener(SWT.Activate, new Listener() {
//			public void handleEvent(Event e) {
//				
//			}
//		});
//		mntmNewItem.setText("New");
//		
//		MenuItem mntmOpen = new MenuItem(menuFile, SWT.NONE);
//		mntmOpen.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				FileHandler fh = 
//						new FileHandler( BaseWindow.this.shlVisualProfiler);
//
//				fh.onLoad( sourceCode );
//				fh.onLoad( ptxCode );
//			}
//		});
//		mntmOpen.setText("Open");

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
		GridData gd_compositeProgressBars = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_compositeProgressBars.heightHint = 375;
		gd_compositeProgressBars.widthHint = 270;
		compositeProgressBars.setLayoutData(gd_compositeProgressBars);
		
		/** Title and profile log file dialog */
		Composite compositeProfileData = new Composite(compositeProgressBars, SWT.BORDER);
		compositeProfileData.setLocation(0, 0);
		compositeProfileData.setLayout(new GridLayout(3, false));
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
		lblComboMethods.setSize(256, 20);
		lblComboMethods.setText("Methods");
		
		this.comboMethods = new Combo(compositeMethodsCombo, SWT.NONE);
		comboMethods.setBounds(0, 20, 256, 27);
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
		lblOptions.setSize(256, 20);
		lblOptions.setText("Options");

		
		this.comboOptions = new Combo(compositeOptionsCombo, SWT.NONE);
		comboOptions.setLocation(0, 27);
		comboOptions.setSize(256, 27);
		
		/** Title and profile log file dialog */
		buttonProfileLog.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileHandler fh = new FileHandler( BaseWindow.this.shlVisualProfiler);
				
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
				
			}
		});
		
		

		
//		Label lblGlobalMemoryStores = new Label(compositeProgressBars, SWT.NONE);
//		lblGlobalMemoryStores.setAlignment(SWT.CENTER);
//		lblGlobalMemoryStores.setLayoutData(new RowData(188, SWT.DEFAULT));
//		lblGlobalMemoryStores.setText("--Store Instructions--");
//		
//		Label lblNewLabel = new Label(compositeProgressBars, SWT.CENTER);
//		lblNewLabel.setLayoutData(new RowData(183, SWT.DEFAULT));
//		lblNewLabel.setText("Coalesced");
//		
//		ProgressBar progressBar = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar.setSelection(64);
//		progressBar.setLayoutData(new RowData(185, SWT.DEFAULT));
//		
//		Label lblGstuncoalesced = new Label(compositeProgressBars, SWT.CENTER);
//		lblGstuncoalesced.setLayoutData(new RowData(185, SWT.DEFAULT));
//		lblGstuncoalesced.setText("Uncoalesced");
//		
//		ProgressBar progressBar_1 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_1.setSelection(34);
//		progressBar_1.setLayoutData(new RowData(184, SWT.DEFAULT));
//		
//		Label label = new Label(compositeProgressBars, SWT.SEPARATOR | SWT.HORIZONTAL);
//		label.setLayoutData(new RowData(187, 3));
//		
//		Label lblLoadInstructions = new Label(compositeProgressBars, SWT.NONE);
//		lblLoadInstructions.setLayoutData(new RowData(187, SWT.DEFAULT));
//		lblLoadInstructions.setText("--Load Instructions--");
//		lblLoadInstructions.setAlignment(SWT.CENTER);
//		
//		Label lblGldcoalesced = new Label(compositeProgressBars, SWT.CENTER);
//		lblGldcoalesced.setLayoutData(new RowData(186, SWT.DEFAULT));
//		lblGldcoalesced.setText("Coalesced");
//		
//		ProgressBar progressBar_2 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_2.setSelection(23);
//		progressBar_2.setLayoutData(new RowData(184, SWT.DEFAULT));
//		
//		Label lblGlduncoalesced = new Label(compositeProgressBars, SWT.CENTER);
//		lblGlduncoalesced.setLayoutData(new RowData(186, SWT.DEFAULT));
//		lblGlduncoalesced.setText("Uncoalesced");
//		
//		ProgressBar progressBar_3 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_3.setSelection(77);
//		progressBar_3.setLayoutData(new RowData(184, SWT.DEFAULT));
//		
//		Label lblSMemory = new Label(compositeProgressBars, SWT.BORDER | SWT.CENTER);
//		lblSMemory.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
//		lblSMemory.setLayoutData(new RowData(182, SWT.DEFAULT));
//		lblSMemory.setText("Shared Memory");
//		lblSMemory.setAlignment(SWT.CENTER);
//		
//		ProgressBar progressBar_4 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_4.setSelection(10);
//		progressBar_4.setLayoutData(new RowData(187, SWT.DEFAULT));
//		
//		Label lblRegisters = new Label(compositeProgressBars, SWT.BORDER | SWT.CENTER);
//		lblRegisters.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
//		lblRegisters.setLayoutData(new RowData(182, SWT.DEFAULT));
//		lblRegisters.setText("Registers");
//		lblRegisters.setAlignment(SWT.CENTER);
//		
//		ProgressBar progressBar_5 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_5.setSelection(100);
//		progressBar_5.setLayoutData(new RowData(186, SWT.DEFAULT));
//		
//		Label lblLocalMemoryslow = new Label(compositeProgressBars, SWT.BORDER | SWT.CENTER);
//		lblLocalMemoryslow.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
//		lblLocalMemoryslow.setLayoutData(new RowData(181, SWT.DEFAULT));
//		lblLocalMemoryslow.setText("Local Memory (slow!)");
//		lblLocalMemoryslow.setAlignment(SWT.CENTER);
//		
//		ProgressBar progressBar_6 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_6.setSelection(15);
//		progressBar_6.setLayoutData(new RowData(187, SWT.DEFAULT));
//		
//		Label lblConstantMemory = new Label(compositeProgressBars, SWT.BORDER | SWT.CENTER);
//		lblConstantMemory.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
//		lblConstantMemory.setLayoutData(new RowData(182, SWT.DEFAULT));
//		lblConstantMemory.setText("Constant Memory");
//		lblConstantMemory.setAlignment(SWT.CENTER);
//		
//		ProgressBar progressBar_7 = new ProgressBar(compositeProgressBars, SWT.NONE);
//		progressBar_7.setSelection(12);
//		progressBar_7.setLayoutData(new RowData(186, SWT.DEFAULT));
		
		/** END Profile Data Panel */
		
		/** BEGIN Canvas Drawing Area */
		
		Canvas canvasGPU = new CanvasGPU(shlVisualProfiler, SWT.BORDER);
		GridData gd_canvasGPU = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		gd_canvasGPU.heightHint = 400;
		//gd_canvasGPU.heightHint = 320;
		gd_canvasGPU.minimumWidth = 200;
		gd_canvasGPU.minimumHeight = 250;
		canvasGPU.setLayoutData(gd_canvasGPU);
		canvasGPU.setVisible(true);
		canvasGPU.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		canvasGPU.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		/** END Canvas Drawing Area */
		
//		Composite compositeHistory = new Composite(shlVisualProfiler, SWT.NONE);
//		compositeHistory.setLayout(new RowLayout(SWT.VERTICAL));
//		GridData gd_compositeHistory = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 3);
//		gd_compositeHistory.widthHint = 248;
//		compositeHistory.setLayoutData(gd_compositeHistory);
//		
//		Canvas canvasPrevious = new CanvasGPU(compositeHistory, SWT.NONE);
//		canvasPrevious.setLayoutData(new RowData(243, 166));
//		
//		Button btnPrevious = new Button(canvasPrevious, SWT.NONE);
//		btnPrevious.setBounds(10, 10, 81, 26);
//		btnPrevious.setText("Previous");
//		
//		Composite compositeFiles = new Composite(compositeHistory, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
//		compositeFiles.setLayoutData(new RowData(240, 298));
//		RowLayout rl_compositeFiles = new RowLayout(SWT.VERTICAL);
//		rl_compositeFiles.fill = true;
//		compositeFiles.setLayout(rl_compositeFiles);
//		GridData gd_compositeFiles = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
//		gd_compositeFiles.heightHint = 375;
//		gd_compositeFiles.widthHint = 248;
//		compositeFiles.setLayout(rl_compositeFiles);
//		
//		Label lblCurrentFiles = new Label(compositeFiles, SWT.CENTER);
//		lblCurrentFiles.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
//		lblCurrentFiles.setLayoutData(new RowData(241, SWT.DEFAULT));
//		lblCurrentFiles.setText("Current Files");
//		
//		
//		Label lblcuSource = new Label(compositeFiles, SWT.CENTER);
//		lblcuSource.setText("kernel source");
//		
//		Composite composite = new Composite(compositeFiles, SWT.NONE);
//		composite.setLayout(new GridLayout(2, false));
//		
//		txtcu = new Text(composite, SWT.BORDER);
//		txtcu.setToolTipText("                  ");
//		GridData gd_txtcu = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//		gd_txtcu.widthHint = 166;
//		txtcu.setLayoutData(gd_txtcu);
//		txtcu.setText("*.cu");
//		
//		Button btnBrowse = new Button(composite, SWT.NONE);
//		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		btnBrowse.setText("Browse");
//		
//		Label lblptxSource = new Label(compositeFiles, SWT.CENTER);
//		lblptxSource.setText("ptx source");
//		
//		Composite composite_1 = new Composite(compositeFiles, SWT.NONE);
//		composite_1.setLayout(new GridLayout(2, false));
//		
//		txtptx = new Text(composite_1, SWT.BORDER);
//		txtptx.setText("*.ptx");
//		GridData gd_txtptx = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//		gd_txtptx.widthHint = 166;
//		txtptx.setLayoutData(gd_txtptx);
//		
//		Button button = new Button(composite_1, SWT.NONE);
//		button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		button.setText("Browse");
//		
//		Label lbltxtCompilerMessages = new Label(compositeFiles, SWT.CENTER);
//		lbltxtCompilerMessages.setText("compiler messages");
//		
//		Composite composite_3 = new Composite(compositeFiles, SWT.NONE);
//		composite_3.setLayout(new GridLayout(2, false));
//		
//		text = new Text(composite_3, SWT.BORDER);
//		text.setText("*.txt");
//		GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
//		gd_text.widthHint = 166;
//		text.setLayoutData(gd_text);
//		
//		Button button_2 = new Button(composite_3, SWT.NONE);
//		button_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
//		button_2.setText("Browse");
//		
//		Canvas canvasNext = new CanvasGPU(compositeHistory, SWT.NONE);
//		canvasNext.setLayoutData(new RowData(244, 183));
//		
//		Button btnNext = new Button(canvasNext, SWT.NONE);
//		btnNext.setBounds(10, 10, 81, 26);
//		btnNext.setText("Next");
		
		sourceCode = new Text(shlVisualProfiler, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		//gd_sourceCode.widthHint = 366;
		sourceCode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		sourceCode.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		sourceCode.setEditable(false);
		
		ptxCode = new Text(shlVisualProfiler, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		ptxCode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		ptxCode.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		ptxCode.setEditable(false);
		
		textSearchCode = new Text(shlVisualProfiler, SWT.BORDER | SWT.H_SCROLL | SWT.SEARCH | SWT.CANCEL);
		GridData gd_textSearchCode = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textSearchCode.minimumWidth = 60;
		gd_textSearchCode.minimumHeight = 20;
		textSearchCode.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		textSearchCode.setLayoutData(gd_textSearchCode);
		
		textSearchPtx = new Text(shlVisualProfiler, SWT.BORDER | SWT.H_SCROLL | SWT.SEARCH | SWT.CANCEL);
		GridData gd_textSearchPtx = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		gd_textSearchPtx.widthHint = 346;
		gd_textSearchPtx.minimumWidth = 60;
		gd_textSearchPtx.minimumHeight = 20;
		textSearchPtx.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		textSearchPtx.setLayoutData(gd_textSearchPtx);


	}
}
