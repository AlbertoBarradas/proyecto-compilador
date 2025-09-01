package com.compiler.lexer.regex;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Utility class for regular expression parsing using the Shunting Yard algorithm.
 * Supports operators: union '|', concatenation '·' (explicit), and unary postfix
 * operators '*', '+', '?' with parentheses '(' ')'.
 *
 * Notes:
 * - Use insertConcatenationOperator first to make implicit concatenations explicit.
 * - Backslash '\' escapes the next character and treats it as a literal.
 */
public class ShuntingYard {

    public ShuntingYard() { }

    /**
     * Inserts the explicit concatenation operator ('·') where concatenation is implied.
     * Rules (insert between A and B when):
     *   Left A ∈ { operand, ')', '*', '+', '?' }
     *   Right B ∈ { operand, '(', '\' (escape) }
     */
    public static String insertConcatenationOperator(String regex) {
        if (regex == null || regex.isEmpty()) return "";

        StringBuilder out = new StringBuilder();
        int n = regex.length();

        for (int i = 0; i < n; i++) {
            char c = regex.charAt(i);

            // Copy escapes as-is: '\x' -> two chars in output
            if (c == '\\') {
                out.append(c);
                if (i + 1 < n) {
                    out.append(regex.charAt(++i));
                }
            } else if (Character.isWhitespace(c)) {
                // ignore whitespaces
                continue;
            } else {
                out.append(c);
            }

            // If there's a next token, decide whether to insert concatenation
            if (i + 1 < n) {
                char next = regex.charAt(i + 1);

                // Skip whitespace when peeking next
                int j = i + 1;
                while (j < n && Character.isWhitespace(regex.charAt(j))) j++;
                if (j >= n) break;
                next = regex.charAt(j);

                // If next begins an escape, treat as operand for the "right" side
                boolean rightIsEscape = (next == '\\');

                // Determine left and right categories
                char left = c;
                boolean leftIsEscape = (left == '\\');
                // If left was an escape, the real left symbol is the escaped char already appended.
                // For concatenation decision, an escaped char behaves like an operand.
                boolean leftCanConcat =
                        leftIsEscape || isOperand(left) || left == ')' || left == '*' || left == '+' || left == '?';

                boolean rightCanConcat =
                        rightIsEscape || isOperand(next) || next == '(';

                if (leftCanConcat && rightCanConcat) {
                    out.append('·');
                }
            }
        }
        return out.toString();
    }

    /**
     * Returns true if c is treated as an operand (literal symbol) for this regex dialect.
     * Operators recognized: '|', '*', '?', '+', '(', ')', '·'
     * Everything else (including '.') is considered an operand unless escaped logic applies elsewhere.
     */
    private static boolean isOperand(char c) {
        return c != '|' && c != '*' && c != '?' && c != '+' && c != '(' && c != ')' && c != '·';
    }

    /**
     * Converts an infix regular expression to postfix (RPN) using Shunting Yard.
     * Precedence:  (* + ?) > · > |
     * Associativity: left for '|' and '·'; right for postfix unary (*, +, ?)
     */
    public static String toPostfix(String infixRegex) {
        if (infixRegex == null) throw new IllegalArgumentException("regex null");
        String withConcat = insertConcatenationOperator(infixRegex);

        Map<Character, Integer> prec = new HashMap<>();
        prec.put('|', 1);
        prec.put('·', 2);
        prec.put('*', 3);
        prec.put('+', 3);
        prec.put('?', 3);

        // left-assoc for binary ops, right-assoc for unary postfix
        Map<Character, Boolean> leftAssoc = new HashMap<>();
        leftAssoc.put('|', true);
        leftAssoc.put('·', true);
        leftAssoc.put('*', false);
        leftAssoc.put('+', false);
        leftAssoc.put('?', false);

        StringBuilder output = new StringBuilder();
        Deque<Character> stack = new ArrayDeque<>();

        int n = withConcat.length();
        for (int i = 0; i < n; i++) {
            char c = withConcat.charAt(i);

            if (Character.isWhitespace(c)) {
                continue;
            }

            if (c == '\\') {
                // escaped literal: output both '\' and the next char as a single literal token
                output.append(c);
                if (i + 1 < n) {
                    output.append(withConcat.charAt(++i));
                } else {
                    throw new IllegalArgumentException("Dangling escape at end of regex");
                }
            } else if (isOperand(c)) {
                output.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                if (stack.isEmpty() || stack.peek() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                stack.pop(); // remove '('
            } else {
                // operator
                while (!stack.isEmpty() && stack.peek() != '(') {
                    char top = stack.peek();
                    int pTop = prec.getOrDefault(top, -1);
                    int pCur = prec.getOrDefault(c, -1);

                    if (pTop > pCur || (pTop == pCur && leftAssoc.getOrDefault(c, true))) {
                        output.append(stack.pop());
                    } else {
                        break;
                    }
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            char op = stack.pop();
            if (op == '(' || op == ')') {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.append(op);
        }

        return output.toString();
    }
}
