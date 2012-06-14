/** Copyright 2012 Cole Nelson  */

import static jcuda.driver.CUdevice_attribute.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;

/**
 * Drawable canvas for visual profiler tool
 * @author nelsoncs 2012-May-14 
 */
public class CanvasGPU extends Canvas implements MouseListener, MouseMoveListener {
	
	static final int FONT_DISPLAY_HEIGHT = 25; 
	static final int FONT_SZ = 14;
	static final String FONT = "Calibri";
	static final int FONT_STYLE = SWT.BOLD;
	static final int LABEL_STYLE = SWT.RIGHT;
	static final int LABEL_WIDTH = 50;
	static final int LABEL_POSN = 940;
	static final int TEXT_STYLE = SWT.BORDER | SWT.RIGHT;

	CanvasGPU( Composite shlVisualProfiler, DeviceQuery devices, int style ) {
		
		super( shlVisualProfiler, style | SWT.BORDER );
		
		/** font for labels and widgets */
		Font font = new Font( shlVisualProfiler.getDisplay(), FONT, FONT_SZ, FONT_STYLE ); 
		
		/** set the widgets on the canvas */
		
		/** default to first device */
		/** TODO enable switching devices for occupancy calculator */
		
		/** Compute capability */
		Label labelComputeCapability = new Label( this, SWT.NULL );
		labelComputeCapability.setFont(font);
		labelComputeCapability.setText( devices.get()[0].getMajor() + "."
											+ devices.get()[0].getMajor() );
		labelComputeCapability.setBounds( 968,  15, 40, FONT_DISPLAY_HEIGHT );
		
		
		/** Shared memory */
		Label labelSharedMemAlloc = new Label( this, LABEL_STYLE );
		labelSharedMemAlloc.setFont(font);
		labelSharedMemAlloc.setText( Integer.toString( 
				devices.get()[0].sharedMemAllocUnitSize() ) );
		labelSharedMemAlloc.setBounds( LABEL_POSN, 100, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Combo comboMaxShared = new Combo( this, SWT.NULL );
		comboMaxShared.setFont(font);
		comboMaxShared.add( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK) ) );
		comboMaxShared.add( "16384" );
		comboMaxShared.select(0);
		comboMaxShared.setBounds( 900, 132, 100, FONT_DISPLAY_HEIGHT );
		
		
		/** Registers */
		Label labelRegMaxPerThread = new Label( this, LABEL_STYLE );
		labelRegMaxPerThread.setFont(font);
		labelRegMaxPerThread.setText( Integer.toString( 
				devices.get()[0].regMaxPerThread() ) );
		labelRegMaxPerThread.setBounds( LABEL_POSN, 265, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelRegFileSz = new Label( this, LABEL_STYLE );
		labelRegFileSz.setFont(font);
		labelRegFileSz.setText( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK) ) );
		labelRegFileSz.setBounds( LABEL_POSN, 305, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelRegAllocUnit = new Label( this, LABEL_STYLE );
		labelRegAllocUnit.setFont(font);
		labelRegAllocUnit.setText( Integer.toString( 
				devices.get()[0].regAllocUnitSz() ) );
		labelRegAllocUnit.setBounds( LABEL_POSN, 340, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelRegGranularity = new Label( this, LABEL_STYLE );
		labelRegGranularity.setFont(font);
		labelRegGranularity.setText( devices.get()[0].regAllocGran() );
		labelRegGranularity.setBounds( LABEL_POSN, 380, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		/** SM limits */
		Label labelmaxSMThreadBlocks = new Label( this, LABEL_STYLE );
		labelmaxSMThreadBlocks.setFont(font);
		labelmaxSMThreadBlocks.setText( Integer.toString( 
				devices.get()[0].maxSMThreadBlocks() ) );
		labelmaxSMThreadBlocks.setBounds( LABEL_POSN, 470, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelmaxSMWarps = new Label( this, LABEL_STYLE );
		labelmaxSMWarps.setFont(font);
		labelmaxSMWarps.setText( Integer.toString( 
				devices.get()[0].maxSMWarps() ) );
		labelmaxSMWarps.setBounds( LABEL_POSN, 515, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		/** Threads */
		Label labelMaxThdPerBlk = new Label( this, LABEL_STYLE );
		labelMaxThdPerBlk.setFont(font);
		labelMaxThdPerBlk.setText( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK) ) );
		labelMaxThdPerBlk.setBounds( LABEL_POSN, 615, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelThdPerWarp = new Label( this, LABEL_STYLE );
		labelThdPerWarp.setFont(font);
		labelThdPerWarp.setText( Integer.toString( 
				devices.get()[0].getAttributes().get( CU_DEVICE_ATTRIBUTE_WARP_SIZE ) ) );
		labelThdPerWarp.setBounds( LABEL_POSN, 653, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		Label labelMaxThdPerSM = new Label( this, LABEL_STYLE );
		labelMaxThdPerSM.setFont(font);
		labelMaxThdPerSM.setText( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR) ) );
		labelMaxThdPerSM.setBounds( LABEL_POSN, 690, LABEL_WIDTH, FONT_DISPLAY_HEIGHT );
		
		/** User side controls and labels */
		
		Combo comboSharedMemPerBlock = new Combo( this, TEXT_STYLE );
		comboSharedMemPerBlock.setFont(font);
		comboSharedMemPerBlock.setText( "" );
		comboSharedMemPerBlock.setBounds( 5, 132, 160, FONT_DISPLAY_HEIGHT );
		/** Note: these setFocus() calls are used to cover the chinsy arrows */
		comboSharedMemPerBlock.setFocus();
		
		Combo comboRegsPerThread = new Combo( this, TEXT_STYLE );
		comboRegsPerThread.setFont(font);
		comboRegsPerThread.setText( "" );
		comboRegsPerThread.setBounds( 5, 369, 160, FONT_DISPLAY_HEIGHT );
		comboRegsPerThread.setFocus();
		
		Combo comboThreadsPerBlock = new Combo( this, TEXT_STYLE );
		comboThreadsPerBlock.setFont(font);
		comboThreadsPerBlock.setText( "" );
		comboThreadsPerBlock.setBounds( 5, 520, 160, FONT_DISPLAY_HEIGHT );
		comboThreadsPerBlock.setFocus();
		
		Text textThreadsPerBlockX = new Text( this, TEXT_STYLE );
		textThreadsPerBlockX.setFont(font);
		textThreadsPerBlockX.setBounds( 5, 560, 40, FONT_DISPLAY_HEIGHT );
		Text textThreadsPerBlockY = new Text( this, TEXT_STYLE );
		textThreadsPerBlockY.setFont(font);
		textThreadsPerBlockY.setBounds( 55, 560, 40, FONT_DISPLAY_HEIGHT );
		Text textThreadsPerBlockZ = new Text( this, TEXT_STYLE );
		textThreadsPerBlockZ.setFont(font);
		textThreadsPerBlockZ.setBounds( 105, 560, 40, FONT_DISPLAY_HEIGHT );
		
		comboSharedMemPerBlock.setFocus();
		comboSharedMemPerBlock.select(0);
		
		/** Canvas SWT Listeners */
		
		/** Paint listener */
		this.addListener (SWT.Paint, new Listener () {
			public void handleEvent (Event event) {
				CanvasGPU.this.Draw( event );
			}
		});

		/** keyboard navigation */
		this.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent ke )
			{				
				CanvasGPU.this.keyPressed( ke );
			}
		} );
		
		/** required for abstract interface spec. */
		this.addMouseListener( this );
		this.addMouseMoveListener( this );
		
	}


	/** 
	 * draw png flow chart on the canvas
	 * 
	 * @param event
	 */
	private void Draw(Event event) {
		
		try {
			
			ImageData image_data = new ImageData( "Calculator.png" );

			Image image = new Image(  this.getDisplay(), image_data );
			

			GC gc = new GC( this );
			
			//gc.setAlpha(64);

			gc.drawImage( image, 5, 5);

			image.dispose();
			gc.dispose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param str_x
	 * @param str_y
	 * @param str_z
	 */
	public void setThreadPerBlockXYZ( String str_x, String str_y, String str_z ){
		
		
		
	}


	protected void keyPressed(KeyEvent ke) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseMove(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDoubleClick(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDown(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseUp(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
