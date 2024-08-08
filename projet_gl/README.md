# Projet Génie Logiciel, Ensimag.
gl41, 01/01/2024.

## ARM
### How to create the environment on Ubuntu
- `./arm-environment/scripts/ubuntu.sh <dest_dir>` (Deploy ARM environment)
- `source <dest_dir>/env.sh` (Initialize ARM environment)
### Using provided tools
Once sourced you should have access to the following tools:
- `compile-deca-arm <file>.s` (Compile an ASM file to an ELF executable)
- `qemu-deca-arm <file>` (Run the ELF file using qemu user mode emulation)

Example workflow given a file named *./code.deca*:
- `decac -arm ./code.deca` (Generate a *./code.s* file)
- `compile-deca-arm ./code.s` (Build *./code.s* to *./code*)
- `qemu-deca-arm ./code` (Run newly built executable using qemu)

