#!/bin/bash

set -e

# Set environment
export LANG="C.UTF-8"
export VERSION=$1

if [ -z "$VERSION" ]; then
	export VERSION=0-master-SNAPSHOT
fi

RUN_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd $RUN_PATH

./step-update-copyrights.sh
./step-clean-compile.sh
