package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Constant;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.ConstantSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.UnnamedVariableSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.VariableSpec;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Factory in charge of creating the corresponding subclass of Term for a given subclass of TermSpec
 */
public class ContextTermFactory {

    private static final String UNNAMED_VARIABLE_NAME = "u";

    private final Set<String> usedVariableNames;
    private int unnamedVariableIndex;

    public ContextTermFactory(Set<String> contextVariableNames) {
        this.usedVariableNames = new LinkedHashSet<>(contextVariableNames);
        this.unnamedVariableIndex = 0;
    }

    public ImmutableTermList buildTerms(List<TermSpec> termSpecList) {

        List<Term> terms = new ArrayList<>();
        for (TermSpec termSpec : termSpecList) {
            if (termSpec instanceof UnnamedVariableSpec) {
                String newVariableName = getNewUnnamedVariable();
                terms.add(new Variable(newVariableName));
            } else if (termSpec instanceof VariableSpec) terms.add(new Variable(termSpec.getName()));
            else if (termSpec instanceof ConstantSpec) terms.add(new Constant(termSpec.getName()));
            else throw new IMPLogicsException("Unrecognized term spec " + termSpec.getClass().getName());
        }
        return new ImmutableTermList(terms);
    }

    private String getNewUnnamedVariable() {
        String candidateNewUnnamedName = UNNAMED_VARIABLE_NAME + this.unnamedVariableIndex++;
        while (usedVariableNames.contains(candidateNewUnnamedName)) {
            candidateNewUnnamedName = UNNAMED_VARIABLE_NAME + this.unnamedVariableIndex++;
        }
        return candidateNewUnnamedName;
    }
}
