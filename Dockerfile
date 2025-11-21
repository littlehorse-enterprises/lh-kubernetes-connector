FROM amazoncorretto:25

RUN yum install findutils -y && \
    yum clean all && \
    rm -rf /var/cache/yum

# entrypoint
COPY docker-entrypoint.sh /connector/docker-entrypoint.sh

# jar app
COPY build/quarkus-app/lib/ /connector/lib/
COPY build/quarkus-app/*.jar /connector/
COPY build/quarkus-app/app/ /connector/app/
COPY build/quarkus-app/quarkus/ /connector/quarkus/

# native app
COPY build/quarkus-run /connector/
RUN chmod +x /connector/quarkus-run

ENTRYPOINT ["/connector/docker-entrypoint.sh"]
CMD ["jvm"]
