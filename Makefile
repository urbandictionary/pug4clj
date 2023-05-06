test-jar:
	lein uberjar
	java -jar target/pug4clj-0.0.1-standalone.jar test.pug
	java -jar target/pug4clj-0.0.1-standalone.jar complex/index.pug

deploy:
	lein deploy clojars

format:
	find . -name \*.clj | xargs zprint -w