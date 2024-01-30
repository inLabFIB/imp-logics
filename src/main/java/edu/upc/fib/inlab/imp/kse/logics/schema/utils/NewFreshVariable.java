package edu.upc.fib.inlab.imp.kse.logics.schema.utils;

import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;

import java.util.Set;

/**
 * @deprecated
 */
@Deprecated(forRemoval = true)
public class NewFreshVariable {

    private NewFreshVariable() {
        throw new IllegalStateException("Utility class");
    }

    @SuppressWarnings("unused")
    public static Variable computeNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        return NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables);
    }

    @SuppressWarnings("unused")
    public static Variable computeEnumeratedNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        return NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables);
    }

}
