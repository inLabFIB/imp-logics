package edu.upc.fib.inlab.imp.kse.logics.schema.utils;

import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;

import java.util.Set;

public class NewFreshVariable {

    public static Variable computeNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        String proposedNewVariableName = variableNamePrefix;
        while (usedVariables.contains(new Variable(proposedNewVariableName))) {
            proposedNewVariableName = proposedNewVariableName + "'";
        }
        return new Variable(proposedNewVariableName);
    }
}
