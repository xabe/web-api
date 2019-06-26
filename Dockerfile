FROM chabir/web-api-builder:latest AS builder
ARG framework_select
ENV FRAMEWORK=$framework_select
WORKDIR /build

RUN mvn install -pl web-api-${FRAMEWORK} --also-make -e -B

RUN echo "done build!"

FROM openjdk:12-jdk-alpine
ARG framework_select
ENV FRAMEWORK=$framework_select
EXPOSE 8008
WORKDIR /home/app

# Copy the binary built in the 1st stage
COPY --from=builder /build/web-api-${FRAMEWORK}/target/${FRAMEWORK}.jar ./
COPY --from=builder /build/web-api-${FRAMEWORK}/target/libs ./libs

RUN mkdir -p /home/app/logs

ENV HOME_APP="/home/app"
ENV JAVA_OPTS=" -Xmx64M \
                -Xms64M \
                -Xss512K \
                -XX:MetaspaceSize=64M \
                -XX:MaxMetaspaceSize=64M \
                -XX:CompressedClassSpaceSize=64M \
                -XX:+UseParallelGC \
                -XX:+HeapDumpOnOutOfMemoryError \
                -XX:HeapDumpPath=${HOME_APP}/dump.hprof \
                -Xrunjdwp:server=y,transport=dt_socket,address=2005,suspend=n \
                -Djava.net.preferIPv4Stack=true \
                -Xdebug \
                -Xlog:gc* \
                -verbose:gc \
                -Xlog:gc:${HOME_APP}/logs/GC.log \
                -Dorg.jboss.logging.provider=slf4j \
                -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
                -DAsyncLogger.RingBufferSize=128 \
                -Dorg.apache.logging.log4j.simplelog.StatusLogger.level=DEBUG \
                -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager \
                -Dapp.log.path=${HOME_APP}/logs \
                -Dapp.log.name=${FRAMEWORK}"


CMD [ "sh", "-c", "exec java ${JAVA_OPTS} -jar ${HOME_APP}/${FRAMEWORK}.jar" ]




