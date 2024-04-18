package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.exceptions.IMPLogicsException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LiteralMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.TermMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LiteralTest {

    @Test
    void should_ReturnUsedVariables() {
        Literal literal = LiteralMother.createOrdinaryLiteral("P", "x", "1", "y");

        Set<Variable> usedVariables = literal.getUsedVariables();

        assertThat(usedVariables)
                .hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }

    @Test
    void should_returnCanBeNegated_ifBuildNegationIsImplemented() {
        Literal literal = createLiteralWithBuildNegatedLiteralFunction();
        assertThat(literal.canBeNegated()).isTrue();
    }

    @Test
    void should_returnCannotBeNegated_ifBuildNegationIsNotImplemented() {
        Literal literal = createLiteralWithoutBuildNegatedLiteralFunction();
        assertThat(literal.canBeNegated()).isFalse();
    }


    @ParameterizedTest
    @MethodSource("providedTermsAndArity")
    void should_returnArity_whenLiteralContainTerms(List<String> terms, int expectedArity) {
        Literal literal = LiteralMother.createOrdinaryLiteralWithVariableNames("P", terms);

        int arity = literal.getArity();

        assertThat(arity)
                .describedAs("Literal Arity")
                .isEqualTo(expectedArity);
    }

    static Stream<Arguments> providedTermsAndArity() {
        return Stream.of(
                Arguments.of(List.of("x", "y", "z"), 3),
                Arguments.of(List.of("x", "y"), 2),
                Arguments.of(List.of("x"), 1),
                Arguments.of(List.of(), 0)
        );
    }

    private static Literal createLiteralWithoutBuildNegatedLiteralFunction() {
        return new Literal() {
            @Override
            public ImmutableTermList getTerms() {
                return null;
            }

            @Override
            public Literal applySubstitution(Substitution substitution) {
                return null;
            }

            @Override
            public <T> T accept(LogicSchemaVisitor<T> visitor) {
                return null;
            }
        };
    }

    private static Literal createLiteralWithBuildNegatedLiteralFunction() {
        return new Literal() {
            @Override
            public ImmutableTermList getTerms() {
                return null;
            }

            @Override
            public Literal applySubstitution(Substitution substitution) {
                return null;
            }

            @Override
            public <T> T accept(LogicSchemaVisitor<T> visitor) {
                return null;
            }

            @Override
            public Literal buildNegatedLiteral() {
                return new BooleanBuiltInLiteral(true);
            }
        };
    }

    @Nested
    class GroundTests {

        @Test
        void should_beGround_whenAllTermsAreConstants() {
            List<Term> terms = TermMother.createTerms("1.0", "2");
            Literal literal = new FakeLiteral(terms);
            assertThat(literal.isGround()).isTrue();
        }

        @Test
        void should_notBeGround_whenAnyTermIsNotConstant() {
            List<Term> terms = TermMother.createTerms("x", "1.0");
            Literal literal = new FakeLiteral(terms);
            assertThat(literal.isGround()).isFalse();
        }

    }

    private static class FakeLiteral extends Literal {
        private final List<Term> terms;

        public FakeLiteral(List<Term> terms) {
            super();
            this.terms = terms;
        }

        @Override
        public ImmutableTermList getTerms() {
            return new ImmutableTermList(terms);
        }

        @Override
        public Literal applySubstitution(Substitution substitution) {
            throw new IMPLogicsException("Not should be called");
        }

        @Override
        public <T> T accept(LogicSchemaVisitor<T> visitor) {
            throw new IMPLogicsException("Not should be called");
        }
    }
}
