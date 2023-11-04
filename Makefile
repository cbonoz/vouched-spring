deploy:
	DOCKER_BUILDKIT=0 fly deploy


test:
	./gradlew test
