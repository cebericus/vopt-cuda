/** Copyright 2012 Cole Nelson  */


import javax.management.BadAttributeValueExpException;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

/**
 * @author nelsoncs
 *
 */
public class MetricShapeDecoratorBorder extends MetricShapeDecorator {
	
	protected int [] shape;
	private int [] bar;

	private Color color;
	int line_width;

	/**
	 * @param base
	 */
	public MetricShapeDecoratorBorder( MetricShapeBase base, int width ) {
		
		super( base );		
		
		this.line_width = width;
		
		this.shape = new int[8];
		
		this.shape[0] = base.shape[0] - this.line_width;
		this.shape[1] = base.shape[1] - this.line_width;
		
		this.shape[2] = base.shape[2] + this.line_width;
		this.shape[3] = base.shape[3] - this.line_width;
		
		this.shape[4] = base.shape[4] + this.line_width;
		this.shape[5] = base.shape[5] + this.line_width;
		
		this.shape[6] = base.shape[6] - this.line_width;
		this.shape[7] = base.shape[7] + this.line_width;
	}

	
	/**
	 * 
	 * @param src
	 * @return dest
	 */
	private int [] copy( int [] src ){
		
		int [] dest = new int[src.length];
		
		for( int i = 0; i < src.length ; ++i){
			
			dest[i] = src[i];
		}
			
		return dest;
	}
	
	/* (non-Javadoc)
	 * @see bui.IsoShapeBase#draw()
	 */
	@Override
	public void draw() {
		
		base.draw();
		
		this.drawBorder();

	}
	
	/**
	 * 
	 */
	private void drawBorder(){
		/** check if we still have a graphics context, maybe create one */
		if( base.gc.isDisposed() == true )
			base.gc = new GC( base.parent );

		base.gc.setLineWidth( this.line_width );
		
		/** drawPoly uses foreground color */
		base.gc.setForeground( this.color );

		base.gc.drawPolygon( this.shape );

		//base.gc.drawRoundRectangle(0, 0, width, height, arcWidth, arcHeight)
		
		base.tryDispose();
	}

	/**
	 * 
	 * @param length as a decimal percentage ie 0.667
	 * @param color
	 * @throws BadAttributeValueExpException
	 */
	public void setBar( double length, Color color ) throws BadAttributeValueExpException  {
		
		if( length > 1 || length < 0 ){
			throw new BadAttributeValueExpException(length);
		}
		
		/** check if we still have a graphics context, maybe create one */
		if( base.gc.isDisposed() == true )
			base.gc = new GC( base.parent );

		base.gc.setAlpha(255);

		this.bar = this.copy( base.shape );
		
		int bar_diff = (int) ( (1 - length) * base.length );
		
		/** use ratio of length to set percentage for bar */
		this.bar[2] = this.bar[2] - bar_diff;
		this.bar[4] = this.bar[4] - bar_diff;
		
		base.gc.setBackground( color );
		base.gc.fillPolygon( this.bar );

		this.tryDispose();
	}

	/**
	 * 
	 * @param length
	 * @param r
	 * @param g
	 * @param b
	 * @throws BadAttributeValueExpException
	 */
	public void setBar( double length, int r, int g, int b ) throws BadAttributeValueExpException  {

		Color c = new Color( base.parent.getDisplay(), r, g, b );
		
		this.setBar(length, c);
		
		c.dispose();		
	}
	
	/**
	 * @param r
	 * @param g
	 * @param b
	 */
	@Override
	public void setColor(  int r, int g, int b ){
		this.color = new Color( base.parent.getDisplay(), r, g, b );
	}

	/**
	 * 
	 * @param color
	 */
	public void setColor(  Color color ) {
		this.setColor( color.getRed(), color.getGreen(), color.getBlue() );
	}
	
	
	@Override
	public void tryDispose(){
		
		if( this.color.isDisposed() == false )
			this.color.dispose();
		
		base.tryDispose();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
