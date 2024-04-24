package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.NoNegatableLiteralException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Set;

/**
 * Implementation of the logic literal. Literals might be ordinary (e.g. "{@code Emp(x)}"), or built-in (e.g.
 * "{@code x < 4}").
 * <p>
 * A literal should appear, at most, inside the body of one {@code NormalClause}. That is, literals should not be reused
 * among several NormalClauses.
 */
public abstract class Literal {

    /**
     * Returns arity of literal, i.e. the number of terms.
     *
     * @return arity of literal, i.e. the number of terms.
     */
    public int getArity() {
        return getTerms().size();
    }

    public abstract ImmutableTermList getTerms();

    /**
     * Constructs new {@code Literal} after applying the substitution to the literal terms.
     *
     * @param substitution substitution to apply.
     * @return a literal after applying the given substitution. The literal will be new if some term has changed,
     * otherwise it will be the same.
     */
    public abstract Literal applySubstitution(Substitution substitution);

    /**
     * Returns used variables of literals.
     *
     * @return used variables of literals.
     */
    public Set<Variable> getUsedVariables() {
        return getTerms().getUsedVariables();
    }

    public abstract <T> T accept(LogicSchemaVisitor<T> visitor);

    /**
     * Returns {@code true} if literal can be negated.
     *
     * @return {@code true} if literal can be negated.
     * @see Literal#buildNegatedLiteral
     */
    public boolean canBeNegated() {
        try {
            buildNegatedLiteral();
            return true;
        } catch (NoNegatableLiteralException ex) {
            return false;
        }
    }

    /**
     * This method constructs a new literal that is the negation of this one.
     * <p>
     * E.g. given an ordinary literal "{@code P(x)}" it will return a new literal "{@code not(P(x))}", or given a
     * built-in literal "{@code x < y}" it will return "{@code x >= y}"
     *
     * @return a new literal that is the negation of this literal, if this is possible.
     * @throws NoNegatableLiteralException in case the literal cannot be negated.
     */
    public Literal buildNegatedLiteral() {
        throw new NoNegatableLiteralException(this);
    }

    /**
     * Returns if all its terms of literal are constants, {@code false} otherwise.
     *
     * @return {@code true} if all its terms of literal are constants, {@code false} otherwise.
     */
    public boolean isGround() {
        return getTerms().stream().allMatch(Term::isConstant);
    }
}
