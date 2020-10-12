FROM alpine:3.11.6

RUN apk update && \
    apk add openjdk8 maven jq python3 bash bash-doc bash-completion git gnupg&& \
    pip3 install yq

RUN mkdir -p /work/secrets

WORKDIR work

ADD files/.bashrc /root

ADD files/deploy.sh /work
ADD files/settings.xml /work

ENTRYPOINT ["/work/deploy.sh"]
