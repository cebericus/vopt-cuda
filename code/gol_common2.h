#ifndef INCLUDES
#define INCLUDES

#include <stdlib.h> // for rand
#include <stdio.h> // for printf
#include <cuda.h> // for CUDA
#include <time.h>

#define P_WIDTH 60
#define P_HEIGHT 30
#define HEIGHT 1024
#define WIDTH 1024
#define BLOCK_WIDTH 16
#define BLOCK_HEIGHT 16

extern const int field_dim;
extern const size_t field_size;
extern const size_t row_bytes;

// set the grid/block dimensions for kernel execution
extern const dim3 gridDim;
extern const dim3 blocksDim; // 256 threads per block

void cudaCheckError(const char *);
void fill_board(unsigned char *);
void print_board(const unsigned char *);
void cudaCheckError (const char *);
void animate(void (*)(void), const unsigned char *board);

#endif