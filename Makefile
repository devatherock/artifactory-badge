DOCKER_TAG=latest

clean:
	./gradlew clean
integration-test:
	docker-compose up > /dev/null &
	./gradlew integrationTest
	docker-compose down
docker-build:
	./gradlew clean build -Dgraalvm=true
	docker build -t devatherock/artifactory-badge:$(DOCKER_TAG) .