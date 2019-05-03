
VERSION := $(shell git describe --tags --always --dirty="-dev")

all: build

.PHONY: build upload
version:
	echo $(VERSION)
mvn-version:
	mvn versions:set -DnewVersion=$(VERSION)
build: mvn-version
	mvn compile

install: mvn-version
	mvn install
