package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;


import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Implementation of a logic derivation rule. That is, a NormalClause with head E.g. "P(x) :- R(x, y)"
 */
public class DerivationRule extends NormalClause {
    /**
     * Invariants: - head cannot be null - head's predicate must be a derived predicate
     */
    private final Atom head;

    /**
     * DerivationRules should be created by their corresponding DerivedLiteral.
     *
     * @param head non-null
     * @param body non-null and not empty
     */
    protected DerivationRule(Atom head, List<Literal> body) {
        super(body);
        this.head = head;
    }

    public ImmutableTermList getHeadTerms() {
        return head.getTerms();
    }

    public Set<Variable> getExistentialVariables() {
        Set<Variable> existentialVariables = new LinkedHashSet<>(getBody().getUsedVariables());
        existentialVariables.removeAll(getUniversalVariables());
        return existentialVariables;
    }

    public Set<Variable> getUniversalVariables() {
        return head.getTerms().getUsedVariables();
    }

    @Override
    public boolean isSafe() {
        Set<Variable> variablesInPositiveOrdinaryLiterals = getBody().getVariablesInPositiveOrdinaryLiterals();

        Set<Variable> variablesInNegativeOrdinaryLiterals = getBody().getVariablesInNegativeOrdinaryLiterals();
        Set<Variable> variablesInBuiltInLiterals = getBody().getVariablesInBuiltInLiterals();
        Set<Variable> variablesInHead = getHead().getVariables();

        Set<Variable> variablesInNegativeLiteralsOrBuiltInLiteralsOrHead = new LinkedHashSet<>();
        variablesInNegativeLiteralsOrBuiltInLiteralsOrHead.addAll(variablesInNegativeOrdinaryLiterals);
        variablesInNegativeLiteralsOrBuiltInLiteralsOrHead.addAll(variablesInBuiltInLiterals);
        variablesInNegativeLiteralsOrBuiltInLiteralsOrHead.addAll(variablesInHead);

        return variablesInPositiveOrdinaryLiterals.containsAll(variablesInNegativeLiteralsOrBuiltInLiteralsOrHead);
    }

    public Atom getHead() {
        return head;
    }

    @Override
    public String toString() {
        return head.toString() + " :- " + this.getBody();
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
