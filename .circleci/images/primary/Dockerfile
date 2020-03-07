FROM circleci/clojure

# Installs npm
RUN export TMPFILE=$(mktemp -d) &&\
        mkdir -p $TMPFILE &&\
        cd $TMPFILE &&\
        curl -sL https://deb.nodesource.com/setup_12.x | sudo bash - && \
        sudo apt-get install -y nodejs &&\
        cd && rm -rf $TMPFILE

# Installs chrome
RUN export TMPFILE=$(mktemp -d) &&\
        wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb &&\
        sudo apt install -y ./google-chrome*.deb &&\
        cd && rm -rf $TMPFILE
