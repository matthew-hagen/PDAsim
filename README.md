# PDA Simulator

PDAsim is a small Java project that simulates a deterministic Pushdown Automata. It reads in a formatted .txt that encodes the PDA to construct it. 
After building the PDA, the user is prompted to enter strings to be computed. PDAsim will then demonstrate each edge transition that results in the entered
string either being accepted or denied by the FSM. This process repeats until the user enters 'quit', which will cause the program to terminate.

## Usage
To compile:
```bash
javac Main.java State.java Edge.java
```

## To execute:
```bash
java Main
```
*Note: This program is meant to be demonstrative, the text file PDA.txt included in the source code is hard-coded into the main method with a relative path. To change the
input, change the name of the desired file in the getResource().getFile() call in the main method.
