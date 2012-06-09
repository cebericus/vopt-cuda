
import static jcuda.driver.CUdevice_attribute.*;
import static jcuda.driver.JCudaDriver.*;

import jcuda.driver.*;

import java.util.*;

/**
 * @author nelsoncs 2012-May-18 
 * 
 * 
 * This class was modeled on the deviceQuery example from the JCUDA.org website.
 */

/**
 * Uses jcuda to to query the cuda device number passed to the constructor as an
 * argument.  A HashMap is created with the key based on the built-in cuda
 * constants for each attribute.
 * 
 * @author nelsoncs 2012-May-18
 *
 */
public class DeviceAttributes {
	
	private String name;

	private int major;
    private int minor;
	
    private HashMap<Integer, Integer> attributes;
	
	/**
	 * constructor
	 * @param dev_num is the device number for which attribute information will
	 * be queried.
	 */
	public DeviceAttributes( int dev_num ) {
		
		/** enumerate available devices */
		CUdevice device = new CUdevice();
        cuDeviceGet( device, dev_num );

        /** device name */
    	byte deviceName[] = new byte[1024];
    	
        cuDeviceGetName( deviceName, deviceName.length, device );
        
        /** device name, convert from array of bytes to java String */
        StringBuilder str = new StringBuilder();
        
        char ch;
        
        for( int i = 0; i < deviceName.length && deviceName[i] != 0; ++i )
        {
            ch = (char) deviceName[i];
            
            str.append( ch );   
        }
        
        this.name = str.toString();

        /** compute capability */
        int majorArray[] = { 0 };
        int minorArray[] = { 0 };

        cuDeviceComputeCapability( majorArray, minorArray, device );
        this.major = majorArray[0];
        this.minor = minorArray[0];
        
        /** device attributes */
        this.attributes = new HashMap<Integer, Integer>();
        
        /** ugly but it works, also note these constants are described
         * in the nvidia cuda library under device management-> 
         * cuDeviceGetAttribute() */
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_BLOCK, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_X, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Y, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_BLOCK_DIM_Z, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_X, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Y, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_GRID_DIM_Z, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_SHARED_MEMORY_PER_BLOCK, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_TOTAL_CONSTANT_MEMORY, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_WARP_SIZE, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_PITCH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_REGISTERS_PER_BLOCK, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_CLOCK_RATE, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_TEXTURE_ALIGNMENT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MULTIPROCESSOR_COUNT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_KERNEL_EXEC_TIMEOUT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_INTEGRATED, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_CAN_MAP_HOST_MEMORY, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_COMPUTE_MODE, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_HEIGHT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_HEIGHT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE3D_DEPTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_HEIGHT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE2D_LAYERED_LAYERS, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_SURFACE_ALIGNMENT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_CONCURRENT_KERNELS, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_ECC_ENABLED, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_PCI_BUS_ID, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_PCI_DEVICE_ID, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_TCC_DRIVER, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MEMORY_CLOCK_RATE, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_GLOBAL_MEMORY_BUS_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_L2_CACHE_SIZE, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAX_THREADS_PER_MULTIPROCESSOR, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_ASYNC_ENGINE_COUNT, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_WIDTH, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_MAXIMUM_TEXTURE1D_LAYERED_LAYERS, 0);
        attributes.put(CU_DEVICE_ATTRIBUTE_PCI_DOMAIN_ID, 0);
        
        /** get attributes */
        int array[] = { 0 };
        
        for( Map.Entry<Integer, Integer> attribute : this.attributes.entrySet() )
        {
            cuDeviceGetAttribute(array, attribute.getKey(), device);
            
        	attribute.setValue( array[0] );
        }
	}
	
	/**
	 * takes the major and minor compute capabilities numbers for any given 
	 * device and returns the number of cores per MP for that class of device
	 * @return # of cores per MP
	 */
	public int coresPerMP() {
		
		int coresPerMP = 0;
		
		switch ( this.getMajor() ) {
		
		case 1:
			coresPerMP = 8;
			break;

		case 2:
			if( this.getMinor() == 0 )
				coresPerMP = 32;
			else
				if( this.getMinor() == 1 )
					coresPerMP = 48;

			break;
			
		default:
			break;
		}
		
		return coresPerMP;
	}
	

	/** could not identify this as an attribute returned by device query
	 * using values from C Programming Guide Appendix F
	 * 
	 * @return size in bytes
	 */
	public Integer maxSMThreadBlocks(){

		Integer max = 0;

		switch ( this.getMajor() ) {

		case 1:
		case 2:
			max = 8;
			break;

		default:
			break;
		}

		return max;
	}
	
	
	/** 
	 * register allocation unit size in bytes
	 * 
	 * using values from C Programming Guide Appendix F
	 * CC 3.X from wikipedia 
	 * @return size in bytes
	 */
	public Integer maxSMWarps(){

		Integer sz = 0;
		int minor = this.getMinor();

		switch ( this.getMajor() ) {

		case 1:
			
			switch( minor ){
			
			case 0:
			case 1:
				sz = 24;
				break;
				
			case 2:
			case 3:
				sz = 32;
				break;
			}

		case 2:
			sz = 48;
			break;
			
		case 3:
			sz = 64;
			break;

		default:
			break;
		}

		return sz;
	}
	
	
	/** could not identify this as an attribute returned by device query, so using
	 * values from occ. calc. worksheet
	 * @return size in bytes
	 */
	public Integer regMaxPerThread(){

		Integer max = 0;

		switch ( this.getMajor() ) {

		case 1:
			max = 124;
			break;

		case 2:
			max = 63;
			break;

		default:
			break;
		}

		return max;
	}
	
	/** 
	 * register allocation granularity
	 * 
	 * using values from occ. calc. worksheet
	 * @return size in bytes
	 */
	public String regAllocGran(){

		String sz = "Unknown";

		switch ( this.getMajor() ) {

		case 1:
			sz = "Block";
			break;

		case 2:
			sz = "Warp";
			break;

		default:
			break;
		}

		return sz;
	}
	
	/** 
	 * register allocation unit size in bytes
	 * 
	 * using values from C Programming Guide Appendix F
	 * @return size in bytes
	 */
	public Integer regAllocUnitSz(){

		Integer sz = 0;
		int minor = this.getMinor();

		switch ( this.getMajor() ) {

		case 1:
			
			switch( minor ){
			
			case 0:
			case 1:
				sz = 256;
				break;
				
			case 2:
			case 3:
				sz = 512;
				break;
			}

		case 2:
			sz = 64;
			break;
			
		case 3:
			sz = 0;
			break;

		default:
			break;
		}

		return sz;
	}
	
	/** could not identify this as an attribute returned by device query, so using
	 * values from occ. calc. worksheet
	 * @return size in bytes
	 */
	public Integer sharedMemAllocUnitSize(){

		Integer sz = 0;

		switch ( this.getMajor() ) {

		case 1:
			sz = 512;
			break;

		case 2:
			sz = 128;
			break;

		default:
			break;
		}

		return sz;
	}
	
 
    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the major
	 */
	public int getMajor() {
		return major;
	}

	/**
	 * @return the minor
	 */
	public int getMinor() {
		return minor;
	}

	/**
	 * @return the attributes
	 */
	public HashMap<Integer, Integer> getAttributes() {
		return attributes;
	}
    
	/**
	 * unused - for testing
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
