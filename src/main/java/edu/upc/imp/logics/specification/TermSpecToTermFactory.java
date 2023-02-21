package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.schema.Constant;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;

import java.util.LinkedList;
import java.util.List;

public class TermSpecToTermFactory {

    public static List<Term> buildTerms(List<TermSpec> termSpecList) {
        List<Term> terms = new LinkedList<>();
        for (TermSpec termSpec : termSpecList) {
            Term term = buildTerm(termSpec);
            terms.add(term);
        }
        return terms;
    }

    public static Term buildTerm(TermSpec termSpec) {
        if (termSpec instanceof VariableSpec) {
            return new Variable(termSpec.getName());
        } else if (termSpec instanceof ConstantSpec) {
            return new Constant(termSpec.getName());
        } else throw new RuntimeException("Unrecognized term spec " + termSpec.getClass().getName());
    }

}
