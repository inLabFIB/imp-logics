package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableTermListAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.TermMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ImmutableTermListTest {

    @Nested
    class CreationTests {
        @Test
        public void should_throwException_whenCreatingImmutableTermsList_withNullList() {
            assertThatThrownBy(() -> new ImmutableTermList((List<Term>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenTryCreatingImmutableTermsList_withNullElement() {
            List<Term> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableTermList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_createEmptyTermList_whenCreatingImmutableTermList_withEmptyList() {
            ImmutableTermList actualTermList = new ImmutableTermList(List.of());
            ImmutableTermListAssert.assertThat(actualTermList).isEmpty();
        }

        @Test
        public void should_containAllTermsInSameOrder_whenCreatingTermList() {
            ImmutableTermList actualTermList = new ImmutableTermList(new Variable("x"), new Constant("1"));
            ImmutableTermListAssert.assertThat(actualTermList)
                    .containsVariable(0, "x")
                    .containsConstant(1, "1");
        }

        @Test
        public void should_throwException_whenCreatingImmutableTermsList_withSomeNull() {
            assertThatThrownBy(() -> new ImmutableTermList(new Variable("x"), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class ApplySubstitution {
        @Test
        public void should_returnOtherImmutableTermList_whenApplyingSubstitution() {
            ImmutableTermList immutableTermList = TermMother.createTerms("x", "1");
            Substitution substitution = new Substitution();

            ImmutableTermList actualTermList = immutableTermList.applySubstitution(substitution);

            assertThat(actualTermList).isNotSameAs(immutableTermList);
        }

        @Test
        public void should_returnTermsList_afterReplacingMappedVariables() {
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

    @Test
    public void should_ReturnUsedVariables() {
        ImmutableTermList immutableTermList = TermMother.createTerms("x", "1", "y");

        Set<Variable> usedVariables = immutableTermList.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }

}