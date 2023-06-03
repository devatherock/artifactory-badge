DOCKER_TAG=latest

clean:
	./gradlew clean
integration-test:
	docker-compose up &
	./gradlew integrationTest --tests '*ControllerIntegrationSpec*'
	docker-compose down
remote-integration-test:
	docker-compose -f docker-compose-remote.yml up &
	./gradlew integrationTest --tests '*RemoteUrlsIntegrationSpec*'
	docker-compose down
build:
	./gradlew build -Dgraalvm=true
docker-build:
	docker build -t devatherock/artifactory-badge:$(DOCKER_TAG) .