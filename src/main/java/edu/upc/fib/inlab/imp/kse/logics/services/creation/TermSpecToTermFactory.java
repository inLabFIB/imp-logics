package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.Constant;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.ConstantSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.VariableSpec;

import java.util.List;

/**
 * Factory in charge of creating the corresponding subclass of Term for a given subclass of TermSpec
 */
public class TermSpecToTermFactory {

    public static ImmutableTermList buildTerms(List<TermSpec> termSpecList) {
        List<Term> terms = termSpecList.stream()
                .map(TermSpecToTermFactory::buildTerm)
                .toList();
        return new ImmutableTermList(terms);
    }

    public static Term buildTerm(TermSpec termSpec) {
        if (termSpec instanceof VariableSpec) {
            return new Variable(termSpec.getName());
        } else if (termSpec instanceof ConstantSpec) {
            return new Constant(termSpec.getName());
        } else throw new RuntimeException("Unrecognized term spec " + termSpec.getClass().getName());
    }

}
