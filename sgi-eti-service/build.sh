# Clean UP
rm maven-compile.log maven-unit-test.log maven-integration-test.log maven-verify.log maven-package.log maven-deploy.log
clear

############
# variables#
############
export MAVEN_CLI_OPTS="--batch-mode --errors --fail-at-end --show-version" # -U"
export MAVEN_OPTS="-Djava.awt.headless=true" # -Dmaven.repo.local=/tmp/.m2/repository"
export MAVEN_SETTINGS_FILE="../.devcontainer/config/settings.xml"
export CA_CERT_JKS_BASE64="/u3+7QAAAAIAAAABAAAAAgAJc29uYXJxdWJlAAABdi05jLIABVguNTA5AAADmDCCA5QwggJ8oAMCAQICEEjmRrvf1t23S/1MBFj0wjAwDQYJKoZIhvcNAQELBQAwUjEVMBMGCgmSJomT8ixkARkWBWxvY2FsMRkwFwYKCZImiZPyLGQBGRYJdHJlZWxvZ2ljMR4wHAYDVQQDExV0cmVlbG9naWMtVFJFRURDMDEtQ0EwHhcNMjAwNDE0MTIzNjU1WhcNNDAwNDE0MTI0NjU0WjBSMRUwEwYKCZImiZPyLGQBGRYFbG9jYWwxGTAXBgoJkiaJk/IsZAEZFgl0cmVlbG9naWMxHjAcBgNVBAMTFXRyZWVsb2dpYy1UUkVFREMwMS1DQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAM5bsDBTrV9Iq4fLbOxjeiZguN4z0k7kSPc+WEiRwagE3xNmqaAYC7B2FvIBgv1qxVTA3ACUmFlzWr3wZg7rJAFiTaGbNLbxFmBtyh0vXIdPZSzDTx80nqLUjAxNYi0jgGApqAMlWVkPuuxUGY8CM9iflvGzNrWGcjqn/vxSw8ZrXNqLviHsCoGqGoO8EHqVOkMb2nclmu5pOPsPb0adJ95dNOmpAwVvtpygVoCoCIcSSnjd2YKA+MoK7L/1k5SZm6OvaQNEbdVKnkMoolD+6kvBsyxc9kkLg2lWdB/OHWbpL5os5abs+2Uv26tK2TzlgBwrleJLPPUzSKWGJNG2FXUCAwEAAaNmMGQwEwYJKwYBBAGCNxQCBAYeBABDAEEwCwYDVR0PBAQDAgGGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFAgfyZ4XY3E8WoZa0FoTFmYaUVylMBAGCSsGAQQBgjcVAQQDAgEAMA0GCSqGSIb3DQEBCwUAA4IBAQApzzgsd2P2QIqc13uuewpHWW/lqfuwIIAcVwI61UkcegbELLdYO90Fu0bFQ+Mrdd7EaPdQ1KcZQ6TSgv+pUBv1OcFdzK9gzLZJKMljEljAqJE8DbAuETED0l+/Jc+WSaijzo5xdYTxyb2o3+hn8uCehwk9prL7oUUgrDlk9pieDs0lLYFoNJI1Mx7qhSfz90UGypDu51opBOWwgdpsfy68E8Oj+hqZJ9LRT3uB2yEoOdAm8MnFUpsEX+SFTNrQGHh0hZkonMAC3C9RWWTkwEUJa7MlkAa+ytV/nFN6VZgSBTakqfpvldCRH1R4cONBzkYZ00f8IoluPt9fpWe0RcqvtUue/PBr6+qTDKsCS8WjFnMG71U="
export CA_CERT_JKS_PASSWORD="changeit"
#export SONAR_HOST_URL="https://sonarqube.corp.treelogic.com/"
#export SONAR_TOKEN="4581ad2dc9f8a835ef085d624216ac66501cd4e0"
export SONAR_HOST_URL="http://sonar.local.computer:9000"
export SONAR_TOKEN="2ff92a3dcc9110bf36504f9d576e8f8fa23b8274"

# custom maven settings file
if [ $MAVEN_SETTINGS_FILE ]; then MAVEN_CLI_OPTS="$MAVEN_CLI_OPTS -s $MAVEN_SETTINGS_FILE"; fi

###############
#maven-compile#
###############
echo "maven-compile > maven-compile.log"
./mvnw $MAVEN_CLI_OPTS clean compile > maven-compile.log
read -p "Press any key to resume ..."

#################
#maven-unit-test#
#################
echo "maven-unit-test > maven-unit-test.log"
./mvnw $MAVEN_CLI_OPTS test -Dmaven.javadoc.skip=true -Dspring.profiles.active=test > maven-unit-test.log # -Dspring.profiles.active=test-postgres > maven-init-test.log
# parse jacoco coverage report for GitLab parsing
if [ -f "target/site/jacoco-ut/jacoco.csv" ]; then
  awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print "Jacoco Coverage Report Results"; print "Element\t\tMissed Instructions\tCov"; print "Total\t\t" covered, "of", instructions "\t\t" 100*covered/instructions "%" }' CONVFMT="%.2f" target/site/jacoco-ut/jacoco.csv
fi
read -p "Press any key to resume ..."

########################
#maven-integration-test#
########################
echo "maven-integration-test > maven-integration-test.log"
./mvnw $MAVEN_CLI_OPTS post-integration-test failsafe:verify -DskipUnitTests -Dmaven.javadoc.skip=true -Dspring.profiles.active=test > maven-integration-test.log # -Dspring.profiles.active=test-postgres > maven-integration-test.log
# parse jacoco coverage report for GitLab parsing
if [ -f "target/site/jacoco-it/jacoco.csv" ]; then
  awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print "Jacoco Coverage Report Results"; print "Element\t\tMissed Instructions\tCov"; print "Total\t\t" covered, "of", instructions "\t\t" 100*covered/instructions "%" }' CONVFMT="%.2f" target/site/jacoco-it/jacoco.csv
fi
read -p "Press any key to resume ..."

##############
#maven-verify#
##############
echo "maven-verify > maven-verify.log"
# check if CA_CERT_JKS_BASE64 variable is defined. In this case, decode CA_CERT_JKS_BASE64 which contains the Tree CA file (format jks, encode base64)
# cat treelogic_ca.pem | keytool -import -noprompt -trustcacerts -alias sonarqube -storepass <CA_CERT_JKS_PASSWORD> -keystore truststore.jks
# CA_CERT_JKS_BASE64=base64 truststore.jks
if [ $CA_CERT_JKS_BASE64 ]; then echo $CA_CERT_JKS_BASE64 | base64 --decode > /tmp/truststore.jks; export SONAR_SCANNER_OPTS="-Djavax.net.ssl.trustStore=/tmp/truststore.jks -Djavax.net.ssl.trustStorePassword=$CA_CERT_JKS_PASSWORD"; fi
# generate jacoco coverage report and inspect code with SonarQube
./mvnw $MAVEN_CLI_OPTS verify -DskipTests -Dmaven.javadoc.skip=true -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml sonar:sonar $SONAR_SCANNER_OPTS > maven-verify.log
# parse jacoco merged coverage report for GitLab parsing
if [ -f "target/site/jacoco/jacoco.csv" ]; then
  awk -F"," '{ instructions += $4 + $5; covered += $5 } END { print "Jacoco Coverage Report Results"; print "Element\t\tMissed Instructions\tCov"; print "Total\t\t" covered, "of", instructions "\t\t" 100*covered/instructions "%" }' CONVFMT="%.2f" target/site/jacoco/jacoco.csv
fi
read -p "Press any key to resume ..."

###############
#maven-package#
###############
echo "maven-package > maven-package.log"
./mvnw $MAVEN_CLI_OPTS package -DskipTests -Pprod > maven-package.log
read -p "Press any key to resume ..."

##############
#maven-deploy#
##############
echo "maven-deploy > maven-deploy.log"
./mvnw $MAVEN_CLI_OPTS install -DskipTests -Pprod > maven-deploy.log # deploy -DskipTests