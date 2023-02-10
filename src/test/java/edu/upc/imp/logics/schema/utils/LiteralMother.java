package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;

import java.util.List;
import java.util.stream.Collectors;

public class LiteralMother {
    public static Literal createOrdinaryLiteralWithVariableNames(String predicateName, List<String> variableNames) {
        List<Term> terms = variableNames.stream().map(Variable::new).collect(Collectors.toList());
        return createOrdinaryLiteral(predicateName, terms);
    }

    public static Literal createOrdinaryLiteral(String predicateName, List<Term> terms) {
        return new OrdinaryLiteral(AtomMother.createAtom(predicateName, terms));
    }
}
