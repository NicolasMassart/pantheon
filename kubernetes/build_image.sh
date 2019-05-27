#!/bin/sh -e
# This script presents a sample way to build Pantheon Docker image
# with automatic build arguments from the current build workspace.
# It must be started from the same path as where the Dockerfile is located.
docker build \
-t pegasyseng/pantheon-kubernetes:latest \
--build-arg BUILD_DATE="`date`" \
--build-arg VCS_REF="`git show -s --format=%h`" \
--build-arg VERSION="`grep -oE "version=(.*)" gradle.properties | cut -d= -f2`" \
.
