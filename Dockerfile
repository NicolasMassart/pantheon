# Build image stage
# Use openJDK 8 to build the image for multi-stage build
FROM openjdk:8-jdk-slim AS builder
# Copy Pantheon sources, note the .dockerignore excluding the copy of
# non necessary files for the build stage
COPY ./ /pantheon
WORKDIR /pantheon
# build the base binaries without archiving
RUN ./gradlew installDist

# Run image stage
# Use openJDK JRE only for running pantheon
FROM openjdk:11-jre-slim
# Copy built binaries from the previous step image
COPY --from=builder /pantheon/build/install/pantheon /opt/pantheon
WORKDIR /opt/pantheon
# Expose services ports
# 8545 HTTP JSON-RPC
# 8546 WS JSON-RPC
# 8547 HTTP GraphQL
# 30303 P2P
EXPOSE 8545 8546 8547 30303
ENTRYPOINT ["/opt/pantheon/bin/pantheon"]
# Build-time metadata as defined at http://label-schema.org
# Use the build_image.sh script in the kubernetes directory of this project to
# easily build this image or as an example of how to inject build parameters.
ARG BUILD_DATE
ARG VCS_REF
ARG VERSION
LABEL org.label-schema.build-date=$BUILD_DATE \
      org.label-schema.name="Pantheon" \
      org.label-schema.description="Enterprise Ethereum client" \
      org.label-schema.url="https://docs.pantheon.pegasys.tech/" \
      org.label-schema.vcs-ref=$VCS_REF \
      org.label-schema.vcs-url="https://github.com/PegaSysEng/pantheon.git" \
      org.label-schema.vendor="Pegasys" \
      org.label-schema.version=$VERSION \
      org.label-schema.schema-version="1.0"