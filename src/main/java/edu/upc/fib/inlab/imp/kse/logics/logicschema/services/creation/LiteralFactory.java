package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        String operator = bilSpec.getOperator();
        Optional<ComparisonOperator> comparisonOperatorOpt = ComparisonOperator.fromSymbol(operator);
        if (comparisonOperatorOpt.isPresent()) {
            checkNumberOfTerms(bilSpec, 2);
            Term leftTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecList().get(0));
            Term rightTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecList().get(1));
            ComparisonOperator comparisonOperator = comparisonOperatorOpt.get();
            if (comparisonOperator.equals(ComparisonOperator.EQUALS))
                return new EqualityComparisonBuiltInLiteral(leftTerm, rightTerm);
            return new ComparisonBuiltInLiteral(leftTerm, rightTerm, comparisonOperator);
        }

        Optional<Boolean> booleanValue = BooleanBuiltInLiteral.fromOperator(operator);
        if (booleanValue.isPresent()) {
            checkEmptyTerms(bilSpec);
            return new BooleanBuiltInLiteral(booleanValue.get());
        } else {
            return new CustomBuiltInLiteral(bilSpec.getOperator(), TermSpecToTermFactory.buildTerms(bilSpec.getTermSpecList()));
        }
    }

    private static void checkEmptyTerms(BuiltInLiteralSpec bilSpec) {
        checkNumberOfTerms(bilSpec, 0);
    }

    private static void checkNumberOfTerms(BuiltInLiteralSpec bilSpec, int expectedTerms) {
        if (bilSpec.getTermSpecList().size() != expectedTerms)
            throw new WrongNumberOfTermsInBuiltInLiteral(expectedTerms, bilSpec.getTermSpecList().size());
    }

}
