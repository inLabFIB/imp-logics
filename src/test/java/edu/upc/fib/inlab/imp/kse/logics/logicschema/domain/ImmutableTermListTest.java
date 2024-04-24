package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.ImmutableTermListAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.TermMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImmutableTermListTest {

    @Test
    void should_ReturnUsedVariables() {
        ImmutableTermList immutableTermList = TermMother.createTerms("x", "1", "y");

        Set<Variable> usedVariables = immutableTermList.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }

    @Nested
    class CreationTests {
        @Test
        void should_throwException_whenCreatingImmutableTermsList_withNullList() {
            assertThatThrownBy(() -> new ImmutableTermList((List<Term>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenTryCreatingImmutableTermsList_withNullElement() {
            List<Term> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableTermList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_createEmptyTermList_whenCreatingImmutableTermList_withEmptyList() {
            ImmutableTermList actualTermList = new ImmutableTermList(List.of());
            ImmutableTermListAssert.assertThat(actualTermList).isEmpty();
        }

        @Test
        void should_containAllTermsInSameOrder_whenCreatingTermList() {
            ImmutableTermList actualTermList = new ImmutableTermList(new Variable("x"), new Constant("1"));
            ImmutableTermListAssert.assertThat(actualTermList)
                    .containsVariable(0, "x")
                    .containsConstant(1, "1");
        }
    }

    @Nested
    class ApplySubstitution {
        @Test
        void should_returnOtherImmutableTermList_whenApplyingSubstitution() {
            ImmutableTermList immutableTermList = TermMother.createTerms("x", "1");
            Substitution substitution = new Substitution();

            ImmutableTermList actualTermList = immutableTermList.applySubstitution(substitution);

            assertThat(actualTermList).isNotSameAs(immutableTermList);
        }

        @Test
        void should_returnTermsList_afterReplacingMappedVariables() {
            ImmutableTermList immutableTermList = TermMother.createTerms("x", "1");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "2")
                    .build();

            ImmutableTermList actualTermList = immutableTermList.applySubstitution(substitution);

            ImmutableTermListAssert.assertThat(actualTermList)
                    .hasSize(2)
                    .containsConstant(0, "2")
                    .containsConstant(1, "1");
        }
    }

    @Nested
    class EqualsTest {

        private static Stream<Arguments> notEqualsTerms() {
            return Stream.of(
                    Arguments.of("Terms in different order",
                                 TermMother.createTerms("x", "y"),
                                 TermMother.createTerms("y", "x")
                    ),
                    Arguments.of("Terms List with different size (size1 > size2)",
                                 TermMother.createTerms("x", "y"),
                                 TermMother.createTerms("x", "y", "z")
                    ),
                    Arguments.of("Terms List with different size (size1 < size2)",
                                 TermMother.createTerms("x", "y", "z"),
                                 TermMother.createTerms("x", "y")
                    ),
                    Arguments.of("With constants in list 2",
                                 TermMother.createTerms("x", "y"),
                                 TermMother.createTerms("x", "1")
                    ),
                    Arguments.of("With constants in list 1",
                                 TermMother.createTerms("x", "1"),
                                 TermMother.createTerms("x", "y")
                    )
            );
        }

        @Test
        void should_returnTrue_whenTermsAreEquals() {
            ImmutableTermList immutableTermList1 = TermMother.createTerms("x", "1", "y");
            ImmutableTermList immutableTermList2 = TermMother.createTerms("x", "1", "y");

            boolean equals = immutableTermList1.equals(immutableTermList2);

            assertThat(equals).isTrue();
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("notEqualsTerms")
        void should_returnFalse_whenTermsAreNotEquals(String description, ImmutableTermList immutableTermList1, ImmutableTermList immutableTermList2) {
            boolean equals = immutableTermList1.equals(immutableTermList2);

            assertThat(equals).as(description).isFalse();
        }

    }
}