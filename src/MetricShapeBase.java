/** Copyright 2012 Cole Nelson  */

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

/**
 * Concrete class for MetricShape decorator pattern
 * @author nelsoncs
 */
public class MetricShapeBase implements MetricShape {

	protected Composite parent;
	protected GC gc;
	private Color color;
	protected int x;
	protected int y;
	protected int [] shape;
	protected int length;

	
	/**
	 * 
	 * @param parent
	 * @param x
	 * @param y
	 * @param length
	 * @param style
	 */
	public MetricShapeBase(Composite parent, int x, int y, int length, int style) {
		//super(parent, style);

		this.parent = parent;
		
		/** TODO gc does not need to be the whole map. can be just the polygon */
		this.gc = new GC( this.parent );
		
		this.setColor( 0, 0, 0 );
		
		this.x = x;
		this.y = y;
		
		this.length = length;
		
		this.shape = new int[8];
		
		this.shape[0] = x;
		this.shape[1] = y + 25;
		
		this.shape[2] = x + length;
		this.shape[3] = y + 25;
		
		this.shape[4] = x + length;
		this.shape[5] = y + 40;
		
		this.shape[6] = x;
		this.shape[7] = y + 40;
	}
	
	
	public void draw(){
		try {

			/** check if we still have a graphics context, maybe create one */
			if( this.gc.isDisposed() == true )
				this.gc = new GC( this.parent );
			
			System.out.println( "MetShpBase color: " + this.color.toString() )  ;
			
			this.gc.setBackground( this.color );

			this.gc.setAlpha(255);
			
			/** fillPoly uses background color */
			this.gc.fillPolygon( this.shape );
			
			//this.setTabList();
			
			this.tryDispose();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @param r
	 * @param g
	 * @param b
	 */
	public void setColor( int r, int g, int b ){

		this.color = new Color( this.parent.getDisplay(), r, g, b );
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setColor( Color color ){

		this.setColor( color.getRed(), color.getGreen(), color.getBlue() );
	}
	
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}


	public void tryDispose(){
		
		/** TODO each decorator base and child is destroying and then
		 * recreating GC, might be more efficient to destroy once
		 * if can track the inheritance chain
		 */
//		if( this.getChildren().length == 0 )
//		{
			if( this.gc.isDisposed() == false )
				this.gc.dispose();
			
			if( this.color.isDisposed() == false )
				this.color.dispose();
//		}
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
