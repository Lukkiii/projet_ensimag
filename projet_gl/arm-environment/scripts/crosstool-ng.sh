#!/bin/sh

usage() {
	echo "$0 <install_path> [--debug]"
	echo "<install_path> = path where the tools will be installed"
	echo ""
	echo "Once installed and env.sh sourced, you should be able to run the following commands:"
	echo "compile-deca-arm <file>.s -o <file>"
	echo "qemu-deca-arm <file>"
	exit
}

if [ -z "$1" ]; then
	usage
fi
if [ ! -d "$1" ]; then
	mkdir -p $1
fi
if [ "$2" = "--debug" ]; then
	debug=true
fi

script_path=$(realpath $0)
cwd=$(realpath $1)
cd $cwd

wget http://crosstool-ng.org/download/crosstool-ng/crosstool-ng-1.26.0.tar.xz
tar xf crosstool-ng-1.26.0.tar.xz || exit
rm crosstool-ng-1.26.0.tar.xz || exit
cd crosstool-ng-1.26.0
./bootstrap || exit
./configure --prefix=$(realpath ../) || exit
make || exit
make install || exit
cd ..
#rm -r crosstool-ng-1.26.0 || exit