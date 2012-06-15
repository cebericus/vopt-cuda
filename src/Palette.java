/** Copyright 2012 Cole Nelson */

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Application wide set of SWT colors
 * 
 * @author nelsoncs
 */
public class Palette {
	
	private Display display;
	
	private Color blk;
	private Color wht;
	private Color grey;
	
	private Color red_angry;
	private Color red_calm;
	
	private Color or_calm;
	
	private Color grn_crazy;
	private Color grn_calm;
	
	private Color bl_deep;
	private Color bl_calm;

	/**
	 * 
	 * @param display
	 */
	public Palette( Display display ) {

		this.display = display;
		
		this.blk = new Color( display, 34, 34, 34 );
		this.wht = new Color( display, 244, 244, 244 );
		this.grey = new Color( display, 219, 219, 219 );

		this.red_angry = new Color( display, 185, 0, 0 );
		this.red_calm = new Color( display, 240, 188, 189 );
		
		this.or_calm = new Color( display, 246, 215, 188 );
		
		this.grn_crazy = new Color( display, 45, 196 , 43 );
		this.grn_calm = new Color( display, 212, 220 , 185 );
		
		this.bl_deep = new Color( display, 0, 0, 235 );
		this.bl_calm = new Color( display, 194, 201, 227 );
	}

	public void dispose(){
		
		this.blk.dispose();
		this.wht.dispose();
		this.grey.dispose();
		
		this.red_angry.dispose();
		this.red_calm.dispose();
		
		this.or_calm.dispose();
		
		this.grn_crazy.dispose();
		this.grn_calm.dispose();
		
		this.or_calm.dispose();
		this.bl_calm.dispose();
	}
	
	
	/**
	 * @return the display
	 */
	public Display getDisplay() {
		return display;
	}

	/**
	 * @return the blk
	 */
	public Color getBlk() {
		return blk;
	}

	/**
	 * @return the wht
	 */
	public Color getWht() {
		return wht;
	}

	/**
	 * @return the grey
	 */
	public Color getGrey() {
		return grey;
	}

	/**
	 * @return the red_angry
	 */
	public Color getRed_angry() {
		return red_angry;
	}

	/**
	 * @return the red_calm
	 */
	public Color getRed_calm() {
		return red_calm;
	}

	/**
	 * @return the or_calm
	 */
	public Color getOr_calm() {
		return or_calm;
	}

	/**
	 * @return the grn_crazy
	 */
	public Color getGrn_crazy() {
		return grn_crazy;
	}

	/**
	 * @return the grn_calm
	 */
	public Color getGrn_calm() {
		return grn_calm;
	}

	/**
	 * @return the bl_deep
	 */
	public Color getBl_deep() {
		return bl_deep;
	}

	/**
	 * @return the bl_calm
	 */
	public Color getBl_calm() {
		return bl_calm;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
