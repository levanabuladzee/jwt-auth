FROM airhacks/glassfish
COPY ./target/security-train.war ${DEPLOYMENT_DIR}
