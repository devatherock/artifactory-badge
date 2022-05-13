FROM ghcr.io/graalvm/native-image:ol8-java11-22.1.0 as graalvm

COPY . /home/app/micronaut-graal-app
WORKDIR /home/app/micronaut-graal-app

RUN native-image -cp build/libs/*-all.jar



FROM frolvlad/alpine-glibc:alpine-3.12

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="1.0.0"

EXPOSE 8080
RUN apk update \
		&& apk add --no-cache libstdc++ dumb-init

COPY --from=graalvm /home/app/micronaut-graal-app/micronautgraalapp /micronaut-graal-app/

ENTRYPOINT ["dumb-init", "--"]
CMD ["sh", "-c", "/micronaut-graal-app/micronautgraalapp ${JAVA_OPTS}"]
