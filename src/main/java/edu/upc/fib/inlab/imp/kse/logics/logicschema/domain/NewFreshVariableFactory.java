package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import java.util.Objects;
import java.util.Set;

/**
 * Utils class for generating new Variables.
 */
public class NewFreshVariableFactory {

    private NewFreshVariableFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Variable createNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        if (Objects.isNull(variableNamePrefix)) throw new IllegalArgumentException("VariableNamePrefix cannot be null");
        if (Objects.isNull(usedVariables)) throw new IllegalArgumentException("UsedVariables cannot be empty");

        StringBuilder newVariableNameBuilder = new StringBuilder();
        newVariableNameBuilder.append(variableNamePrefix);
        while (usedVariables.contains(new Variable(newVariableNameBuilder.toString()))) {
            newVariableNameBuilder.append("'");
        }
        return new Variable(newVariableNameBuilder.toString());
    }

    @SuppressWarnings("unused")
    public static Variable createEnumeratedNewFreshVariable(String variableNamePrefix, Set<Variable> usedVariables) {
        if (Objects.isNull(variableNamePrefix)) throw new IllegalArgumentException("VariableNamePrefix cannot be null");
        if (Objects.isNull(usedVariables)) throw new IllegalArgumentException("UsedVariables cannot be empty");

        int index = 0;
        String proposedNewVariableName = variableNamePrefix + index;
        while (usedVariables.contains(new Variable(proposedNewVariableName))) {
            proposedNewVariableName = variableNamePrefix + ++index;
        }
        return new Variable(proposedNewVariableName);
    }

}
