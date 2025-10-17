POM_PATH = com.example.progetto.angelo.rosa.test/pom.xml

test:
	mvn clean verify -f $(POM_PATH)
.PHONY: test

run-pit:
	mvn test org.pitest:pitest-maven:mutationCoverage -f $(POM_PATH)
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