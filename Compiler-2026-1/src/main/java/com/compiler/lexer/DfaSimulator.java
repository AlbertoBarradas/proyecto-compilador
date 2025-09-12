package com.compiler.lexer;

import com.compiler.lexer.dfa.DFA;
import com.compiler.lexer.dfa.DfaState;

/**
 * DfaSimulator
 * ------------
 * Simulates the execution of a DFA on a given input string.
 */
public class DfaSimulator {

    public DfaSimulator() {
        // No initialization needed
    }

    /**
     * Simulates the DFA on the given input string.
     *
     * @param dfa   The DFA to simulate.
     * @param input The input string to test.
     * @return True if the input is accepted by the DFA, false otherwise.
     */
    public boolean simulate(DFA dfa, String input) {
        DfaState currentState = dfa.startState;

        for (char c : input.toCharArray()) {
            currentState = currentState.getTransition(c);

            if (currentState == null) {
                return false; // No valid transition, reject input
            }
        }

        return currentState.isFinal();
    }
}
