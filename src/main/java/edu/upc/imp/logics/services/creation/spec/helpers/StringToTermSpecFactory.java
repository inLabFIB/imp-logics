package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.ConstantSpec;
import edu.upc.imp.logics.services.creation.spec.TermSpec;
import edu.upc.imp.logics.services.creation.spec.VariableSpec;

import java.util.LinkedList;
import java.util.List;


public abstract class StringToTermSpecFactory {

    public List<TermSpec> createTermSpecs(String... termNames) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for (String name : termNames) {
            TermSpec termSpec = createTermSpec(name);

            termSpecList.add(termSpec);
        }
        return termSpecList;
    }


    public TermSpec createTermSpec(String name) {
        if (isConstant(name)) {
            return new ConstantSpec(name);
        } else if (isVariable(name)) {
            return new VariableSpec(name);
        } else {
            throw new RuntimeException("Unrecognized term name: " + name);
        }
    }

    protected abstract boolean isConstant(String name);

    protected abstract boolean isVariable(String name);
}
