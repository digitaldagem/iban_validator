DOCKER_IMAGES = $(shell docker images -q iban_validator-webapp)

up:
	docker-compose up -d --build --remove-orphans

up-local:
	docker-compose up --build --remove-orphans

down:
ifneq ($(strip $(DOCKER_IMAGES)),)
	docker-compose down -v --remove-orphans
	docker rmi $(DOCKER_IMAGES)
endif

test:
	mvn test

.PHONY: up up-local down test
