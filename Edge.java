/***********************************************************
 * PDA Simulator - Edge Class
 * Matthew Hagen
 *
 * This class encodes a single edge of the PDA as an object
 ***********************************************************/

import java.util.Stack;

public class Edge {
    private int startState;                 //instance variables for the edge
    private int endState;
    private char transitionChar;
    private String pop;
    private String push;

    //constructor for edge
    public Edge(int start, int end, char ch, String pop, String push) {
        this.startState = start;
        this.endState = end;
        this.transitionChar = ch;
        this.pop = pop;
        this.push = push;
    }

    //various getters/setters for edges.

    public int getStartState() {return startState;}

    public int getEndState() {return endState;}

    public char getTransitionChar() {return transitionChar;}

    public String getPop() {return pop;}


    /*******************************************
     * pushChars
     *
     * this method pushes characters to the PDA stack in correct order.
     * @param s the stack being pushed to
     */
    public void pushChars(Stack<Character> s) {
        if (!push.equals("{e}")) {  //if push does not equal {e}, characters need to get pushed
            char[] pushArray = push.toCharArray();
            for (int i = pushArray.length - 1; i >= 0; i--) {  //read the push string backwards to push all the chars correctly
                s.push(pushArray[i]);
            }
        }
    }
}
