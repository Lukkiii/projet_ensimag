// Tiny deca library for compatibilty with print(), println(), readInt(), ...

#include <stdio.h>
#include <stdlib.h>


void write_bool(int val) {
    if (val) {
        printf("true");
        return;
    }
    printf("false");
}

void write_bool_nl(int val) {
    if (val) {
        printf("true\n");
        return;
    }
    printf("false\n");
}


void write_int(int val) {
    printf("%d", val);
}

void write_int_nl(int val) {
    printf("%d\n", val);
}

void write_int_x(int val) {
    printf("0x%08x", val);
}

void write_int_nl_x(int val) {
    printf("0x%08x\n", val);
}


void write_float(float val) {
    printf("%.5e", val);
}

void write_float_nl(float val) {
    printf("%.5e\n", val);
}

void write_float_x(float val) {
    printf("0x%08x", (unsigned int)val);
}

void write_float_nl_x(float val) {
    printf("0x%08x\n", (unsigned int)val);
}


void write_str(char * s) {
    printf("%s", s);
}

void write_str_nl(char *s) {
    printf("%s\n", s);
}

int read_int() {
    int ret;
    if (scanf("%d", &ret) != 1) {
        printf("Error : Input/Output error\n");
        exit(EXIT_FAILURE);
    }
    return ret;
}

float read_float() {
    float ret;
    if (scanf("%f", &ret) != 1) {
        printf("Error : Input/Output error\n");
        exit(EXIT_FAILURE);
    }
    return ret;
}


float conv_float(int val) {
    return __floatsisf(val);
}

float add_float(float a, float b) {
    return __addsf3(a, b);
}

void leave(int status) {
    exit(status);
}