package com.compiler.lexer.regex;

import java.util.Stack;

import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;
import com.compiler.lexer.nfa.Transition;

/**
 * Parses regular expressions and constructs NFAs using Thompson's construction.
 */
public class RegexParser {
    /**
     * Default constructor for RegexParser.
     */
    public RegexParser() { }

    /**
     * Converts an infix regular expression to an NFA.
     *
     * @param infixRegex The regular expression in infix notation.
     * @return The constructed NFA.
     */
    public NFA parse(String infixRegex) {
        String postfix = ShuntingYard.toPostfix(infixRegex);
        return buildNfaFromPostfix(postfix);
    }

    /**
     * Builds an NFA from a postfix regular expression.
     *
     * @param postfixRegex The regular expression in postfix notation.
     * @return The constructed NFA.
     */
    private NFA buildNfaFromPostfix(String postfixRegex) {
        Stack<NFA> stack = new Stack<>();

        for (int i = 0; i < postfixRegex.length(); i++) {
            char c = postfixRegex.charAt(i);

            if (isOperand(c)) {
                stack.push(createNfaForCharacter(c));
            } else {
                switch (c) {
                    case '·':
                        handleConcatenation(stack);
                        break;
                    case '|':
                        handleUnion(stack);
                        break;
                    case '*':
                        handleKleeneStar(stack);
                        break;
                    case '+':
                        handlePlus(stack);
                        break;
                    case '?':
                        handleOptional(stack);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator: " + c);
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid postfix regex: stack size = " + stack.size());
        }

        return stack.pop();
    }

    /**
     * Handles the '?' operator (zero or one occurrence).
     */
    private void handleOptional(Stack<NFA> stack) {
        NFA nfa = stack.pop();
        State start = new State();
        State end = new State();

        start.transitions.add(new Transition(null, nfa.startState)); // epsilon to original
        start.transitions.add(new Transition(null, end));            // epsilon to end
        nfa.endState.transitions.add(new Transition(null, end));     // epsilon from old end to new end

        stack.push(new NFA(start, end));
    }

    /**
     * Handles the '+' operator (one or more occurrences).
     */
    private void handlePlus(Stack<NFA> stack) {
        NFA nfa = stack.pop();
        State start = new State();
        State end = new State();

        start.transitions.add(new Transition(null, nfa.startState)); // must go through once
        nfa.endState.transitions.add(new Transition(null, nfa.startState)); // loop back
        nfa.endState.transitions.add(new Transition(null, end));            // epsilon to end

        stack.push(new NFA(start, end));
    }

    /**
     * Creates an NFA for a single character.
     */
    private NFA createNfaForCharacter(char c) {
        State start = new State();
        State end = new State();
        start.transitions.add(new Transition(c, end));
        return new NFA(start, end);
    }

    /**
     * Handles the concatenation operator (·).
     */
    private void handleConcatenation(Stack<NFA> stack) {
        NFA nfa2 = stack.pop();
        NFA nfa1 = stack.pop();

        nfa1.endState.isFinal = false; // old end no longer final
        nfa1.endState.transitions.add(new Transition(null, nfa2.startState));

        stack.push(new NFA(nfa1.startState, nfa2.endState));
    }

    /**
     * Handles the union operator (|).
     */
    private void handleUnion(Stack<NFA> stack) {
        NFA nfa2 = stack.pop();
        NFA nfa1 = stack.pop();

        State start = new State();
        State end = new State();

        start.transitions.add(new Transition(null, nfa1.startState));
        start.transitions.add(new Transition(null, nfa2.startState));

        nfa1.endState.transitions.add(new Transition(null, end));
        nfa2.endState.transitions.add(new Transition(null, end));

        nfa1.endState.isFinal = false;
        nfa2.endState.isFinal = false;

        stack.push(new NFA(start, end));
    }

    /**
     * Handles the Kleene star operator (*).
     */
    private void handleKleeneStar(Stack<NFA> stack) {
        NFA nfa = stack.pop();

        State start = new State();
        State end = new State();

        start.transitions.add(new Transition(null, nfa.startState)); // go to NFA
        start.transitions.add(new Transition(null, end));            // or skip entirely

        nfa.endState.transitions.add(new Transition(null, nfa.startState)); // loop back
        nfa.endState.transitions.add(new Transition(null, end));            // go to new end

        nfa.endState.isFinal = false;

        stack.push(new NFA(start, end));
    }

    /**
     * Checks if a character is an operand (not an operator).
     */
    private boolean isOperand(char c) {
        return c != '|' && c != '*' && c != '?' && c != '+' && c != '·' && c != '(' && c != ')';
    }
}
