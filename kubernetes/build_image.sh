#!/bin/sh -e
# This script presents a sample way to build Pantheon Docker image
# with automatic build arguments from the current build workspace.
# It must be started from the same path as where the Dockerfile is located.
# you have to pass the imnage tag as an argument like for instance :
# build_image.sh "pegasyseng/pantheon-kubernetes:develop"

if [ -z "$1" ]
  then
    me=`basename "$0"`
    echo "No image tag argument supplied to ${me}"
    echo "ex.: ${me} \"pegasyseng/pantheon-kubernetes:develop\""
    exit 1
fi

docker build \
-t "$1" \
--build-arg BUILD_DATE="`date`" \
--build-arg VCS_REF="`git show -s --format=%h`" \
--build-arg VERSION="`grep -oE "version=(.*)" gradle.properties | cut -d= -f2`" \
.
