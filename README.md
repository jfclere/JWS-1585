mvn install -DskipTests

and install the war in the tomcat you want to test.

mvn install 

that tests the tomcat on localhost:8009

To run just the test:

mvn -Dtest=org.apache.coyote.ajp.TestJWS1585 test
