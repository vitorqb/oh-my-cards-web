.PHONY: watch server test karma scss

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
