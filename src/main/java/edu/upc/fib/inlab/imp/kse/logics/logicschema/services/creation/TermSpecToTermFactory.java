package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Constant;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.ConstantSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.VariableSpec;

import java.util.List;

/**
 * Factory in charge of creating the corresponding subclass of Term for a given subclass of TermSpec
 */
public class TermSpecToTermFactory {

    private TermSpecToTermFactory() {
        throw new IllegalStateException("Utility class");
    }

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
        } else throw new IMPLogicsException("Unrecognized term spec " + termSpec.getClass().getName());
    }

}
