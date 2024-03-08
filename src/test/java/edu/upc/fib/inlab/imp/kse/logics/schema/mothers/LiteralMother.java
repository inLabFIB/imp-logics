package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.List;
import java.util.stream.Collectors;

public class LiteralMother {
    public static OrdinaryLiteral createOrdinaryLiteralWithVariableNames(String predicateName, List<String> variableNames) {
        return createOrdinaryLiteralWithVariableNames(true, predicateName, variableNames);
    }

    public static OrdinaryLiteral createOrdinaryLiteralWithVariableNames(boolean isPositive, String predicateName, List<String> variableNames) {
        List<Term> terms = variableNames.stream().map(Variable::new).collect(Collectors.toList());
        return createOrdinaryLiteral(isPositive, predicateName, terms);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(String predicateName, List<Term> terms) {
        return createOrdinaryLiteral(true, predicateName, terms);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(String predicateName, String... termNames) {
        return createOrdinaryLiteral(true, predicateName, termNames);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(boolean isPositive, String predicateName, List<Term> terms) {
        return new OrdinaryLiteral(AtomMother.createAtom(predicateName, terms), isPositive);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(boolean isPositive, String predicateName, String... terms) {
        return new OrdinaryLiteral(AtomMother.createAtom(predicateName, terms), isPositive);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(LogicSchema schema, boolean isPositive, String predicateName, String... termNames) {
        Predicate predicateFromSchema = schema.getPredicateByName(predicateName);
        return new OrdinaryLiteral(new Atom(predicateFromSchema, TermMother.createTerms(termNames)), isPositive);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(LogicSchema schema, String predicateName, String... termNames) {
        return createOrdinaryLiteral(schema, true, predicateName, termNames);
    }

    public static OrdinaryLiteral createOrdinaryLiteral(DependencySchema schema, boolean isPositive, String predicateName, String... termNames) {
        Predicate predicateFromSchema = schema.getPredicateByName(predicateName);
        return new OrdinaryLiteral(new Atom(predicateFromSchema, TermMother.createTerms(termNames)), isPositive);
    }

    @SuppressWarnings("unused")
    public static OrdinaryLiteral createOrdinaryLiteral(DependencySchema schema, String predicateName, String... termNames) {
        return createOrdinaryLiteral(schema, true, predicateName, termNames);
    }

    public static CustomBuiltInLiteral createCustomBuiltInLiteral(String customBuiltIn, String... termNames) {
        return new CustomBuiltInLiteral(customBuiltIn, TermMother.createTerms(termNames));
    }
}
