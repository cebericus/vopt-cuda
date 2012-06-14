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
	
	private Text textThreadsPerBlockX;
	private Text textThreadsPerBlockY;
	private Text textThreadsPerBlockZ;
	
	private double threadsPerBlockX;
	private double threadsPerBlockY;
	private double threadsPerBlockZ;

	private Combo comboThreadsPerBlock;
	private double threadsPerBlock;

	/**
	 * constructor
	 * TODO there is an issue with this class, thread and block XYZ need to be 
	 * maintained as a set
	 * so that a change during runtime can be identified and dealt with.
	 * the current practice of using the average over the whole run then casting
	 * it to an int is not going to work in the long run.   So, presently, there
	 * is an assumption overall that there is only one kernel and the kernel 
	 * parameters do not change.
	 * 
	 * @param shlVisualProfiler
	 * @param devices
	 * @param style
	 */
	public CanvasGPU( Composite shlVisualProfiler, DeviceQuery devices, int style ) {
		
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
											+ devices.get()[0].getMinor() );
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
		comboRegsPerThread.setBounds( 5, 369, 160, FONT_DISPLAY_HEIGHT );
		comboRegsPerThread.setFocus();
		
		
		/** BEGIN threads per block */
		
		this.textThreadsPerBlockX = new Text( this, TEXT_STYLE );
		this.textThreadsPerBlockX.setFont(font);
		this.textThreadsPerBlockX.setBounds( 5, 560, 40, FONT_DISPLAY_HEIGHT );
		
		this.textThreadsPerBlockY = new Text( this, TEXT_STYLE );
		this.textThreadsPerBlockY.setFont(font);
		this.textThreadsPerBlockY.setBounds( 55, 560, 40, FONT_DISPLAY_HEIGHT );
		
		this.textThreadsPerBlockZ = new Text( this, TEXT_STYLE );
		this.textThreadsPerBlockZ.setFont(font);
		this.textThreadsPerBlockZ.setBounds( 105, 560, 40, FONT_DISPLAY_HEIGHT );
		
		/** default to 1 */
		this.setThreadsPerBlockX( 1 );
		this.setThreadsPerBlockY( 1 );
		this.setThreadsPerBlockZ( 1 );
		
		this.comboThreadsPerBlock = new Combo( this, TEXT_STYLE );
		this.comboThreadsPerBlock.setFont(font);
		
		/** calculate threads per block ->> move to controller class */
		this.setComboThreadsPerBlock( this.calcThreadsPerBlock() );
		this.comboThreadsPerBlock.setBounds( 5, 520, 160, FONT_DISPLAY_HEIGHT );
		this.comboThreadsPerBlock.setFocus();
		
		comboSharedMemPerBlock.setFocus();
		comboSharedMemPerBlock.select(0);
		

		/** BEGIN Text listeners */
		
		/** TODO better error handling and/or entry validation */
		textThreadsPerBlockX.addListener( SWT.DefaultSelection, new Listener() {

			public void handleEvent(Event e) {
				
				System.out.println( "X defaultSelection ");
				
				
				try {
					/** grab the new value and convert to double, update class attribute */
					CanvasGPU.this.threadsPerBlockX = Double
							.valueOf(textThreadsPerBlockX.getText());

					CanvasGPU.this.setComboThreadsPerBlock(CanvasGPU.this
							.calcThreadsPerBlock());
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});

		textThreadsPerBlockY.addListener( SWT.DefaultSelection, new Listener() {

			public void handleEvent(Event e) {
				
				try {
					/** grab the new value and convert to double, update class attribute */
					CanvasGPU.this.threadsPerBlockY = Double
							.valueOf(textThreadsPerBlockY.getText());

					CanvasGPU.this.setComboThreadsPerBlock(CanvasGPU.this
							.calcThreadsPerBlock());
					
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		
		textThreadsPerBlockZ.addListener( SWT.DefaultSelection, new Listener() {
			
			public void handleEvent(Event e) {
				
				try {

					/** grab the new value and convert to double, update class attribute */
					CanvasGPU.this.threadsPerBlockZ = Double
							.valueOf(textThreadsPerBlockZ.getText());

					CanvasGPU.this.setComboThreadsPerBlock(CanvasGPU.this
							.calcThreadsPerBlock());

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		
		/** Modify listeners to enable profile log parsing */
		/** TODO needs better error checking */
		textThreadsPerBlockX.addListener( SWT.Modify, new Listener() {

			public void handleEvent(Event e) {
				System.out.println( "X defaultSelection ");
				if( e.keyCode == SWT.CR)
					CanvasGPU.this.setComboThreadsPerBlock( CanvasGPU.this.calcThreadsPerBlock() );
			}
		});

		textThreadsPerBlockY.addListener( SWT.Modify, new Listener() {

			public void handleEvent(Event e) {
				CanvasGPU.this.setComboThreadsPerBlock( CanvasGPU.this.calcThreadsPerBlock() );
			}
		});
		
		textThreadsPerBlockZ.addListener( SWT.Modify, new Listener() {

			public void handleEvent(Event e) {
				CanvasGPU.this.setComboThreadsPerBlock( CanvasGPU.this.calcThreadsPerBlock() );
			}
		});
		/** END Text listeners */
		
		

		
		/** END threads per blocks */
		
		
		/** BEGIN Canvas SWT Listeners */
		
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
		
		/** END Canvas SWT Listeners */
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
	 * @return X * Y * Z
	 */
	private double calcThreadsPerBlock(){
		return this.threadsPerBlockX * this.threadsPerBlockY * this.threadsPerBlockZ;
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
	 * @param textThreadsPerBlockX the textThreadsPerBlockX to set
	 */
	private void setTextThreadsPerBlockX( double value ) {
		this.textThreadsPerBlockX.setText( Integer.toString( (int) value ) );
	}


	/**
	 * @param textThreadsPerBlockY the textThreadsPerBlockY to set
	 */
	private void setTextThreadsPerBlockY( double value ) {
		this.textThreadsPerBlockY.setText( Integer.toString( (int) value ) );
	}


	/**
	 * @param textThreadsPerBlockZ the textThreadsPerBlockZ to set
	 */
	private void setTextThreadsPerBlockZ( double value ) {
		this.textThreadsPerBlockZ.setText( Integer.toString( (int) value ) );
	}

	/**
	 * @param threadsPerBlockX the threadsPerBlockX to set
	 */
	public void setThreadsPerBlockX(double threadsPerBlockX) {
		this.threadsPerBlockX = threadsPerBlockX;
		
		this.setTextThreadsPerBlockX( this.threadsPerBlockX );
	}


	/**
	 * sets class attribute and also the text box displayed
	 * 
	 * @param threadsPerBlockY the threadsPerBlockY to set
	 */
	public void setThreadsPerBlockY(double threadsPerBlockY) {
		this.threadsPerBlockY = threadsPerBlockY;
		
		this.setTextThreadsPerBlockY( this.threadsPerBlockY );
	}


	/**
	 * sets class attribute and also the text box displayed
	 * @param threadsPerBlockZ the threadsPerBlockZ to set
	 */
	public void setThreadsPerBlockZ(double threadsPerBlockZ) {
		this.threadsPerBlockZ = threadsPerBlockZ;
		
		this.setTextThreadsPerBlockZ( this.threadsPerBlockZ );
	}
	
	private void setComboThreadsPerBlock( double value ) {
		this.comboThreadsPerBlock.setText( Integer.toString( (int) value ) );
	}
	
	/**
	 * @param threadsPerBlock the threadsPerBlock to set
	 */
	public void setThreadsPerBlock( double threadsPerBlock ) {
		this.threadsPerBlock = threadsPerBlock;
		
		this.setComboThreadsPerBlock( this.threadsPerBlock );
	}


	/**
	 * sets class attribute and also the text box displayed
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
