package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.OrdinaryLiteralSpec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LiteralFactory {

    private final Map<String, Predicate> predicatesByName;

    public LiteralFactory(Map<String, ? extends Predicate> predicatesByName) {
        this.predicatesByName = Collections.unmodifiableMap(predicatesByName);
    }

    public OrdinaryLiteral buildOrdinaryLiteral(OrdinaryLiteralSpec olSpec) {
        List<Term> terms = TermSpecToTermFactory.buildTerms(olSpec.getTermSpecList());
        Predicate predicate = predicatesByName.get(olSpec.getPredicateName());
        return new OrdinaryLiteral(new Atom(predicate, terms), olSpec.isPositive());
    }

    public BuiltInLiteral buildBuiltInLiteral(BuiltInLiteralSpec bilSpec) {
        ComparisonOperator comparisonOperator = ComparisonOperator.fromSymbol(bilSpec.getOperator());
        if (Objects.nonNull(comparisonOperator)) {
            checkNumberOfTerms(bilSpec);
            Term leftTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecList().get(0));
            Term rightTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecList().get(1));
            return new ComparisonBuiltInLiteral(leftTerm, rightTerm, comparisonOperator);
        } else throw new UnrecognizedBuiltInOperator(bilSpec.getOperator());
    }

    private static void checkNumberOfTerms(BuiltInLiteralSpec bilSpec) {
        if (bilSpec.getTermSpecList().size() != 2)
            throw new WrongNumberOfTermsInBuiltInLiteral(2, bilSpec.getTermSpecList().size());
    }

}
