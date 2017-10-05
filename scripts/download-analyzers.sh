#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")"

[ $# = 2 ] && version=$2 || version=
[ $# = 1 ] && analyzer=$1 || analyzer=

case $# in
    2)
        analyzer=$1
        version=$2
        ;;
    1)
        analyzer=$1
