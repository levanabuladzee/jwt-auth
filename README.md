# Build
mvn clean package && docker build -t org.example/security-train .

# RUN

docker rm -f security-train || true && docker run -d -p 8080:8080 -p 4848:4848 --name security-train org.example/security-train 