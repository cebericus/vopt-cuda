
import static jcuda.driver.CUdevice_attribute.*;
import static jcuda.driver.JCudaDriver.*;

import jcuda.driver.*;

import java.util.*;

/**
 * Team 4b: Michael Barger, Alex Kelly, Cole Nelson
 * @author nelsoncs 2012-May-18 
 * 
 * This class was modeled on the deviceQuery example from the JCUDA.org website.
 * 
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
		
		CUdevice device = new CUdevice();
        cuDeviceGet( device, dev_num );

        /** device name */
    	byte deviceName[] = new byte[1024];
    	
        cuDeviceGetName( deviceName, deviceName.length, device );
        
        this.name = bytesToString( deviceName );

        /** compute capability */
        int majorArray[] = { 0 };
        int minorArray[] = { 0 };

        cuDeviceComputeCapability( majorArray, minorArray, device );
        this.major = majorArray[0];
        this.minor = minorArray[0];
        
        /** device attributes */
        this.attributes = new HashMap<Integer, Integer>();
        
        /** ugly but it works, also note these constants are described
         * in the nvidia cuda library under device management 
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
	
	
    /**
     * Creates a String from a zero-terminated string in a byte array
     * 
     * @param buf byte[]
     * @return String
     */
    private static String bytesToString( byte buf[] )
    {
        StringBuilder str = new StringBuilder();
        char ch;
        
        for (int i = 0; i < buf.length; i++)
        {
            ch = (char) buf[i];
            
            if (ch != 0)
            	str.append( ch );
            else
                break;     
        }
        
        return str.toString();
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
