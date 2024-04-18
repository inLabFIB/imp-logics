package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.ConstantSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.VariableSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * Factory in charge of instantiating the corresponding subclass of Term (Constant, or Variable)
 * for a given String.
 */
public class StringToTermSpecFactory {

    private final TermTypeCriteria termTypeCriteria;

    public StringToTermSpecFactory(TermTypeCriteria termTypeCriteria) {
        this.termTypeCriteria = termTypeCriteria;
    }

    public StringToTermSpecFactory() {
        this(new AllVariableTermTypeCriteria());
    }

    public List<TermSpec> createTermSpecs(String... termNames) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (String name : termNames) {
            TermSpec termSpec = createTermSpec(name);

            termSpecList.add(termSpec);
        }
        return termSpecList;
    }

    public TermSpec createTermSpec(String name) {
        if (termTypeCriteria.isConstant(name)) {
            return new ConstantSpec(name);
        } else if (termTypeCriteria.isVariable(name)) {
            return new VariableSpec(name);
        } else {
            throw new IMPLogicsException("Unrecognized term name: " + name);
        }
    }
}
