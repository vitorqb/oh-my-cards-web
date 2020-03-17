FROM nginx:1.17-alpine

# Dependencies
RUN apk update && apk add --no-cache tar

# Copies the nginx config
COPY nginx.config /etc/nginx/nginx.conf

# Prepares the work directory and user
RUN addgroup -S ohmycardsweb && adduser -S ohmycardsweb -G ohmycardsweb
USER ohmycardsweb:ohmycardsweb
WORKDIR /home/ohmycardsweb

# Copies the artifact and untars it
COPY --chown=ohmycardsweb ohmycards-web.tar.gz ohmycards-web.tar.gz
RUN tar -vzxf ohmycards-web.tar.gz && rm -rf ohmycards-web.tar.gz

EXPOSE 8000
