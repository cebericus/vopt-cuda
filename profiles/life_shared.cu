/* Modified for CUDA - Cole Nelson - 2012 Apr 11 - mail@colenelson.net
 *
 * To compile for CUDA with debugging and gcc-4.4:
 * nvcc --compiler-bindir /opt/gcc44 -lX11 -g -G -o life_0 ./life_0.cu
 * 
 * Author: Christopher Mitchell <chrism@lclark.edu>
 * Date: 2011-08-10
 *
 * Compile with `gcc -lX11 gol.c`.
 */

#include <stdlib.h> 	// for rand
#include <string.h> 	// for memcpy
#include <stdio.h> 	// for printf
#include <X11/Xlib.h> 	// for the graphics

#include <cuda.h>		// "for speed"

#define WIDTH 800
#define HEIGHT 600


// The game board 
int curr_board[WIDTH * HEIGHT];

// set random starting points on game boarde
void fill_board( int *board ) {
    int i;
    for ( i = 0; i < WIDTH * HEIGHT; ++i )
        board[i] = rand() % 2;
}


// this kernel processes the neighborhood around an individual cell 
__global__ void step_kernel( int * cuda_curr_board, int * cuda_next_board ){
	
	// array initializer not allowed for __shared__
	__shared__ int offsets[8][2];
	
	//{-1, 1},{0, 1},{1, 1},
	offsets[0][0] = -1; offsets[0][1] = 1;
	offsets[1][0] = 0; offsets[1][1] = 1;
	offsets[2][0] = 1; offsets[2][1] = 1;
									
	//{-1, 0},       {1, 0},
	offsets[3][0] = -1; offsets[3][1] = 0;
	offsets[4][0] = 1; offsets[4][1] = 0;
	
	//{-1,-1},{0,-1},{1,-1}};
	offsets[5][0] = -1; offsets[5][1] = -1;
	offsets[6][0] = 0; offsets[6][1] = -1;
	offsets[7][0] = 1; offsets[7][1] = -1;

	// offset index, neighbor coordinates, living neighbor count
	int i, nx, ny, num_neighbors;

	// count this cell's living neighbors
	num_neighbors = 0;
	
	// calculate thread indices
	int x = ( blockIdx.x * blockDim.x ) + threadIdx.x;
	int y = ( blockIdx.y * blockDim.y ) + threadIdx.y;
	
	for( i = 0; i < 8; ++i ) {
		
		// To make the board torroidal, we use modular arithmetic to
		// wrap neighbor coordinates around to the other side of the
		// board if they fall off.
		nx = ( x + offsets[i][0] + WIDTH ) % WIDTH;
		ny = ( y + offsets[i][1] + HEIGHT ) % HEIGHT;
		
		if( cuda_curr_board[ny * WIDTH + nx] ) {
			++num_neighbors;
		}
	}

	// apply the Game of Life rules to this cell
	cuda_next_board[y * WIDTH + x] = 0;
	
	// TODO does this cause thread wave problems?
	if( ( cuda_curr_board[y * WIDTH + x] && ( num_neighbors == 2 ) ||
			num_neighbors == 3 ) ) {
		
		cuda_next_board[y * WIDTH + x] = 1;
		
		// supposedly, lot's of syncthreads are ok
		__syncthreads();
	}
	
	__syncthreads();
}


// creates an X11 window, sets up CUDA memory and enters an endless while(1)
// loop to run the game
void animate() {
    Display* display;
    display = XOpenDisplay(NULL);
    
    if( display == NULL ) {
        fprintf( stderr, "Could not open an X display.\n" );
        exit( -1 );
    }
    
    int screen_num = DefaultScreen( display );

    int black = BlackPixel( display, screen_num );
    int white = WhitePixel( display, screen_num );

    Window win = XCreateSimpleWindow( display,
            RootWindow( display, screen_num ),
            0, 0,
            WIDTH, HEIGHT,
            0,
            black, white );
    
    XStoreName( display, win, "The CUDA Game of Life" );

    XSelectInput( display, win, StructureNotifyMask );
    XMapWindow( display, win );
    while( 1 ) {
        XEvent e;
        XNextEvent( display, & e );
        if( e.type == MapNotify )
            break;	//<--------------------------internal break
    }

    GC gc = XCreateGC( display, win, 0, NULL );

    int x, y, n;						// display coords and points counter
    XPoint points[WIDTH * HEIGHT];		// display array
    
    /* begin setup of cuda specific host code */
    // simple error checking
    cudaError_t err;
    
    // calculate the size of game board
    size_t size_board = WIDTH * HEIGHT * sizeof( int );
    
    // pointers to game boards on the device
    int *cuda_curr_board = NULL;
    int *cuda_next_board = NULL;
    
    // allocate memory on device: cudaError_t cudaMalloc( void ** devPtr, size_t size );
    err = cudaMalloc( (void **) &cuda_curr_board, size_board );
    if( err != cudaSuccess )
    	printf( "cudamalloc error: curr_board\n" );
    
    // allocate memory on device
    err = cudaMalloc( (void **) &cuda_next_board, size_board );
    if( err != cudaSuccess )
    	printf( "cudamalloc error: next_board\n" );
    
    // cuda dimensions
    dim3 threadsPerBlock( 16, 16 );
    dim3 numBlocks( WIDTH / threadsPerBlock.x, HEIGHT / threadsPerBlock.y );
    
    /* endless game loop */
    while( 1 ){
    	
        XClearWindow( display, win );
        
        // init counter for points array
        n = 0;
        
        // set the Xpoints for display from the game board array
        for( y = 0; y < HEIGHT; ++y ) {
            for( x = 0; x < WIDTH; ++x ) {
                if( curr_board[y * WIDTH + x] ) {
                    points[n].x = x;
                    points[n].y = y;
                    ++n;
                }
            }
        }
        
        // display current board state
        XDrawPoints( display, win, gc, points, n, CoordModeOrigin );
        XFlush( display );
        
        // copy game board to device: 
        // cudaError_t cudaMemcpy( void * dst, const void * src, size_t count, enum cudaMemcpyKind kind);
        err = cudaMemcpy( cuda_curr_board, curr_board, size_board, cudaMemcpyHostToDevice );
        if( err != cudaSuccess )
        	printf( "cudaMemcpy error: to device\n" );
        
        // calculate next game board
        step_kernel<<< numBlocks, threadsPerBlock >>>( cuda_curr_board, cuda_next_board );
        
        // copy game board from device to host board
        err = cudaMemcpy( curr_board, cuda_next_board, size_board, cudaMemcpyDeviceToHost );
        if( err != cudaSuccess )
        	printf( "cudaMemcpy error: from device\n" );
    }
    /* end endless game loop */
    
    // Note: unreachable - TODO need to test for a static board instead of while(1)
    cudaFree( cuda_curr_board );
    cudaFree( cuda_next_board );
    /* end of cuda specific host code */
}

// initializes the host side game board and calls animate() to initalize CUDA 
// device game boards and the X11 display
int main( void ) {
	
    // Initialize the global "current_board".
    fill_board( curr_board );
    
    // run game
    animate();

    return 0;
}


