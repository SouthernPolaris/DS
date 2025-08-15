# Distributed Systems Assignment 1

### Java RMI Calculator

This program uses Java RMI to implement a Distributed Calculator

It has: 
- A central server that hosts the calculator operations
- A client implementation that can remotely access the calculator methods through the RMI stub
- A calculator interface
- A calculator implementation
- A Test file for tests with both single and multiple clients

The calculator is implemented through a stack. 

## How to Run
1. Run `CalculatorServer.java`
2. Run `CalculatorClient.java`
3. Follow the Prompts on the Screen to Use The RMI Stub Operations
4. If you want to run multiple clients simultaneously, run another `CalculatorClient.java` in a separate terminal

### How to Test
1. Run `TestCalcClient.java`
- This runs all tests inside the file, for both single and multiple clients.
- The test file tests for all operations for both cases, using JUnit.

### BONUS MARK
Each Client has its own individual stack, which is identified through a <UUID, Stack> hashmap;
