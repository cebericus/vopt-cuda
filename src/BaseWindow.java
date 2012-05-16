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


/**
 * Main() and window shell written in SWT for visual profiler tool
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-14.   
 */
public class BaseWindow {

	protected Shell shlVisualProfiler;
	private Text sourceCode;
	private Text ptxCode;
	private Text textSearchCode;
	private Text textSearchPtx;

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
		shlVisualProfiler = new Shell();
		shlVisualProfiler.setForeground(SWTResourceManager.getColor(76, 76, 76));
		shlVisualProfiler.setSize(792, 666);
		shlVisualProfiler.setText("Visual Profiler 4b");
		GridLayout gl_shlVisualProfiler = new GridLayout(4, true);
		shlVisualProfiler.setLayout(gl_shlVisualProfiler);
		
		Menu menuMain = new Menu(shlVisualProfiler, SWT.BAR);
		shlVisualProfiler.setMenuBar(menuMain);
		
		MenuItem mntmFile = new MenuItem(menuMain, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menuFile = new Menu(mntmFile);
		mntmFile.setMenu(menuFile);
		
		MenuItem mntmNewItem = new MenuItem(menuFile, SWT.NONE);
		mntmNewItem.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				
			}
		});
		mntmNewItem.setText("New");
		
		MenuItem mntmOpen = new MenuItem(menuFile, SWT.NONE);
		mntmOpen.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileHandler fh = 
						new FileHandler( BaseWindow.this.shlVisualProfiler);

				fh.onLoad( sourceCode );
				fh.onLoad( ptxCode );
			}
		});
		mntmOpen.setText("Open");

		MenuItem mntmExit = new MenuItem(menuFile, SWT.NONE);
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
		
		Composite composite = new Composite(shlVisualProfiler, SWT.NONE);
		RowLayout rl_composite = new RowLayout(SWT.HORIZONTAL);
		rl_composite.fill = true;
		composite.setLayout(rl_composite);
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite.heightHint = 375;
		gd_composite.widthHint = 190;
		composite.setLayoutData(gd_composite);
		
		Label lblGlobalMemory = new Label(composite, SWT.BORDER | SWT.CENTER);
		lblGlobalMemory.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblGlobalMemory.setAlignment(SWT.CENTER);
		lblGlobalMemory.setLayoutData(new RowData(183, SWT.DEFAULT));
		lblGlobalMemory.setText("Global Memory");
		
		Label lblGlobalMemoryStores = new Label(composite, SWT.NONE);
		lblGlobalMemoryStores.setAlignment(SWT.CENTER);
		lblGlobalMemoryStores.setLayoutData(new RowData(188, SWT.DEFAULT));
		lblGlobalMemoryStores.setText("--Store Instructions--");
		
		Label lblNewLabel = new Label(composite, SWT.CENTER);
		lblNewLabel.setLayoutData(new RowData(183, SWT.DEFAULT));
		lblNewLabel.setText("Coalesced");
		
		ProgressBar progressBar = new ProgressBar(composite, SWT.NONE);
		progressBar.setSelection(64);
		progressBar.setLayoutData(new RowData(185, SWT.DEFAULT));
		
		Label lblGstuncoalesced = new Label(composite, SWT.CENTER);
		lblGstuncoalesced.setLayoutData(new RowData(185, SWT.DEFAULT));
		lblGstuncoalesced.setText("Uncoalesced");
		
		ProgressBar progressBar_1 = new ProgressBar(composite, SWT.NONE);
		progressBar_1.setSelection(34);
		progressBar_1.setLayoutData(new RowData(184, SWT.DEFAULT));
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new RowData(187, 3));
		
		Label lblLoadInstructions = new Label(composite, SWT.NONE);
		lblLoadInstructions.setLayoutData(new RowData(187, SWT.DEFAULT));
		lblLoadInstructions.setText("--Load Instructions--");
		lblLoadInstructions.setAlignment(SWT.CENTER);
		
		Label lblGldcoalesced = new Label(composite, SWT.CENTER);
		lblGldcoalesced.setLayoutData(new RowData(186, SWT.DEFAULT));
		lblGldcoalesced.setText("Coalesced");
		
		ProgressBar progressBar_2 = new ProgressBar(composite, SWT.NONE);
		progressBar_2.setSelection(23);
		progressBar_2.setLayoutData(new RowData(184, SWT.DEFAULT));
		
		Label lblGlduncoalesced = new Label(composite, SWT.CENTER);
		lblGlduncoalesced.setLayoutData(new RowData(186, SWT.DEFAULT));
		lblGlduncoalesced.setText("Uncoalesced");
		
		ProgressBar progressBar_3 = new ProgressBar(composite, SWT.NONE);
		progressBar_3.setSelection(77);
		progressBar_3.setLayoutData(new RowData(184, SWT.DEFAULT));
		
		Label lblSMemory = new Label(composite, SWT.BORDER | SWT.CENTER);
		lblSMemory.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblSMemory.setLayoutData(new RowData(182, SWT.DEFAULT));
		lblSMemory.setText("Shared Memory");
		lblSMemory.setAlignment(SWT.CENTER);
		
		ProgressBar progressBar_4 = new ProgressBar(composite, SWT.NONE);
		progressBar_4.setSelection(10);
		progressBar_4.setLayoutData(new RowData(187, SWT.DEFAULT));
		
		Label lblRegisters = new Label(composite, SWT.BORDER | SWT.CENTER);
		lblRegisters.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblRegisters.setLayoutData(new RowData(182, SWT.DEFAULT));
		lblRegisters.setText("Registers");
		lblRegisters.setAlignment(SWT.CENTER);
		
		ProgressBar progressBar_5 = new ProgressBar(composite, SWT.NONE);
		progressBar_5.setSelection(100);
		progressBar_5.setLayoutData(new RowData(186, SWT.DEFAULT));
		
		Label lblLocalMemoryslow = new Label(composite, SWT.BORDER | SWT.CENTER);
		lblLocalMemoryslow.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblLocalMemoryslow.setLayoutData(new RowData(181, SWT.DEFAULT));
		lblLocalMemoryslow.setText("Local Memory (slow!)");
		lblLocalMemoryslow.setAlignment(SWT.CENTER);
		
		ProgressBar progressBar_6 = new ProgressBar(composite, SWT.NONE);
		progressBar_6.setSelection(15);
		progressBar_6.setLayoutData(new RowData(187, SWT.DEFAULT));
		
		Label lblConstantMemory = new Label(composite, SWT.BORDER | SWT.CENTER);
		lblConstantMemory.setFont(SWTResourceManager.getFont("Ubuntu", 11, SWT.BOLD));
		lblConstantMemory.setLayoutData(new RowData(182, SWT.DEFAULT));
		lblConstantMemory.setText("Constant Memory");
		lblConstantMemory.setAlignment(SWT.CENTER);
		
		ProgressBar progressBar_7 = new ProgressBar(composite, SWT.NONE);
		progressBar_7.setSelection(12);
		progressBar_7.setLayoutData(new RowData(186, SWT.DEFAULT));
		
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
