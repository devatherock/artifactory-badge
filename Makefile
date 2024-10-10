docker_tag=latest

clean:
	rm -rf build
integration-test:
	rm -rf logs-intg.txt
	DOCKER_TAG=$(docker_tag) docker compose up --wait
	docker logs -f artifactory-badge-intg > logs-intg.txt &
	./gradlew integrationTest --tests '*ControllerIntegrationSpec*'
	docker-compose down
remote-integration-test:
	rm -rf logs-intg-remote.txt
	DOCKER_TAG=$(docker_tag) docker compose -f docker-compose-remote.yml up --wait
	docker logs -f artifactory-badge-intg-remote > logs-intg-remote.txt &
	./gradlew integrationTest --tests '*RemoteUrlsIntegrationSpec*'
	docker-compose down
build-all:
	./gradlew build -Dgraalvm=true
docker-build:
	docker build --progress=plain --build-arg QUICK_BUILD=-Ob -t devatherock/artifactory-badge:$(docker_tag) .
