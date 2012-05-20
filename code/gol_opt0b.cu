/* 
   Original Author of gol_textual.c:
	Christopher Mitchell <chrism@lclark.edu>
   CUDAfied version by Michael Barger <mbarger@pdx.edu>
    for Homework 2 for CS510[GPU] (Prof Karavanic)
 */	

#include "gol_common2.h"


// The two boards -- host only needs one
unsigned char h_current[WIDTH * HEIGHT];
unsigned char *d_current;
unsigned char *d_next;

const dim3 gridDim(8, 8, 1);
const dim3 blocksDim(16, 16, 1); // 256 threads per block


extern "C" __global__ void step (const unsigned char *current, unsigned char *next) {
    // coordinates of the cell we're currently evaluating
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    // offset index, neighbor coordinates, alive neighbor count
    int i, nx, ny, num_neighbors;
    
    const int offsets[8][2] = {{-1, 1},{0, 1},{1, 1},
                           {-1, 0},       {1, 0},
                           {-1,-1},{0,-1},{1,-1}};

    // count this cell's alive neighbors
    num_neighbors = 0;
    for (i=0; i<8; i++) {
        // To make the board torroidal, we use modular arithmetic to
        // wrap neighbor coordinates around to the other side of the
        // board if they fall off.
        nx = (x + offsets[i][0] + WIDTH) % WIDTH;
        ny = (y + offsets[i][1] + HEIGHT) % HEIGHT;
        num_neighbors += current[ny * WIDTH + nx]==1;
    }

    // apply the Game of Life rules to this cell
    next[y * WIDTH + x] = ((current[y * WIDTH + x] && num_neighbors==2) || num_neighbors==3);
}


void loop_func() {
    step<<<gridDim, blocksDim>>>(d_current, d_next);
    cudaCheckError("kernel execution");

    cudaMemcpy(h_current, d_next, field_size, cudaMemcpyDeviceToHost);
    cudaCheckError("Device->Host memcpy");

    cudaMemcpy(d_current, d_next, field_size, cudaMemcpyDeviceToDevice);
    cudaCheckError("Device->Device memcpy");
}


int main(void) {
	// allocate the device-side field arrays
	cudaMalloc((void **)&d_current, field_size);
	cudaMalloc((void **)&d_next, field_size);
	cudaCheckError("device memory allocation");

    // Initialize the host-side "current".
    fill_board(h_current);
    
    // copy host memory to device
    cudaMemcpy(d_current, h_current, field_size, cudaMemcpyHostToDevice);
    cudaCheckError("init array host->device copy");
    
    // run the simulation!
    animate(loop_func, h_current);

	// free device memory
	cudaFree(d_current);
	cudaFree(d_next);
    return 0;
}