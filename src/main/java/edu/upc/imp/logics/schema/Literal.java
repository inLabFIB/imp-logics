package edu.upc.imp.logics.schema;

import java.util.List;

/**
 * Implementation of the logic literal. Literals might be Ordinary (e.g. "Emp(x)"), or built-in (e.g. "x < 4")
 * A literal should appear, at most, inside the body of one NormalClause.
 * That is, literals should not be reused among several NormalClauses.
 */
public abstract class Literal {
    public abstract List<Term> getTerms();
}
