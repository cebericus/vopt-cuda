/** Copyright 2012 Cole Nelson  */

/**
 * interface specification for isometric shape decorator pattern
 * @author 
 * 
 */
public interface MetricShape {
	public void draw();
	public void setColor( int r, int g, int b );
	public void tryDispose();
}
