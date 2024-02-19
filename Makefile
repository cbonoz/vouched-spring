deploy:
	DOCKER_BUILDKIT=0 fly deploy


test:
	./gradlew test

# db:
#   fly postgres connect -a vouched-db