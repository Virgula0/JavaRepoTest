POM_PATH = com.example.progetto.angelo.rosa.test/pom.xml

test:
	mvn clean verify -f $(POM_PATH)
.PHONY: test
