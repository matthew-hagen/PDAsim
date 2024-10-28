/***********************************************************
 * PDA Simulator - State
 * Matthew Hagen
 *
 * This class encodes a state of the PDA as an object
 ***********************************************************/
public class State {
    private final int stateNum;          //instance variables for the state
    private boolean accepting;

    //constructor for State...
    public State(int num, boolean acc) {
        this.stateNum = num;
        this.accepting = acc;
    }

    //sets the accepting boolean to true
    public void setAccepting(){this.accepting = true;}

    //returns whether the state is accepting or not
    public boolean isAccepting() {return accepting; }
}
