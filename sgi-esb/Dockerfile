# Start with a base image containing Java runtime
FROM wso2/wso2mi:1.2.0

# Versions args
ARG ESB_COMMON_VERSION
ARG ESB_SGDOC_VERSION
ARG ESB_SGE_VERSION
ARG ESB_SGEMP_VERSION
ARG ESB_SGEPII_VERSION
ARG ESB_SGI_VERSION
ARG ESB_SGO_VERSION
ARG ESB_SGP_VERSION

# Make ports 8290, 8253, 9164 available to the world outside this container
EXPOSE 9164

# Add sgi-esb-common/sgi-auth-mediator application's jar to the container
COPY --chown=wso2carbon:wso2 sgi-esb-common/sgi-auth-mediator/target/sgi-auth-mediator-${ESB_COMMON_VERSION}.jar  ${WSO2_SERVER_HOME}/lib/

# Add sgi-esb-common application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-common/esb-composite-application/target/esb-composite-application_${ESB_COMMON_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgdoc application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgdoc/sgdoc-composite-application/target/sgdoc-composite-application_${ESB_SGDOC_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sge application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sge/sge-composite-application/target/sge-composite-application_${ESB_SGE_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgemp application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgemp/sgemp-composite-application/target/sgemp-composite-application_${ESB_SGEMP_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgepii application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgepii/sgepii-composite-application/target/sgepii-composite-application_${ESB_SGEPII_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgi application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgi/sgi-composite-application/target/sgi-composite-application_${ESB_SGI_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgo application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgo/sgo-composite-application/target/sgo-composite-application_${ESB_SGO_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Add sgi-esb-sgp application's car to the container
COPY --chown=wso2carbon:wso2 sgi-esb-sgp/sgp-composite-application/target/sgp-composite-application_${ESB_SGP_VERSION}.car ${WORKING_DIRECTORY}/base-capp/

# Copy deployment.toml with hot deployment - true
COPY --chown=wso2carbon:wso2 docker/deployment.toml ${WORKING_DIRECTORY}/wso2mi-1.2.0/conf/

# Add script to copy esb-composite before launch server
COPY --chown=wso2carbon:wso2 docker/sgi-esb-entrypoint.sh ${WORKING_DIRECTORY}

RUN chmod +x ${WORKING_DIRECTORY}/sgi-esb-entrypoint.sh

RUN chmod 777 ${WSO2_SERVER_HOME}/repository/deployment/server/carbonapps/

# Generate nginx.conf using environment variables
ENTRYPOINT ${WORKING_DIRECTORY}/sgi-esb-entrypoint.sh
