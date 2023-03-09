package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.visitor.Visitable;

/**
 * Implementation of the logic literal. Literals might be Ordinary (e.g. "Emp(x)"), or built-in (e.g. "x < 4")
 * A literal should appear, at most, inside the body of one NormalClause.
 * That is, literals should not be reused among several NormalClauses.
 */
public abstract class Literal implements Visitable {
    public abstract ImmutableTermList getTerms();

//    public abstract Literal applySubstitution(Substitution substitution);

//    protected List<Term> applySubstitutionToTerms(Substitution substitution) {
//        List<Term> newTerms = null;
//        return null;
//    }
}
