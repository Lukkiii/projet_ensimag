.section .rodata
hello:
	.asciz "Hello world"

.section .text
.globl main
main:
	ldr r0, =hello
	bl write_str
	bl exit
