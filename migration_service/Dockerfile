# Stage: build application JAR
FROM maven:3.9.12 AS builder
WORKDIR /application
COPY migration_service/ .
RUN --mount=type=cache,target=/root/.m2 mvn clean install -Pmigration_service -Dmaven.test.skip

# Stage: extract Spring Boot application layers
FROM amazoncorretto:21-alpine AS layers
WORKDIR /application
COPY --from=builder /application/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --destination extracted

# Stage: final runtime image
FROM amazoncorretto:21-alpine

VOLUME /tmp

# Configure non-root user
RUN adduser -S spring-user
USER spring-user

WORKDIR /application

# Copy Spring Boot application layers
COPY --from=layers /application/extracted/dependencies/ ./
COPY --from=layers /application/extracted/spring-boot-loader/ ./
COPY --from=layers /application/extracted/snapshot-dependencies/ ./
COPY --from=layers /application/extracted/application/ ./

# Generate CDS archive
RUN java -XX:ArchiveClassesAtExit=app.jsa -Dspring.context.exit=onRefresh -jar app.jar || true

# JVM memory options
ENV JAVA_RESERVED_CODE_CACHE_SIZE="240M"
ENV JAVA_MAX_DIRECT_MEMORY_SIZE="10M"
ENV JAVA_MAX_METASPACE_SIZE="179M"
ENV JAVA_XSS="1M"
ENV JAVA_XMX="345M"

# JVM options
ENV JAVA_CDS_OPTS="-XX:SharedArchiveFile=app.jsa -Xlog:class+load:file=/tmp/classload.log"
ENV JAVA_ERROR_FILE_OPTS="-XX:ErrorFile=/tmp/java_error.log"
ENV JAVA_HEAP_DUMP_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp"
ENV JAVA_ON_OUT_OF_MEMORY_OPTS="-XX:+ExitOnOutOfMemoryError"
ENV JAVA_NATIVE_MEMORY_TRACKING_OPTS="-XX:NativeMemoryTracking=summary -XX:+UnlockDiagnosticVMOptions -XX:+PrintNMTStatistics"

ENTRYPOINT exec java \
    -XX:ReservedCodeCacheSize=$JAVA_RESERVED_CODE_CACHE_SIZE \
    -XX:MaxDirectMemorySize=$JAVA_MAX_DIRECT_MEMORY_SIZE \
    -XX:MaxMetaspaceSize=$JAVA_MAX_METASPACE_SIZE \
    -Xss$JAVA_XSS \
    -Xmx$JAVA_XMX \
    $JAVA_HEAP_DUMP_OPTS \
    $JAVA_ON_OUT_OF_MEMORY_OPTS \
    $JAVA_ERROR_FILE_OPTS \
    $JAVA_NATIVE_MEMORY_TRACKING_OPTS \
    $JAVA_CDS_OPTS \
    -jar app.jar
