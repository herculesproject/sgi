#./mvnw clean compile test integration-test verify -Dskip.unit.tests=false -Dskip.integration.tests=false -Punit-test -Dmaven.javadoc.skip=true -Duser.timezone=UTC > test.txt

# exit when any command fails
set -e

# keep track of the last executed command
trap 'last_command=$current_command; current_command=$BASH_COMMAND' DEBUG

# echo an error message before exiting
trap 'echo "\"${last_command}\" command filed with exit code $?."' EXIT

./mvnw clean verify -Dmaven.javadoc.skip=true -Ptest > maven-test.log
if [ -f "target/site/jacoco/jacoco.csv" ]; then  
  awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print "Jacoco Coverage Report Results"; print "Element\t\tMissed Instructions\tCov"; print "Total\t\t" covered, "of", instructions "\t\t" 100*covered/instructions "%" }' CONVFMT="%.2f" target/site/jacoco/jacoco.csv
fi
