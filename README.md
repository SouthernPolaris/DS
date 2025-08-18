# Distributed Systems Assignment 1

### Java RMI Calculator

This program uses Java RMI to implement a Distributed Calculator

The calculator is implemented through unique stacks for each client connecting to the server. 

#### Features:
- A central server that hosts the calculator operations
- A client implementation that can remotely access the calculator methods through the RMI stub
- A calculator interface
- A calculator implementation
- Test files for tests with both single and multiple clients
- Stack Operations on the Calculator, done through typing them through client terminals:
    - `push <value>`: Push an integer onto stack
    - `pop`: Pop top of stack
    - `delayPop <mills>`: Pop top of stack after a delay in milliseconds
    - `op <min/max/lcm/gcd>`: Perform the respective supported operation onto the stack (applies to all elements on stack currently; calculates respective result, clears stack and appends res onto the top)
    - `help`: Display available commands
    - `exit`: Exit client  


# How to Run

### Prerequisites
1. Have JDK 17 installed
2. Install Maven
3. Both of the above can be done on a linux distro using the `apt` package manager through `sudo apt install openjdk-17-jdk maven`. Or, if not using apt, please search how to install these packages using that distro's package manager
4. Check that you have successfully installed them by running `java -version` and `mvn -version`


----

## Running

### Makefile Building
1. Build the files
```bash
make all
```
2. Run the server
```bash
make server
```
3. Run the client in a separate terminal (as many as needed in individual terminals)
```bash
make client
```

### Building Without Makefile (If Makefile doesn't work)
1. Build the files
```bash
mvn clean compile
```
2. Run the server
```bash
java -cp target/classes CalculatorServer
```
3. Run the client in a separate terminal (as many as needed in individual terminals)
```bash
java -cp target/classes CalculatorClient
```


## How to Test
### Using Makefile
1. Compile the code
```bash
make all
```
2. Run the tests
```bash
make test
```

### Without Makefile
1. Compile the code
```bash
mvn clean compile
```
2. Run the tests
```bash
mvn test
```

### Additional Info
The clients and servers output useful print debugging, which acts as action confirmation to both the server and client.

### BONUS MARK
Each Client has its own individual stack, which is identified through a <UUID, Stack> hashmap;
