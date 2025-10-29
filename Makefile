POM_PATH = com.example.progetto.angelo.rosa.test/pom.xml

junit:
	mvn clean verify -f $(POM_PATH)
.PHONY: junit

integration-test:
	mvn clean verify -Pintegration-tests -f $(POM_PATH)
.PHONY: integration-test

run-pit:
	mvn clean verify org.pitest:pitest-maven:mutationCoverage -f $(POM_PATH)
.PHONY: run-pit

package:
	mvn clean package -f $(POM_PATH)
.PHONY: package

docker-build:
	xhost +local:docker
	docker compose up --build 
.PHONY: docker-build

docker-stop:
	docker compose down -v 
.PHONY: docker-stop

compile-and-setup: package docker-build
test: junit integration-test run-pit
