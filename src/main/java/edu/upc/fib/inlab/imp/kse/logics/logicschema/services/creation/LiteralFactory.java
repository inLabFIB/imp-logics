package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteralException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;

import java.util.*;

//TODO: add documentation
public class LiteralFactory {

    private final Map<String, Predicate> predicatesByName;
    private final ContextTermFactory contextTermFactory;

    public LiteralFactory(Map<String, ? extends Predicate> predicatesByName) {
        this(predicatesByName, new ContextTermFactory(Set.of()));
    }

    public LiteralFactory(Map<String, ? extends Predicate> predicatesByName, ContextTermFactory contextTermFactory) {
        this.predicatesByName = Collections.unmodifiableMap(predicatesByName);
        this.contextTermFactory = contextTermFactory;
    }

    public OrdinaryLiteral buildOrdinaryLiteral(OrdinaryLiteralSpec olSpec) {
        List<Term> terms = contextTermFactory.buildTerms(olSpec.getTermSpecList());
        Predicate predicate = predicatesByName.get(olSpec.getPredicateName());
        return new OrdinaryLiteral(new Atom(predicate, terms), olSpec.isPositive());
    }

    public BuiltInLiteral buildBuiltInLiteral(BuiltInLiteralSpec bilSpec) {
        String operator = bilSpec.getOperator();
        Optional<ComparisonOperator> comparisonOperatorOpt = ComparisonOperator.fromSymbol(operator);
        if (comparisonOperatorOpt.isPresent()) {
            checkNumberOfTerms(bilSpec, 2);
            ImmutableTermList terms = contextTermFactory.buildTerms(bilSpec.getTermSpecList());
            Term leftTerm = terms.get(0);
            Term rightTerm = terms.get(1);
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
            return new CustomBuiltInLiteral(bilSpec.getOperator(), contextTermFactory.buildTerms(bilSpec.getTermSpecList()));
        }
    }

    private static void checkNumberOfTerms(BuiltInLiteralSpec bilSpec, int expectedTerms) {
        if (bilSpec.getTermSpecList().size() != expectedTerms)
            throw new WrongNumberOfTermsInBuiltInLiteralException(expectedTerms, bilSpec.getTermSpecList().size());
    }

    private static void checkEmptyTerms(BuiltInLiteralSpec bilSpec) {
        checkNumberOfTerms(bilSpec, 0);
    }

}
