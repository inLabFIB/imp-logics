package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.services.creation.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.imp.logics.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import edu.upc.imp.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.LiteralSpec;
import edu.upc.imp.logics.services.creation.spec.OrdinaryLiteralSpec;

import java.util.*;

/**
 * Class in charge of instantiating the body of some normal clause given the predicates of the schema
 */
class BodyBuilder {
    private final Map<String, Predicate> predicatesByName;
    private final List<Literal> body;

    public BodyBuilder(Map<String, ? extends Predicate> predicatesByName) {
        this.predicatesByName = Collections.unmodifiableMap(predicatesByName);
        body = new LinkedList<>();
    }

    public BodyBuilder addLiteral(LiteralSpec literalSpec) {
        if (literalSpec instanceof OrdinaryLiteralSpec olSpec) {
            body.add(buildOrdinaryLiteral(olSpec));
        } else if (literalSpec instanceof BuiltInLiteralSpec biSpec) {
            body.add(buildBuiltInLiteral(biSpec));
        } else throw new RuntimeException("Unrecognized literalSpec " + literalSpec.getClass().getName());
        return this;
    }

    private Literal buildOrdinaryLiteral(OrdinaryLiteralSpec olSpec) {
        List<Term> terms = TermSpecToTermFactory.buildTerms(olSpec.getTermSpecList());
        Predicate predicate = predicatesByName.get(olSpec.getPredicateName());
        return new OrdinaryLiteral(new Atom(predicate, terms), olSpec.isPositive());
    }

    private Literal buildBuiltInLiteral(BuiltInLiteralSpec bilSpec) {
        ComparisonOperator comparisonOperator = ComparisonOperator.fromSymbol(bilSpec.getOperator());
        if (Objects.nonNull(comparisonOperator)) {
            checkNumberOfTerms(bilSpec);
            Term leftTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecs().get(0));
            Term rightTerm = TermSpecToTermFactory.buildTerm(bilSpec.getTermSpecs().get(1));
            return new ComparisonBuiltInLiteral(leftTerm, rightTerm, comparisonOperator);
        } else throw new UnrecognizedBuiltInOperator(bilSpec.getOperator());
    }

    private static void checkNumberOfTerms(BuiltInLiteralSpec bilSpec) {
        if (bilSpec.getTermSpecs().size() != 2)
            throw new WrongNumberOfTermsInBuiltInLiteral(2, bilSpec.getTermSpecs().size());
    }

    public BodyBuilder addLiterals(List<LiteralSpec> bodySpec) {
        bodySpec.forEach(this::addLiteral);
        return this;
    }

    public List<Literal> build() {
        return body;
    }
}
