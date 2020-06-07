./gradlew :client:clean :client:shadowJar

cd client/build/libs

java -jar -XstartOnFirstThread client-1.0-SNAPSHOT-all.jar

exit 1
