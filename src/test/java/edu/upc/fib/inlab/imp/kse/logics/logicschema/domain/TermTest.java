package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.TermAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

class TermTest {

    @Nested
    class CreationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void should_throwException_whenNameIsNullOrEmpty(String nullOrEmpty) {
            assertThatThrownBy(() -> new Term(nullOrEmpty) {
                @Override
                public Term applySubstitution(Substitution substitution) {
                    return null;
                }

                @Override
                public <T> T accept(LogicSchemaVisitor<T> visitor) {
                    return null;
                }

            }).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_createTerm_whenNameIsNotNullNorEmpty() {
            assertThatCode(() -> new Term("x") {
                @Override
                public Term applySubstitution(Substitution substitution) {
                    return null;
                }

                @Override
                public <T> T accept(LogicSchemaVisitor<T> visitor) {
                    return null;
                }

            }).doesNotThrowAnyException();
        }
    }

    @Nested
    class SubstitutionTest {
        @Test
        void should_returnSameVariable_whenVariableIsNotMappedInSubstitution() {
            Variable term = new Variable("x");
            Substitution substitution = new Substitution();

            Term actual = term.applySubstitution(substitution);

            TermAssert.assertThat(actual).isVariable("x");
        }

        @Test
        void should_returnMappedVariable_whenVariableIsMapped() {
            Variable term = new Variable("x");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "y")
                    .build();

            Term actual = term.applySubstitution(substitution);

            TermAssert.assertThat(actual).isVariable("y");
        }

        @Test
        void should_returnSameConstant_evenWhenVariableWithSameName_isMappedInSubstitution() {
            Constant term = new Constant("X");
            Substitution substitution = new Substitution();
            substitution.addMapping(new Variable("X"), new Constant("1"));

            Term actual = term.applySubstitution(substitution);

            TermAssert.assertThat(actual).isConstant("X");
        }
    }

    @Nested
    class IsVariableTests {
        @Test
        void should_returnTrue_whenItIsVariable() {
            Term term = new Variable("v");
            assertThat(term.isVariable()).isTrue();
        }

        @Test
        void should_returnFalse_whenIsConstant() {
            Term term = new Constant("X");
            assertThat(term.isVariable()).isFalse();
        }
    }
}