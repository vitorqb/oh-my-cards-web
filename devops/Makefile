.PHONY: images/prod build/prod artifacts/prod

# A regexp with files from the source (./..) that should be ignored for builds.
IGNORE_BUILD_FILES_REG ?= ^(devops|\.|\.\.|node_modules|\.shadow-cljs)$

# The docker command to use
DOCKER ?= sudo docker

# The build directory to use
BUILD_DIR ?= ./build

# A nice msg function
msg = @echo -e "\n==>" $(1) "\n"

# Retrieves the current version
define version
$(shell cd $(BUILD_DIR) && git describe --tags | tr -d '\n')
endef

# Retrieves the path to the generated production artifact.
define target_prod
$(shell echo "target/ohmycards-web-$(version).tar.gz")
endef

# Image tag to user for docker in prod
define imgtag_prod
$(shell echo "vitorqb23/ohmycards-web:$(version)")
endef

# Cleans the build directory
clean:
	rm -rf $(BUILD_DIR) ./target ./context
	mkdir -p $(BUILD_DIR) ./target ./context

# Bring all needed files from ../ to here.
build/prod: clean
	ls -a .. | grep -vP '$(IGNORE_BUILD_FILES_REG)' | xargs -I {} cp -rv ../{} $(BUILD_DIR)/{}
	$(MAKE) -C $(BUILD_DIR) clean

# Generates an .tar.gz artifact for production.
artifacts/prod: build/prod
	$(call msg,"Preparing to generate artifact $(target_prod) for version $(version)")

	$(call msg,"Compiling scss and js...")
	$(MAKE) -C $(BUILD_DIR) ci scss/once release/app

	$(call msg,"Creating .tar.gz file...")
	tar --dereference -vzcf $(target_prod) -C $(BUILD_DIR)/public css js vendor index.html

# Creates an image ready for deploy in a production environment.
images/prod: artifacts/prod
	$(call msg,"Preparing the docker image for production: $(imgtag_prod)")
	cp -v ./images/prod/* ./context/
	cp -v '$(target_prod)' ./context/ohmycards-web.tar.gz
	$(DOCKER) build ./context -t '$(imgtag_prod)'
