package com.compiler.lexer.dfa;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.compiler.lexer.nfa.State;

public class DfaState {
    private static int nextId = 0;

    /** Unique identifier for this DFA state. */
    public final int id;

    /** The set of NFA states this DFA state represents. */
    public final Set<State> nfaStates;

    /** Indicates whether this DFA state is a final (accepting) state. */
    public boolean isFinal;

    /** Map of input symbols to destination DFA states (transitions). */
    public final Map<Character, DfaState> transitions;

    /**
     * Constructs a new DFA state.
     * @param nfaStates The set of NFA states that this DFA state represents.
     */
    public DfaState(Set<State> nfaStates) {
        this.id = nextId++;
        this.nfaStates = nfaStates;
        this.transitions = new HashMap<>();

        // A DFA state is final if ANY NFA state it contains is final
        this.isFinal = nfaStates.stream().anyMatch(State::isFinal);
    }

    /**
     * Returns all transitions from this state.
     * @return Map of input symbols to destination DFA states.
     */
    public Map<Character, DfaState> getTransitions() {
        return transitions;
    }

    /**
     * Adds a transition from this state to another on a given symbol.
     * @param symbol The input symbol for the transition.
     * @param toState The destination DFA state.
     */
    public void addTransition(Character symbol, DfaState toState) {
        transitions.put(symbol, toState);
    }

    /**
     * Gets the transition for a given input symbol.
     * @param symbol The input symbol for the transition.
     * @return The destination DFA state for the transition, or null if none exists.
     */
    public DfaState getTransition(char symbol) {
        return transitions.get(symbol);
    }

    /**
     * Sets the finality of the DFA state.
     * @param isFinal True if this state is a final state, false otherwise.
     */
    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    /**
     * Checks if the DFA state is final.
     * @return True if this state is a final state, false otherwise.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Two DfaStates are considered equal if they represent the same set of NFA states.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DfaState)) return false;
        DfaState other = (DfaState) obj;
        return this.nfaStates.equals(other.nfaStates);
    }

    /**
     * The hash code is based on the set of NFA states.
     */
    @Override
    public int hashCode() {
        return nfaStates.hashCode();
    }

    /**
     * Returns a string representation of the DFA state, including its id and finality.
     */
    @Override
    public String toString() {
        return "DfaState{id=" + id + ", isFinal=" + isFinal + ", nfaStates=" + nfaStates + "}";
    }

    /**
     * Returns the set of NFA states this DFA state represents.
     */
    public Set<State> getName() {
        return nfaStates;
    }
}
