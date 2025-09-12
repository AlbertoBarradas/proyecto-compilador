package com.compiler.lexer.nfa;

/**
 * Represents a Non-deterministic Finite Automaton (NFA) with a start and end state.
 * <p>
 * An NFA is used in lexical analysis to model regular expressions and pattern matching.
 * This class encapsulates the start and end states of the automaton.
 */
public class NFA {
    /**
     * The initial (start) state of the NFA.
     */
    public final State startState;

    /**
     * The final (accepting) state of the NFA.
     */
    public final State endState;

    public State accept;

    public Object start;

    /**
     * Constructs a new NFA with the given start and end states.
     *
     * @param start The initial state.
     * @param end   The final (accepting) state.
     */
    public NFA(State start, State end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Start and end states cannot be null");
        }
        this.startState = start;
        this.endState = end;
        this.endState.isFinal = true; // mark end state as accepting
    }

    /**
     * Returns the initial (start) state of the NFA.
     *
     * @return the start state
     */
    public State getStartState() {
        return this.startState;
    }
}
