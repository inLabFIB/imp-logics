package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.utils;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.NewFreshVariableFactory;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NewFreshVariableFactoryTest {

    @Nested
    class ParametersTest {
        @Test
        void createNewFreshVariableThrowsExceptionForNullUsedVariables() {
            String variableNamePrefix = "x";
            Set<Variable> usedVariables = null;

            assertThatThrownBy(() -> NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createNewFreshVariableThrowsExceptionForNullVariableNamePrefix() {
            String variableNamePrefix = null;
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("x"));

            assertThatThrownBy(() -> NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createEnumeratedNewFreshVariableThrowsExceptionForNullUsedVariables() {
            String variableNamePrefix = "x";
            Set<Variable> usedVariables = null;

            assertThatThrownBy(() -> NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void createEnumeratedNewFreshVariableThrowsExceptionForNullVariableNamePrefix() {
            String variableNamePrefix = null;
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("x"));

            assertThatThrownBy(() -> NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables))
                    .isInstanceOf(IllegalArgumentException.class);
        }


    }

    @Nested
    class CreateNewFreshVariableTest {

        @Test
        void createNewFreshVariableReturnsUniqueNameWhenUsedVariablesIsEmpty() {
            String variableNamePrefix = "prefix";
            Set<Variable> usedVariables = new LinkedHashSet<>();

            Variable result = NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables);

            assertThat(result.getName()).isEqualTo(variableNamePrefix);
        }

        @Test
        void createNewFreshVariableReturnsUniqueNameWhenUsedVariablesContainsPrefix() {
            String variableNamePrefix = "var";
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("var"));

            Variable result = NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables);

            assertThat(result.getName()).isNotEqualTo(variableNamePrefix);
            assertThat(result.getName()).startsWith(variableNamePrefix);
            assertThat(result.getName()).isEqualTo("var'");
        }

        @Test
        void createNewFreshVariableThrowsExceptionForNullPrefix() {
            String variableNamePrefix = null;
            Set<Variable> usedVariables = new LinkedHashSet<>();

            assertThatThrownBy(() -> NewFreshVariableFactory.createNewFreshVariable(variableNamePrefix, usedVariables))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    class CreateEnumeratedNewFreshVariableTest {

        @Test
        void createEnumeratedNewFreshVariableReturnsEnumeratedNameWhenUsedVariablesContainsVariablePrefix() {
            String variableNamePrefix = "x";
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("x"));
            usedVariables.add(new Variable("y"));

            Variable result1 = NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables);

            assertThat(result1.getName()).isEqualTo(variableNamePrefix + "0");
        }

        @Test
        void createEnumeratedNewFreshVariableReturnsUniqueNameWhenUsedVariablesNotContainsVariableName() {
            String variableNamePrefix = "var";
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("x"));
            usedVariables.add(new Variable("y"));

            Variable result1 = NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables);

            assertThat(result1.getName()).isEqualTo("var0");
        }

        @Test
        void createEnumeratedNewFreshVariableReturnsUniqueNameWhenUsedVariablesContainsVariableName() {
            String variableNamePrefix = "x";
            Set<Variable> usedVariables = new LinkedHashSet<>();
            usedVariables.add(new Variable("x0"));
            usedVariables.add(new Variable("x1"));

            Variable result1 = NewFreshVariableFactory.createEnumeratedNewFreshVariable(variableNamePrefix, usedVariables);

            assertThat(result1.getName()).isEqualTo("x2");
        }

    }
}