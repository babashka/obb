#!/usr/bin/env bash

prefix="$1"

# obb executable
mkdir -p "$prefix/bin"
echo 'globalThis.obb_brew = true;' >> obb
echo "globalThis.obb_lib_dir = \"$prefix/libexec\";" >> obb
cp obb "$prefix/bin"

# internal libs
mkdir -p "$prefix/libexec"
cp libexec/* "$prefix/libexec"

mkdir -p "$prefix/bin"
cp obb "$prefix/bin"
