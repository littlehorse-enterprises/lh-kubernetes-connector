FROM amazoncorretto:21
RUN mkdir /lh
COPY ./docker-entrypoint.sh /lh
COPY ./build/libs/agent-worker-all.jar /lh
ENTRYPOINT ["/lh/docker-entrypoint.sh"]
