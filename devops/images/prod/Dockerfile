FROM nginx:1.17-alpine

# Dependencies
RUN apk update && apk add --no-cache tar

# Prepares the work directory
RUN mkdir /files
WORKDIR /files

# Copies the nginx config
COPY nginx.config /etc/nginx/nginx.conf

# Copies the artifact and untars it
COPY ohmycards-web.tar.gz ohmycards-web.tar.gz
RUN tar -vzxf ohmycards-web.tar.gz && rm -rf ohmycards-web.tar.gz

EXPOSE 8000
