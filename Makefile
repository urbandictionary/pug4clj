test-jar:
	lein uberjar
	java -jar target/pug4clj-0.1.0-SNAPSHOT-standalone.jar test.pug
	java -jar target/pug4clj-0.1.0-SNAPSHOT-standalone.jar complex/index.pug