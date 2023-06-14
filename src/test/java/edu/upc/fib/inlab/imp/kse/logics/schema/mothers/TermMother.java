package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.schema.Constant;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.TermTypeCriteria;

import java.util.Arrays;
import java.util.List;

public class TermMother {

    private static final TermTypeCriteria termTypeCriteria = new AllVariableTermTypeCriteria();

    public static ImmutableTermList createTerms(List<String> terms) {
        return new ImmutableTermList(terms.stream().map(TermMother::createTerm).toList());
    }

    public static ImmutableTermList createTerms(String... terms) {
        return new ImmutableTermList(Arrays.stream(terms).map(TermMother::createTerm).toList());
    }

    public static Term createTerm(String termName) {
        if (termTypeCriteria.isVariable(termName)) {
            return new Variable(termName);
        } else if (termTypeCriteria.isConstant(termName)) {
            return new Constant(termName);
        } else throw new RuntimeException("Unrecognized term name " + termName);
    }
}
