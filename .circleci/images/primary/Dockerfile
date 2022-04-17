FROM openjdk:11-buster

ARG BLACKBOX_VERSION=2.0.0
ARG BLACKBOX_TAR_URL=https://github.com/StackExchange/blackbox/archive/refs/tags/v${BLACKBOX_VERSION}.tar.gz

RUN apt-get update
RUn apt-get install -y build-essential

# Installs npm
RUN export TMPFILE=$(mktemp -d) &&\
        mkdir -p $TMPFILE &&\
        cd $TMPFILE &&\
        curl -sL https://deb.nodesource.com/setup_14.x | bash - && \
        apt-get install -y nodejs &&\
        cd && rm -rf $TMPFILE

# Installs chrome
RUN export TMPFILE=$(mktemp -d) &&\
        wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb &&\
        apt install -y ./google-chrome*.deb &&\
        cd && rm -rf $TMPFILE

# Install blackbox
RUN export TMPFILE="$(mktemp -d)" &&\
    mkdir -p "$TMPFILE" &&\
    curl -sL ${BLACKBOX_TAR_URL} -o ./blackbox.tar.gz &&\
    tar -vxf ./blackbox.tar.gz &&\
    cd ./blackbox* &&\
    make copy-install &&\
    which blackbox_list_admins &&\
    rm -rf "$TMPFILE"

# Install docker
RUN apt-get -y install ca-certificates curl gnupg lsb-release
RUN  curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
RUN echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian \
  $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null
RUN apt-get -y update
RUN apt-get -y install docker-ce docker-ce-cli containerd.io

# Install sbt
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update
RUN apt-get install sbt

# Sets user up
RUN useradd -ms /bin/bash circleci
USER circleci

RUN mkdir /tmp/foo-project && cd /tmp/foo-project && sbt version && rm -rf /tmp/foo-project
