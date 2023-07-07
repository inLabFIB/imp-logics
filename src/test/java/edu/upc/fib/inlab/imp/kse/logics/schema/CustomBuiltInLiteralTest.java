package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

public class CustomBuiltInLiteralTest {
    @Nested
    class CreationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void should_throwException_when_OperationNameIsEmptyOrNull(String nullOrEmptyName) {
            List<Term> terms = List.of(new Constant("1"));
            assertThatThrownBy(() -> new CustomBuiltInLiteral(nullOrEmptyName, terms))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_when_TermsListContainsNull() {
            List<Term> terms = new LinkedList<>();
            terms.add(null);

            assertThatThrownBy(() -> new CustomBuiltInLiteral("CustomBuiltIn", terms))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_notThrowException_when_TermsListIsEmpty() {
            List<Term> terms = new LinkedList<>();

            assertThatNoException().isThrownBy(() -> new CustomBuiltInLiteral("CustomBuiltIn", terms));
        }

        @Test
        public void should_MakeTermsListImmutable() {
            List<Term> terms = new LinkedList<>();
            terms.add(new Variable("x"));
            CustomBuiltInLiteral customBuiltIn = new CustomBuiltInLiteral("CustomBuiltIn", terms);

            Assertions.assertThat(customBuiltIn.getTerms()).isUnmodifiable();
        }
    }

    @Nested
    class ApplySubstitution {

        @Test
        public void should_returnNewLiteralWithSubstitutedTerms_WhenApplyingSubstitution() {
            CustomBuiltInLiteral customBuiltInLiteral = LiteralMother.createCustomBuiltInLiteral("CustomBuiltIn", "x");
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("x", "1")
                    .build();

            CustomBuiltInLiteral newLiteral = customBuiltInLiteral.applySubstitution(substitution);

            LiteralAssert.assertThat(newLiteral)
                    .isNotSameAs(customBuiltInLiteral)
                    .hasBuiltInCustomOperation("CustomBuiltIn")
                    .hasConstant(0, "1");
        }
    }
}
