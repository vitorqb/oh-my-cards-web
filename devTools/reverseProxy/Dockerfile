FROM golang:1.16

RUN go get github.com/cortesi/devd/cmd/devd

ENTRYPOINT ["devd"]
CMD ["-p","9000","/api/=http://127.0.0.1:9002/","/=http://127.0.0.1:9001/"]
