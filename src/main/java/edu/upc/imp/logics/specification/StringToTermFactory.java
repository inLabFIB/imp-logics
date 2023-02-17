package edu.upc.imp.logics.specification;

import java.util.LinkedList;
import java.util.List;


public abstract class StringToTermFactory {

    public List<TermSpec> createTerms(String[] termNames) {
        List<TermSpec> termSpecList = new LinkedList<>();
        for(String name: termNames) {
            if (isConstant(name)) {
                termSpecList.add(new ConstantSpec(name));
            }
            else if (isVariable(name)) {
                termSpecList.add(new VariableSpec(name));
            }
            else {
                throw new RuntimeException("Unrecognized term name: "+name);
            }
        }
        return termSpecList;
    }


    protected abstract boolean isConstant(String name);
    protected abstract boolean isVariable(String name);
}
