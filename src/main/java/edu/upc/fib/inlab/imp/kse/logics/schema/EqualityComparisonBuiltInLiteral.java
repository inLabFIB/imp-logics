package edu.upc.fib.inlab.imp.kse.logics.schema;


public class EqualityComparisonBuiltInLiteral extends ComparisonBuiltInLiteral {

    public EqualityComparisonBuiltInLiteral(Term leftTerm, Term rightTerm) {
        super(leftTerm, rightTerm, ComparisonOperator.EQUALS);
    }
}
