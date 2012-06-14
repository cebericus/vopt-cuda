<b>Visual Occupancy and Performance Tool for CUDA</b> 

Cole S. Nelson, Ph.D. (2012)  

Abstract: 
The Compute Unified Device Architecture (CUDA) is a combined hardware and software 
model for general-purpose graphics processing unit (GP-GPU) parallel computing. Successful 
utilization of GP-GPU computing requires an understanding of the interaction 
between the underlying platform-specific hardware and multiple levels of software 
functionality.  To aid the programmer, numerous detailed reference materials and 
tools are provided by the manufacturer.  However, integrating the multiplicity of 
resources can prove to be a daunting task for the CUDA programmer.  

An important criterion for correct, efficient parallel programs in CUDA is occupancy. The 
transfer of program instructions and data from the host device to the CUDA-enabled 
device is relatively slow and incurs a large overhead in time and physical resources. 
For this reason, it is desirable to utilize the CUDA-enabled hardware to a maximal extent 
and leverage the benefits of parallel execution.  The first step is to examine the 
“occupancy”, meaning compute-cycle and resource utilization.  To analyze device occupancy 
there are three basic, yet separate, tools that the CUDA programmer has available.  
These are deviceQuery which enumerates the CUDA model for the underlying hardware 
platform, the Occupancy Calculator which is a spreadsheet based form that requires the 
output of deviceQuery and the command-line profiler log file which provides run-time 
information.  A programmer is faced with transitioning between these three separate 
resources and some of the 30 or so written reference materials in order to analyze a 
developing CUDA program.

Therefore, I have designed and implemented the Visual Occupancy and Performance Tool 
(VOPT) for CUDA with a user-supportive graphical interface.  VOPT combines the basic 
CUDA resources: deviceQuery, Occupancy Calculator and Command-Line Profiler log files 
into a unified, visual reference and analysis tool.  This removes several practical 
obstacles for the CUDA programmer and facilitates the analysis and optimization of
 CUDA software.

Notes:
=====

Requires SWT. 

http://www.eclipse.org/swt/

Requires JCUDA.

http://www.jcuda.org

Currently using swt 3.7.2, jcuda 0.4.1 and OpenJDK 1.7.
