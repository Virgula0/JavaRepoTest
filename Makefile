POM_PATH = com.example.progetto.angelo.rosa.test/pom.xml

test:
	mvn clean verify -f $(POM_PATH)
.PHONY: test

ci:
	@if [ -n "$(ARGS)" ]; then \
		echo "Running Maven with additional arguments: $(ARGS)"; \
		mvn verify -f $(POM_PATH) $(ARGS); \
	else \
		echo "Running Maven without additional arguments"; \
		mvn verify -f $(POM_PATH); \
	fi
.PHONY: ci
