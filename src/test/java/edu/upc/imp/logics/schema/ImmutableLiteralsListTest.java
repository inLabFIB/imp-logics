package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.utils.ImmutableLiteralsListMother;
import edu.upc.imp.logics.services.comparator.SubstitutionBuilder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ImmutableLiteralsListTest {

    @Nested
    class CreateImmutableLiteralsList {
        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithElementNull() {
            List<Literal> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableLiteralsList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithNull() {
            assertThatThrownBy(() -> new ImmutableLiteralsList((List<Literal>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_createImmutableLiteralsEmptyList() {
            assertThat(new ImmutableLiteralsList()).isEmpty();
        }

        @Test
        public void should_createEmptyLiteralList_whenCreatingImmutableLiteralList_withEmptyList() {
            ImmutableLiteralsList actualTermList = new ImmutableLiteralsList(List.of());
            ImmutableLiteralsListAssert.assertThat(actualTermList).isEmpty();
        }
    }

    @Test
    public void should_ReturnNewLiteralsList_WithReplacedTerms_WhenApplyingSubstitution() {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, y), R(y, z), x < y");
        Substitution substitution = new SubstitutionBuilder()
                .addMapping("x", "a")
                .build();

        ImmutableLiteralsList actualLiteralsList = immutableLiteralsList.applySubstitution(substitution);

        ImmutableLiteralsListAssert.assertThat(actualLiteralsList)
                .isNotSameAs(immutableLiteralsList)
                .hasSize(3)
                .containsOrdinaryLiteral("P", "a", "y")
                .containsOrdinaryLiteral("R", "y", "z")
                .containsComparisonBuiltInLiteral("a", "<", "y");
    }

    @Test
    public void should_ReturnUsedVariables() {
        ImmutableLiteralsList immutableLiteralsList = ImmutableLiteralsListMother.create("P(x, 1), R(y, 2), x < y");

        Set<Variable> usedVariables = immutableLiteralsList.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));

    }
}