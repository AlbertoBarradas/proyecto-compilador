package com.compiler.parser.syntax;

import java.util.*;
import com.compiler.parser.grammar.Grammar;
import com.compiler.parser.grammar.Production;
import com.compiler.parser.grammar.Symbol;
import com.compiler.parser.grammar.SymbolType;

/**
 * Calculates the FIRST and FOLLOW sets for a given grammar.
 * Main task of Practice 5.
 */
public class StaticAnalyzer {
    private final Grammar grammar;
    private final Map<Symbol, Set<Symbol>> firstSets;
    private final Map<Symbol, Set<Symbol>> followSets;

    private static final Symbol EPSILON = new Symbol("ε", SymbolType.TERMINAL);
    private static final Symbol END = new Symbol("$", SymbolType.TERMINAL);

    public StaticAnalyzer(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
    }

    /**
     * Calculates and returns the FIRST sets for all symbols.
     * @return A map from Symbol to its FIRST set.
     */
    public Map<Symbol, Set<Symbol>> getFirstSets() {
        if (!firstSets.isEmpty()) {
            return firstSets;
        }

        // 1. Inicializar
        for (Symbol t : grammar.getTerminals()) {
            firstSets.put(t, new HashSet<>(Collections.singletonList(t)));
        }
        // aseguramos epsilon también
        firstSets.put(EPSILON, new HashSet<>(Collections.singletonList(EPSILON)));

        for (Symbol nt : grammar.getNonTerminals()) {
            firstSets.put(nt, new HashSet<>());
        }

        // 2. Repetir hasta convergencia
        boolean changed;
        do {
            changed = false;
            for (Production p : grammar.getProductions()) {
                Symbol A = p.getLeft();
                List<Symbol> rhs = p.getRight();

                boolean allNullable = true;
                for (Symbol Xi : rhs) {
                    Set<Symbol> firstXi = firstSets.get(Xi);
                    if (firstXi != null) {
                        // Añadir FIRST(Xi) sin epsilon
                        for (Symbol s : firstXi) {
                            if (!s.equals(EPSILON)) {
                                if (firstSets.get(A).add(s)) {
                                    changed = true;
                                }
                            }
                        }
                        // si Xi no contiene epsilon, parar
                        if (!firstXi.contains(EPSILON)) {
                            allNullable = false;
                            break;
                        }
                    }
                }
                // Si todos Xi pueden derivar epsilon, añadir epsilon
                if (allNullable) {
                    if (firstSets.get(A).add(EPSILON)) {
                        changed = true;
                    }
                }
            }
        } while (changed);

        return firstSets;
    }

    /**
     * Calculates and returns the FOLLOW sets for non-terminals.
     * @return A map from Symbol to its FOLLOW set.
     */
    public Map<Symbol, Set<Symbol>> getFollowSets() {
        if (!followSets.isEmpty()) {
            return followSets;
        }

        // 1. Inicializar
        for (Symbol nt : grammar.getNonTerminals()) {
            followSets.put(nt, new HashSet<>());
        }
        // añadir $ al símbolo inicial
        followSets.get(grammar.getStartSymbol()).add(END);

        Map<Symbol, Set<Symbol>> first = getFirstSets();

        // 2. Iterar hasta convergencia
        boolean changed;
        do {
            changed = false;
            for (Production p : grammar.getProductions()) {
                Symbol A = p.getLeft();
                List<Symbol> rhs = p.getRight();

                for (int i = 0; i < rhs.size(); i++) {
                    Symbol Xi = rhs.get(i);
                    if (Xi.type == SymbolType.NON_TERMINAL) {
                        // mirar símbolos después de Xi
                        boolean allNullable = true;
                        for (int j = i + 1; j < rhs.size(); j++) {
                            Symbol Xj = rhs.get(j);
                            Set<Symbol> firstXj = first.get(Xj);

                            if (firstXj != null) {
                                for (Symbol s : firstXj) {
                                    if (!s.equals(EPSILON)) {
                                        if (followSets.get(Xi).add(s)) {
                                            changed = true;
                                        }
                                    }
                                }
                                if (!firstXj.contains(EPSILON)) {
                                    allNullable = false;
                                    break;
                                }
                            } else {
                                allNullable = false;
                                break;
                            }
                        }
                        // si todos los símbolos siguientes pueden derivar ε, añadir FOLLOW(A)
                        if (i == rhs.size() - 1 || allNullable) {
                            for (Symbol s : followSets.get(A)) {
                                if (followSets.get(Xi).add(s)) {
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);

        return followSets;
    }
}
