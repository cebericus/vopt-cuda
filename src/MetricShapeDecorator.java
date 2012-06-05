/**
 * 
 */


/**
 * @author nelsoncs
 *
 */
abstract public class MetricShapeDecorator implements MetricShape {
	
	protected final MetricShapeBase base;
	
	public MetricShapeDecorator( MetricShapeBase base ){
		this.base = base;
	}


	abstract public void draw();


//	public void setColor(int r, int g, int b) {
//		// TODO Auto-generated method stub
//		
//	}
//
//
//	public void tryDispose() {
//		// TODO Auto-generated method stub
//		
//	}
//	
	

}
