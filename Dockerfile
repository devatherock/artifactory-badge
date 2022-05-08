FROM devatherock/graalvm:ol7-java11-20.3.4-1 as graalvm

COPY . /home/app/micronaut-graal-app
WORKDIR /home/app/micronaut-graal-app

RUN native-image --no-server -cp build/libs/*-all.jar



FROM frolvlad/alpine-glibc:alpine-3.12

LABEL maintainer="devatherock@gmail.com"
LABEL io.github.devatherock.version="0.6.0"

EXPOSE 8080
RUN apk update \
		&& apk add --no-cache libstdc++ dumb-init

COPY --from=graalvm /home/app/micronaut-graal-app/micronautgraalapp /micronaut-graal-app/micronautgraalapp
ENTRYPOINT ["dumb-init", "--", "/micronaut-graal-app/micronautgraalapp"]
