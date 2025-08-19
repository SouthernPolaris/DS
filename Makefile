# Makefile for Assignment 1

MVN = mvn
SRC_DIR = src/main/java
TARGET_DIR = target
SERVER_CLASS = CalculatorServer
CLIENT_CLASS = CalculatorClient

JAVAC_SRC = $(shell find src/main/java -name "*.java")
JAVAC_OUT = target/classes

all: compile

compile:
	mkdir -p $(JAVAC_OUT)
	javac -d $(JAVAC_OUT) $(JAVAC_SRC)

server: compile
	java -cp $(TARGET_DIR)/classes $(SERVER_CLASS)

client: compile
	java -cp $(TARGET_DIR)/classes $(CLIENT_CLASS)

test:
	$(MVN) test

clean:
	$(MVN) clean