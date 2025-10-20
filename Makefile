POM_PATH = com.example.progetto.angelo.rosa.test/pom.xml

junit-test:
	mvn clean test -f $(POM_PATH)
.PHONY: junit-test

integration-test:
	mvn clean verify -Pintegration-tests -f $(POM_PATH)
.PHONY: integration-test

run-pit:
	mvn clean test org.pitest:pitest-maven:mutationCoverage -f $(POM_PATH)
.PHONY: run-pit

package:
	mvn package -f $(POM_PATH)
.PHONY: package

docker-build:
	docker compose up --build 
.PHONY: docker-build

docker-stop:
	docker compose down -v 
.PHONY: docker-stop

compile-and-setup: package docker-build
test: integration-test run-pit