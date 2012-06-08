

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
import org.eclipse.swt.widgets.Listener;

/**
 * Drawable canvas for visual profiler tool
 * @author nelsoncs 2012-May-14 
 */
public class CanvasGPU extends Canvas implements MouseListener, MouseMoveListener {
	
	static final int FONT_DISPLAY_HEIGHT = 25; 

	CanvasGPU( Composite shlVisualProfiler, DeviceQuery devices, int style ) {
		
		super( shlVisualProfiler, style | SWT.BORDER );
		
		/** font for labels and widgets */
		Font font = new Font( shlVisualProfiler.getDisplay(), "Arial",18,SWT.BOLD ); 
		
		/** set the widgets on the canvas */
		
		/** default to first device */
		/** TODO enable switching devices for occupancy calculator */
		Label labelComputeCapability = new Label( this, SWT.NULL );
		labelComputeCapability.setFont(font);
		labelComputeCapability.setText( devices.get()[0].getMajor() + "."
											+ devices.get()[0].getMajor() );
		labelComputeCapability.setBounds( 970,  15, 40, FONT_DISPLAY_HEIGHT );
		
		Label labelSharedMemAlloc = new Label( this, SWT.NULL );
		labelSharedMemAlloc.setFont(font);
		labelSharedMemAlloc.setText( Integer.toString( 
				devices.get()[0].sharedMemAllocUnitSize() ) + " byte");
		labelSharedMemAlloc.setBounds( 900, 100, 100, FONT_DISPLAY_HEIGHT );
		
		Combo comboMaxShared = new Combo( this, SWT.NULL );
		comboMaxShared.setFont(font);
		comboMaxShared.add( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK) ) );
		comboMaxShared.add( "16384" );
		comboMaxShared.select(0);
		comboMaxShared.setBounds( 900, 132, 100, FONT_DISPLAY_HEIGHT );
		
		Label labelMaxThdPerBlk = new Label( this, SWT.NULL );
		labelMaxThdPerBlk.setFont(font);
		labelMaxThdPerBlk.setText( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK) ) );
		labelMaxThdPerBlk.setBounds( 925, 615, 100, FONT_DISPLAY_HEIGHT );
		
		Label labelThdPerWarp = new Label( this, SWT.NULL );
		labelThdPerWarp.setFont(font);
		labelThdPerWarp.setText( Integer.toString( 
				devices.get()[0].getAttributes().get( CU_DEVICE_ATTRIBUTE_WARP_SIZE ) ) );
		labelThdPerWarp.setBounds( 925, 653, 100, FONT_DISPLAY_HEIGHT );
		
		Label labelMaxThdPerSM = new Label( this, SWT.NULL );
		labelMaxThdPerSM.setFont(font);
		labelMaxThdPerSM.setText( Integer.toString( 
				devices.get()[0].getAttributes().get(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR) ) );
		labelMaxThdPerSM.setBounds( 925, 690, 100, FONT_DISPLAY_HEIGHT );
		
		
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



	protected void Draw(Event event) {
		
		try {
			
			ImageData image_data = new ImageData( "Calculator.png" );
			
			

			Image image = new Image(  this.getDisplay(), image_data );
			
			

			GC gc = new GC( this );
			
			//gc.setAlpha(64);

			gc.drawImage( image, 5, 5);

			image.dispose();

//			ImageData image_data_grid = new ImageData( "Grid.png" );
//
//			Image image_grid = new Image(  this.getDisplay(), image_data_grid );
//
//			gc.drawImage( image_grid, 10, 220);
//
//			image_grid.dispose();
//
//			ImageData image_data_block = new ImageData( "Block.png" );
//
//			Image image_block = new Image(  this.getDisplay(), image_data_block );
//
//			gc.drawImage( image_block, 350, 220);
//
//			image_block.dispose();

			gc.dispose();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
