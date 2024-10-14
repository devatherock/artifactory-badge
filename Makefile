.PHONY: build

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
perf-test:
	rm -rf logs-perf.txt
	DOCKER_TAG=$(docker_tag) docker compose -f docker-compose-perf.yml up --wait
	docker logs -f artifactory-badge-perf > logs-perf.txt &
	./gradlew gatlingRun --all $(additional_gradle_args)
	docker compose down
build:
	./gradlew build
fast-build:
	./gradlew build -x test
docker-build:
	./gradlew dockerBuildNative -Dnative.threads=2 -Dnative.xmx=3072m \
	    -Dnative.tag=$(docker_tag) -Dnative.arch=native -Dnative.mode=dev
