import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Stack;
import java.util.Vector;

/***********************************************************
 * PDA Simulator - Main
 * Matthew Hagen
 *
 * This program reads data from a formatted .txt file to construct a pushdown automata.
 * The user is then prompted to enter strings, which will be computed and either accepted/rejected
 * by the PDA.
 ***********************************************************/

public class Main{
    static Vector<State> states = new Vector<>();             //vector that will hold all the states
    static Vector<Edge> edges = new Vector<>();               //vector that will hold all the edges
    static Vector<Character> inputAlphabet = new Vector<>();  //vector that holds the valid input alphabet
    static Vector<Character> stackAlphabet = new Vector<>();  //vector that holds the valid stack alphabet


    static int currentState;                                  //the code is written such that the states are enumerable
                                                              //currentState keeps track of the current state the PDA is in
    public static void main (String[] args) throws FileNotFoundException {

        ClassLoader cl = ClassLoader.getSystemClassLoader();
        File file = new File(cl.getResource("PDA.txt").getFile());  //open up the data file

        Scanner fileScanner = new Scanner(file);                               //create scanner for the file
        initStates(fileScanner);                                               //initialize states
        initAlphabet(fileScanner, inputAlphabet);                              //initialize input alphabet
        initAlphabet(fileScanner, stackAlphabet);                              //initialize stack alphabet

        while (fileScanner.hasNextLine()) {                                    //initialize all edges
            String line = fileScanner.nextLine();                              //there can be variable # of edge rules
            initEdges(line);                                                   //so this is done in a loop to handle them all
        }

        Scanner sc = new Scanner(System.in);                                   //input scanner
        System.out.print("Please enter a string to evaluate: ");               //prompts the user to enter a string to eval
        String input = sc.nextLine();

        Stack<Character> stack = new Stack<>();                  //initializing the stack for the PDA
        stack.push('L');                                    //pushing 'L' to the bottom, is the empty stack signifier

        boolean invalid;
        while (!input.equalsIgnoreCase("quit")) {    //will run until the user enters "quit"
            currentState = 0;                                    //start at start state
            invalid = false;                                     //boolean flag for invalid input, will break loop
            System.out.println(">>>Computation...");

            /**************************
             * COMPUTATIONAL LOOP
             **************************/

            while (!input.equals("{e}")) {                       //while there is still more input to be parsed

                System.out.print(currentState + ", " + input + "/" + stack.peek());
                System.out.print(" -> ");                        //formatting for output. showing where we start

                invalid = !traverseEdge(input, stack);           //calls traverse edge and checks for valid move
                input = input.substring(1);            //remove the front character from the input string
                if (input.isEmpty())                             //if that makes it empty, format it with {e}
                    input = "{e}";

                if (invalid)
                    break;                                       //if there was no valid move, break out of the loop
                else {                                           //else, print out updated state/string/stack
                    System.out.print(currentState + ", " + input + "/");
                    printStack(stack);
                    System.out.println("");
                }

            }

            /*********************
             * COMPUTATION FINISHED. if we broke out of the loop in an accepting state, didn't get flagged for invalid
             * moves, and the stack is empty, that means it worked.
             *********************/

            if (states.get(currentState).isAccepting() && !invalid && stack.peek() == 'L')
                System.out.println("ACCEPTED");
            else
                System.out.println("REJECTED");

            stack.clear();                                           //clear the stack for the next string
            stack.push('L');

            System.out.print("Please enter a string to evaluate: "); //prompt user for next input
            input = sc.nextLine();
        }


        System.out.println("Goodbye <3");                            //quit the program
        System.exit(0);

    }

    /*************************************************************************************
     * initStates
     *
     * used during initialization of FSM to create state objects
     * @param scanner file scanner passed in from main method
     **************************************************************************************/

    public static void initStates(Scanner scanner){
        int numOfStates = scanner.nextInt();             //how many states need made
        for (int i = 0; i < numOfStates; i++) {
            State state = new State(i, false);      //make new state obj, defaults to non-accepting
            states.add(state);                           //add it to the vector
        }

        String acceptingLine = scanner.nextLine();
        acceptingLine = scanner.nextLine();        //go to next line and find all the accepting states

        Scanner stringScanner = new Scanner(acceptingLine);
        int accept = stringScanner.nextInt();      //want to parse indiv digits from the line of accepting states

        while (stringScanner.hasNextInt()) {
            states.get(accept).setAccepting();
            accept = stringScanner.nextInt();     //make sure to set all the accepting states accordingly
        }

        states.get(accept).setAccepting();        //catch the last accepting state after broken out of loop
    }

    /******************************************************************************************************
     * initAlphabet
     *
     * now used in two instances, to initialize both the input alphabet and stack alphabet
     * @param scanner file scanner passed in from main
     * @param alphabet the alphabet that is being initialized
     ******************************************************************************************************/
    public static void initAlphabet(Scanner scanner, Vector<Character> alphabet){
        String acceptingLine = scanner.nextLine();

        char[] alph = acceptingLine.toCharArray();

        for (char ch: alph) {
            if(!Character.isSpace(ch))         //parsing through string for all the alphabet characters
                alphabet.add(ch);              //adding it to the appropriate vector
        }
    }

    /**
     * initEdges
     *
     * used during initialization to create edge objects. this gets a little funky
     * due to formatting, but each edge should have a start and end state, a transition character,
     * and some character(s) getting pushed or popped off the stack. this method is mostly just doing
     * string manipulation to break the line from the .txt file into those pieces
     * @param line
     */
    public static void initEdges(String line) {
        Scanner edgeScanner = new Scanner(line);
        int startState;
        int endState;
        char transitionChar;
        String pop;
        String push;

        try { //there HAS to be a better way to do this lol
            String start = edgeScanner.next();
            startState = Integer.parseInt(start);           //the start state for the edge

            String input = edgeScanner.next();              //will have a token like a/a
            String[] parts = input.split("/");        //split along delimiter "/"
            transitionChar = parts[0].charAt(0);            //the first piece is the transition character
            pop = parts[1];                                 //the second piece is what's getting popped
                                                            //note that this is being handled like a string since {e}
                                                            //is a possibility. in practice only one char gets popped
            String arrow = edgeScanner.next();              //don't care about the arrow lol

            String out = edgeScanner.next();                //another token, this time int/charcharchar....
            parts = out.split("/");                   //split it
            endState = Integer.parseInt(parts[0]);          //the first part (the int) is the end state
            push = parts[1];                                //the char(s) getting pushed are the second
        }
        catch (NoSuchElementException e){                   //can also catch formatting errors in the .txt file
            throw new NoSuchElementException();
        }

        if (!push.equals("{e}")) {                          //also want to make sure that all the stack pushes are valid
            for (char c : push.toCharArray()) {             //make sure that each char getting pushed is in the alphabet
                if (!stackAlphabet.contains(c)) {
                    System.out.println("error in txt file. invalid character: " + c);
                    System.exit(1);
                }
            }
        }

        Edge edge = new Edge(startState, endState, transitionChar, pop, push); //finally make the edge obj
        edges.add(edge);                                                       //add it to edge vector
    }

    /************************************************************************
     * traverseEdge
     *
     * used in computation to find an edge to take, performs the transition
     * @param string the current input string
     * @param stack the PDA stack
     * @return true if successful, false for invalid input or dead state transition
     ************************************************************************/
    public static boolean traverseEdge(String string, Stack<Character> stack) {
        Character ch = string.charAt(0);                //need the input character to work with
        if (!inputAlphabet.contains(ch)) {              //if it's not in the alphabet...that's wrong
            System.out.println("INVALID INPUT");
            return false;
        }

        /**this is messy but to use an edge the following conditions need to be true:
         *
         * the edge has to be for the right start state
         * the edge has to be for the right input character
         * the character at the top of the stack needs to match what is going to get popped OR
         * the edge does not pop anything from the stack
         *
         */
        for (int i = 0; i < edges.size(); i++) {
            if (currentState == edges.get(i).getStartState() &&
            ch == edges.get(i).getTransitionChar() &&
                    (stack.peek() == edges.get(i).getPop().charAt(0) ||
                    edges.get(i).getPop().equals("{e}"))) {

                if (!edges.get(i).getPop().equals("{e}"))      //pop from stack if edge calls for it
                    stack.pop();
                edges.get(i).pushChars(stack);                 //call the pushChars() func to push to stack
                currentState = edges.get(i).getEndState();     //update currentState
                return true;
            }
        }

        return false;                                          //no edges found, return false
    }

    /***************************************************
     * printStack
     *
     * this is just used for formatting output to console.
     * prints the characters in the stack top-down with no spaces or delimiting chars
     * @param s the stack to be printed
     ****************************************************/
    public static void printStack(Stack<Character> s) {
        for (int i = s.size() - 1; i >= 0; i--) {
            System.out.print(s.get(i));
        }
    }
}