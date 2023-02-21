package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.*;
import edu.upc.imp.logics.specification.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.imp.logics.specification.exceptions.WrongNumberOfTermsInBuiltInLiteral;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BodyBuilder {
    private final Map<String, MutablePredicate> predicatesByName;
    private final List<Literal> body;

    public BodyBuilder(Map<String, MutablePredicate> predicatesByName) {
        this.predicatesByName = predicatesByName;
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
