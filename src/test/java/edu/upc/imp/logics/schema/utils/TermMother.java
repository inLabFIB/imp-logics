package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Constant;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;
import edu.upc.imp.logics.services.creation.spec.helpers.DefaultTermTypeCriteria;
import edu.upc.imp.logics.services.creation.spec.helpers.TermTypeCriteria;

import java.util.Arrays;
import java.util.List;

public class TermMother {

    private static final TermTypeCriteria termTypeCriteria = new DefaultTermTypeCriteria();

    public static List<Term> createTerms(String... terms) {
        return Arrays.stream(terms).map(TermMother::createTerm).toList();
    }

    public static Term createTerm(String termName) {
        if (termTypeCriteria.isVariable(termName)) {
            return new Variable(termName);
        } else if (termTypeCriteria.isConstant(termName)) {
            return new Constant(termName);
        } else throw new RuntimeException("Unrecognized term name " + termName);
    }
}
