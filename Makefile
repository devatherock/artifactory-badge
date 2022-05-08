clean:
	./gradlew clean
integration-test:
	docker-compose up > /dev/null &
	./gradlew integrationTest
	docker-compose down