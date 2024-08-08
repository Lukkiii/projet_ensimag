#!/bin/bash

usage() {
	echo "$0 <install_path> [--debug]"
	echo "<install_path> = path where the tools will be installed"
	echo ""
	echo "Once installed and env.sh sourced, you should be able to run the following commands:"
	echo "compile-deca-arm <file>.s -o <file>"
	echo "qemu-deca-arm <file>"
	exit
}

download() {
  PACKAGES=$1
  apt-get download $(apt-cache depends --recurse --no-recommends --no-suggests \
    --no-conflicts --no-breaks --no-replaces --no-enhances \
    --no-pre-depends ${PACKAGES} | grep "^\w" | grep -v '.*:i386')
}

dl_packages() {
  #download "binutils-arm-linux-gnueabi qemu-user libc6-armel-cross libc6-dev-armel-cross"
  download "libc6-dev-armel-cross gcc-arm-linux-gnueabi qemu-user"
}

x_packages() {
  for f in *.deb; do
    dpkg --extract $f .
  done
}

rm_deb() {
  rm --force *.deb
}

build_libdeca() {
  LIBDECA_SRC=$(dirname $1)/../src/lib/deca.c
  LD_LIBRARY_PATH=$cwd/usr/lib/x86_64-linux-gnu/ ./usr/bin/arm-linux-gnueabi-gcc --sysroot=$cwd -fPIC -shared $LIBDECA_SRC -o ./usr/arm-linux-gnueabi/lib/libdeca.so -lc
}

gef() {
  wget -O $cwd/.gdbinit-gef.py -q https://gef.blah.cat/py
}

env() {
	cat << 'EOF' > $cwd/env.sh
#!/bin/bash
cwd=$(dirname $(realpath ${BASH_SOURCE[0]}))
export LD_LIBRARY_PATH=$cwd/usr/lib/x86_64-linux-gnu/
export PATH=$PATH:$cwd/usr/bin
compile-deca-arm() {
  arm-linux-gnueabi-gcc --sysroot=$cwd -msoft-float $1 -o "${1%.*}" -ldeca
}
alias qemu-deca-arm="qemu-arm -L $cwd/usr/arm-linux-gnueabi/"
EOF
	if [ "$debug" = true ];  then
		cat << 'EOF' >> $cwd/env.sh
alias gef-multiarch="gdb-multiarch -ix $cwd/.gdbinit-gef.py"
EOF
	fi
}

bye() {
	echo "Don't forget to source \"env.sh\" before running any tools from \"$cwd\""
	echo "... and before running compile-deca-arm and qemu-deca-arm"
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

dl_packages
x_packages
rm_deb
build_libdeca $script_path
env
bye
if [ "$debug" = true ]; then
	gef
fi
