package com.compiler.lexer;

import java.util.*;
import com.compiler.lexer.dfa.DFA;
import com.compiler.lexer.dfa.DfaState;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;

/**
 * NfaToDfaConverter
 * -----------------
 * Converts an NFA into a DFA using the subset construction algorithm.
 */
public class NfaToDfaConverter {

    public NfaToDfaConverter() {
        // Utility class, no initialization required
    }

    /**
     * Converts an NFA to a DFA using the subset construction algorithm.
     *
     * @param nfa      The input NFA
     * @param alphabet The input alphabet (set of characters)
     * @return The resulting DFA
     */
    /* public static DFA convertNfaToDfa(NFA nfa, Set<Character> alphabet) {
        // Step 1: initial DFA state = epsilon-closure of NFA start
        Set<State> startClosure = epsilonClosure(Set.of(nfa.start));
        DfaState startState = new DfaState(startClosure);

        if (containsFinal(startClosure, nfa.accept)) {
            startState.setFinal(true);
        }

        List<DfaState> dfaStates = new ArrayList<>();
        Queue<DfaState> unprocessed = new LinkedList<>();

        dfaStates.add(startState);
        unprocessed.add(startState);

        // Step 2: process states
        while (!unprocessed.isEmpty()) {
            DfaState current = unprocessed.poll();

            for (char symbol : alphabet) {
                // Compute move and closure
                Set<Object> moveSet = move(current.getName(), symbol);
                Set<State> closure = epsilonClosure(moveSet);

                if (closure.isEmpty()) continue;

                DfaState target = findDfaState(dfaStates, closure);
                if (target == null) {
                    target = new DfaState(closure);
                    if (containsFinal(closure, nfa.accept)) {
                        target.setFinal(true);
                    }
                    dfaStates.add(target);
                    unprocessed.add(target);
                }
                current.addTransition(symbol, target);
            }
        }

        return new DFA(startState, dfaStates);
    } */

    /**
     * Computes the epsilon-closure of a set of NFA states.
     */
/*     private static Set<State> epsilonClosure(Set<Object> moveSet) {
        Stack<State> stack = new Stack<>();
        Collection<? extends State> set;
		stack.addAll(set);

        while (!stack.isEmpty()) {
            State state = stack.pop();
            for (State epsTarget : state.getEpsilonTransitions()) {
                if (closure.add(epsTarget)) {
                    stack.push(epsTarget);
                }
            }
        }
    } */

    /**
     * Returns the set of states reachable from a set of NFA states by a given symbol.
     */
/*     private static Set<Object> move(Set<State> states, char symbol) {
        Set<State> result = new HashSet<>();
        for (State s : states) {
            for (var transition : s.getTransitions(symbol)) {
                if (transition.symbol == symbol) {
                    result.add(transition.to);
                }
            }
        }

    } */

    /**
     * Finds an existing DFA state representing a given set of NFA states.
     */
    private static DfaState findDfaState(List<DfaState> dfaStates, Set<State> targetNfaStates) {
        for (DfaState dfaState : dfaStates) {
            if (dfaState.nfaStates.equals(targetNfaStates)) {
                return dfaState;
            }
        }
        return null;
    }

    /**
     * Checks if a closure contains the final NFA state.
     */
    private static boolean containsFinal(Set<State> states, State finalState) {
        return states.contains(finalState);
    }
}
