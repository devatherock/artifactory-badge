FROM ghcr.io/graalvm/native-image:ol8-java17-22.1.0 as graalvm

COPY . /home/app/micronaut-graal-app
WORKDIR /home/app/micronaut-graal-app

RUN native-image -cp build/libs/artifactory-badge-all.jar



FROM gcr.io/distroless/base-debian11:latest

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="2.0.0"

EXPOSE 8080

COPY --from=graalvm /home/app/micronaut-graal-app/micronautgraalapp /micronaut-graal-app/micronautgraalapp
ENTRYPOINT ["/micronaut-graal-app/micronautgraalapp"]
