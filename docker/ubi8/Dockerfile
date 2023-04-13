FROM quay.io/opsmxpublic/ubifips:8.7
MAINTAINER sig-platform@spinnaker.io
COPY kayenta-web/build/install/kayenta /opt/kayenta
RUN yum -y install java-17-openjdk-devel wget vim  net-tools curl nettle
RUN yum -y update
RUN adduser spinnaker
RUN mkdir -p /opt/kayenta/plugins
RUN chmod -R 777 /opt/kayenta/plugins/
RUN chown -R spinnaker:spinnaker /opt/
USER spinnaker
CMD ["/opt/kayenta/bin/kayenta"]
