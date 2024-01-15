.PHONY: init
init:
	@echo "###### RAP-Application Initialize ######"
	@echo "mysql & redis docker up"
	@docker-compose -p rateAllThings -f src/main/resources/db/container/database/docker-compose.yaml up -d

.PHONY: gen
gen:
	@./gradlew compileJava

.PHONY: clean
	@./gradlew clean

.PHONY: compile
compile:
	@./gradlew compileJava

.PHONY: run
run: compile
	@./gradlew bootRun

clean:
	@./gradlew clean

.PHONY: test
test:
	@./gradlew test

build.application:
	@./gradlew build -x test

.PHONY: build
build: build.docker

