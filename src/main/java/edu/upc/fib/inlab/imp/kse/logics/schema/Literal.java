package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.NoNegatableLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.Set;

/**
 * Implementation of the logic literal. Literals might be Ordinary (e.g. "Emp(x)"), or built-in (e.g. "x < 4")
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
     * @return a new Literal after applying the given substitution
     */
    public abstract Literal applySubstitution(Substitution substitution);

    public Set<Variable> getUsedVariables() {
        return getTerms().getUsedVariables();
    }

    public abstract <T> T accept(LogicSchemaVisitor<T> visitor);

    /**
     * @return a new literal that is the negation of this literal, if this is possible
     * @throws NoNegatableLiteral in case the literal cannot be negated
     */
    public Literal buildNegatedLiteral() {
        throw new NoNegatableLiteral(this);
    }

    /**
     * @return true if all its terms of literal are constants, false otherwise
     */
    public boolean isGround() {
        return getTerms().stream().allMatch(Term::isConstant);
    }
}
