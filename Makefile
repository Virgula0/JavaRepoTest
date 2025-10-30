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

run-all:
	mvn clean verify org.pitest:pitest-maven:mutationCoverage -Pintegration-tests -f $(POM_PATH)
.PHONY: run-all

package:
	mvn clean package -f $(POM_PATH)
.PHONY: package

docker-build:
	xhost +local:docker
	docker compose up --build 
.PHONY: docker-build

docker-stop:
	xhost -local:docker
	docker compose down -v 
.PHONY: docker-stop

compile-and-setup: package docker-build
test: run-all
