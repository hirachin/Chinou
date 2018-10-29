#Makefile
ALLSOURCE = ./src/*.java

.PHONY: all
all: comp
comp:
	@javac -encoding UTF-8 -d bin ${ALLSOURCE}

.PHONY: clean
clean:
	@rm -rf ./bin/*.class