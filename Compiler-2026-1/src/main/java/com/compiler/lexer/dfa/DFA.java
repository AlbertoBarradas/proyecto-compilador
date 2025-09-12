package com.compiler.lexer.dfa;

import java.util.Collections;
import java.util.List;

/**
 * DFA
 * ---
 * Represents a complete Deterministic Finite Automaton (DFA).
 * Contains the start state and a list of all states in the automaton.
 */
public class DFA {
    /**
     * The starting state of the DFA.
     */
    public final DfaState startState;

    /**
     * A list of all states in the DFA.
     */
    public final List<DfaState> allStates;

    /**
     * Constructs a new DFA.
     * @param startState The starting state of the DFA.
     * @param allStates  A list of all states in the DFA.
     */
    public DFA(DfaState startState, List<DfaState> allStates) {
        if (startState == null) {
            throw new IllegalArgumentException("DFA must have a start state.");
        }
        if (allStates == null || allStates.isEmpty()) {
            throw new IllegalArgumentException("DFA must have at least one state.");
        }

        this.startState = startState;
        this.allStates = Collections.unmodifiableList(allStates);
    }

    /**
     * Returns the starting state of the DFA.
     * @return the DFA's start state.
     */
    public DfaState getStartState() {
        return startState;
    }

    /**
     * Returns all states of the DFA.
     * @return list of DFA states.
     */
    public List<DfaState> getAllStates() {
        return allStates;
    }

    @Override
    public String toString() {
        return "DFA{startState=" + startState + ", totalStates=" + allStates.size() + "}";
    }
}
