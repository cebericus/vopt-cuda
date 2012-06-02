
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Drawable canvas for visual profiler tool
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-14 
 */
public class CanvasGPU extends Canvas implements MouseListener, MouseMoveListener {

	CanvasGPU(Composite shlVisualProfiler, int style) {
		
		super( shlVisualProfiler, style | SWT.BORDER );
		
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
			
			ImageData image_data_global = new ImageData( "Calculator.png" );

			Image image_global = new Image(  this.getDisplay(), image_data_global );

			GC gc = new GC( this );

			gc.drawImage( image_global, 10, 10);

			image_global.dispose();

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
