# Makefile for Assignment 1

MVN = mvn
SRC_DIR = src/main/java
TARGET_DIR = target
SERVER_CLASS = CalculatorServer
CLIENT_CLASS = CalculatorClient

all: compile

compile:
	$(MVN) clean compile

server: compile
	java -cp $(TARGET_DIR)/classes $(SERVER_CLASS)

client: compile
	java -cp $(TARGET_DIR)/classes $(CLIENT_CLASS)

test:
	$(MVN) test

clean:
	$(MVN) clean