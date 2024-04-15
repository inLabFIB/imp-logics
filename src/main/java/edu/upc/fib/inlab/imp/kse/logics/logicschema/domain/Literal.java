package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.NoNegatableLiteralException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.Set;

/**
 * Implementation of the logic literal. Literals might be Ordinary (e.g. "Emp(x)"), or built-in (e.g. {@code "x < 4"})
 * A literal should appear, at most, inside the body of one NormalClause.
 * That is, literals should not be reused among several NormalClauses.
 */
public abstract class Literal {

    public abstract ImmutableTermList getTerms();

    public int getArity() {
        return getTerms().size();
    }

    /**
     * @param substitution not null
     * @return a literal after applying the given substitution. The literal will be new if some term has changed,
     * otherwise it will be the same
     */
    public abstract Literal applySubstitution(Substitution substitution);

    public Set<Variable> getUsedVariables() {
        return getTerms().getUsedVariables();
    }

    public abstract <T> T accept(LogicSchemaVisitor<T> visitor);

    /**
     * This method builds a new literal that is the negation of this one.
     * E.g.: given an ordinary literal "P(x)" it will return a new literal "not(P(x))",
     * or given a built-in literal {@code "x < y" it will return "x >= y"}
     * <p>
     * Not all built-in literals can be negated. Thus, this function might throw an Exception.
     *
     * @return a new literal that is the negation of this literal, if this is possible
     * @throws NoNegatableLiteralException in case the literal cannot be negated
     */
    public Literal buildNegatedLiteral() {
        throw new NoNegatableLiteralException(this);
    }


    /**
     * @return whether this literal can be negated.
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
     * @return true if all its terms of literal are constants, false otherwise
     */
    public boolean isGround() {
        return getTerms().stream().allMatch(Term::isConstant);
    }
}
