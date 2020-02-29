.PHONY: watch server test karma

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
