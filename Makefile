.PHONY: watch server test karma scss rev-proxy

# Docker command to use
DOCKER ?= docker

# The .env file used to the run backend
BACKEND_ENV_FILE ?= $(shell realpath ./backend.env)

# The db file used to run the backend
BACKEND_DB_FILE ?= $(shell realpath ./backend.sqlite)

# The name for the be docker container
BACKEND_DOCKER_NAME ?= ohmycards-web--backend

# Starts shadow-cljs server.
server:
	npx shadow-cljs server

# Watch all builds
watch:
	npx shadow-cljs watch app test

# Test using karma
test: karma
karma:
	npx karma start

# Watch-compile scss
scss:
	npx node-sass src/scss/site.scss public/css/site.css
	npx node-sass --watch src/scss/site.scss public/css/site.css

# Launches a backend docker image. Assumes `ohmycards-dev` image is accessible.
run-backend:
	$(DOCKER) kill $(BACKEND_DOCKER_NAME) || :
	touch $(BACKEND_ENV_FILE)
	touch $(BACKEND_DB_FILE)
	$(DOCKER) run --rm -ti\
	  --name '$(BACKEND_DOCKER_NAME)'\
	  --env-file '$(BACKEND_ENV_FILE)'\
	  -v '$(BACKEND_DB_FILE):/home/ohmycards/dev.sqlite'\
	  -p '9002:9000'\
	  ohmycards/ohmycards\
	  -Dplay.evolutions.db.default.autoApply=true

rev-proxy:
        # A reverse proxy, usefull for development with BE.
        # Assumes BE is at 9002 and FE at 9001.
	devd -p 9000 '/api/=http://127.0.0.1:9002/' '/=http://127.0.0.1:9001/'

# Refreshes the test-ns-requires file containing all mftickets-web tests.
test-ns-requires:
	$(eval tmpfile=$(shell mktemp))
	rm -rf ${tmpfile} && touch ${tmpfile}
	echo "(ns test-ns-requires" >>"${tmpfile}"
	echo '  "Namespace used to require all tests namespaces.' >>"${tmpfile}"
	echo '  THIS FILE IS CREATED AUTOMATICALLY AND SHOULD NOT BE EDITED.' >>"${tmpfile}"
	echo '  See make test-ns-requires"' >>"${tmpfile}"
	echo '  (:require' >>"${tmpfile}"
	ag --no-group --no-filename -o 'ohmycards.*-test' test | sed -n '/./ p' | sed -E 's/(.*)/[\1]/g' | sort >>"${tmpfile}"
	echo '))' >>"${tmpfile}"
	cp ${tmpfile} src/test_ns_requires.cljs
	rm -rf ${tmpfile}
