FROM openjdk:8-jre-alpine

RUN apk add --no-cache tzdata
ENV TZ Europe/Rome

ADD target/gup-gestione-pagopa-1.0.15.alfa.jar gup/gup-helper-pagopa-attiva-rpt/gup-gestione-pagopa-1.0.15.alfa.jar

CMD java $JAVA_OPTS $JAVA_ADDITIONAL_OPTS -Djavax.net.ssl.trustStore=/mnt/pod/cacerts -Djavax.net.ssl.trustStorePassword=changeit -jar gup/gup-helper-pagopa-attiva-rpt/gup-gestione-pagopa-1.0.15.alfa.jar
