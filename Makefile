.PHONY: build

docker_tag=latest
perf_test_name=--simulation=io.github.devatherock.artifactory.controllers.CompositeSimulation

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
	docker compose -f docker-compose-remote.yml down
perf-test:
	make perf-compose-up
	make perf-run
	make perf-compose-down
perf-run:
	./gradlew gatlingRun $(perf_test_name) $(additional_gradle_args)
perf-compose-up:
	rm -rf logs-perf.txt
	DOCKER_TAG=$(docker_tag) docker compose -f docker-compose-perf.yml up --wait
	docker logs -f artifactory-badge-perf > logs-perf.txt &
perf-compose-down:
	docker compose -f docker-compose-perf.yml down
build:
	./gradlew build
fast-build:
	./gradlew build -x test
docker-build:
	./gradlew dockerBuildNative -Dnative.threads=2 -Dnative.xmx=3072m \
	    -Dnative.tag=$(docker_tag) -Dnative.arch=native -Dnative.mode=dev
