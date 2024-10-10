docker_tag=latest

clean:
	rm -rf build
integration-test:
	DOCKER_TAG=$(docker_tag) docker compose up --wait
	./gradlew integrationTest --tests '*ControllerIntegrationSpec*'
	docker-compose down
remote-integration-test:
	DOCKER_TAG=$(docker_tag) docker compose -f docker-compose-remote.yml up --wait
	./gradlew integrationTest --tests '*RemoteUrlsIntegrationSpec*'
	docker-compose down
build-all:
	./gradlew build -Dgraalvm=true
docker-build:
	docker build --progress=plain --build-arg QUICK_BUILD=-Ob -t devatherock/artifactory-badge:$(docker_tag) .
