package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;


public class EqualityComparisonBuiltInLiteral extends ComparisonBuiltInLiteral {

    public EqualityComparisonBuiltInLiteral(Term leftTerm, Term rightTerm) {
        super(leftTerm, rightTerm, ComparisonOperator.EQUALS);
    }
}
