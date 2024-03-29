docker_tag=latest

clean:
	./gradlew clean
integration-test:
	DOCKER_TAG=$(docker_tag) docker-compose up &
	./gradlew integrationTest --tests '*ControllerIntegrationSpec*'
	docker-compose down
remote-integration-test:
	DOCKER_TAG=$(docker_tag) docker-compose -f docker-compose-remote.yml up &
	./gradlew integrationTest --tests '*RemoteUrlsIntegrationSpec*'
	docker-compose down
build:
	./gradlew build -Dgraalvm=true
fast-build:
	./gradlew build -Dgraalvm=true -x dependencyCheckAggregate	
docker-build:
	docker build -t devatherock/artifactory-badge:$(docker_tag) .