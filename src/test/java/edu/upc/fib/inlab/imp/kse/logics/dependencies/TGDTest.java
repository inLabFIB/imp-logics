package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TGDTest {

    private final List<Literal> trueBooleanBuiltInLiteralBody = List.of(
            new BooleanBuiltInLiteral(true)
    );

    private final List<Atom> singleAtomHead = List.of(
            AtomMother.createAtom("head_predicate")
    );

    @Nested
    class CreationTests {

        private final List<Literal> defaultBody = List.of(
                LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x"))
        );

        @Test
        void should_throwException_whenCreatingATGD_withNullHead() {
            assertThatThrownBy(() -> new TGD(defaultBody, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenCreatingATGD_withEmptyHead() {
            List<Atom> head = List.of();
            assertThatThrownBy(() -> new TGD(defaultBody, head)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_makeHeadImmutable_whenCreatingATGD_withMutableListInput() {
            Atom atom = AtomMother.createAtom("P", "x");
            List<Atom> head = createMutableAtomList(atom);
            TGD dependency = new TGD(defaultBody, head);

            assertThat(dependency.getHead()).isUnmodifiable();
        }

        private static List<Atom> createMutableAtomList(Atom... atoms) {
            return new LinkedList<>(List.of(atoms));
        }
    }

    @Nested
    class VariableTypesTests {

        @Nested
        class UniversalVariableTests {
            /**
             * Test case: <code>p(x) -> p()</code>
             */
            @Test
            void should_getUniversalVariables() {
                List<Literal> body = List.of(LiteralMother.createOrdinaryLiteral("P", "x"));
                List<Atom> head = List.of(AtomMother.createAtom("P"));

                Set<Variable> universalVars = new TGD(body, head).getUniversalVariables();

                assertThat(universalVars)
                        .isNotEmpty()
                        .contains(new Variable("x"));
            }

            @Test
            void should_getUniversalVariables_fromBody_withSeveralLiterals() {
                List<Literal> body = List.of(
                        LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")),
                        LiteralMother.createOrdinaryLiteralWithVariableNames("Q", List.of("x")),
                        LiteralMother.createOrdinaryLiteralWithVariableNames("T", List.of("y"))
                );
                List<Atom> head = List.of(AtomMother.createAtom("P"));

                Set<Variable> universalVars = new TGD(body, head).getUniversalVariables();

                assertThat(universalVars)
                        .hasSize(2)
                        .contains(new Variable("x"))
                        .contains(new Variable("y"));
            }

            @Test
            void should_returnEmptySet_whenGettingUniversalVariables_fromBody_withOnlyConstants() {
                List<Literal> body = List.of(
                        LiteralMother.createOrdinaryLiteral("P", List.of(new Constant("1"))),
                        LiteralMother.createOrdinaryLiteral("Q", List.of(new Constant("2"))),
                        LiteralMother.createOrdinaryLiteral("T", List.of(new Constant("3")))
                );
                List<Atom> head = List.of(AtomMother.createAtom("P"));

                Set<Variable> universalVars = new TGD(body, head).getUniversalVariables();

                assertThat(universalVars).isEmpty();
            }
        }

        @Nested
        class ExistentialVariablesTest {
            @Test
            void getExistentialVariablesFromTrivialHead() {
                List<Atom> head = List.of(AtomMother.createAtom("P"));

                Set<Variable> existentialVars = new TGD(trueBooleanBuiltInLiteralBody, head).getExistentialVariables();

                assertThat(existentialVars).isEmpty();
            }

            @Test
            void getExistentialVariablesFromHeadWithExistentialAndUniversalVariables() {
                List<Literal> body = List.of(LiteralMother.createOrdinaryLiteral("P", "x"));
                List<Atom> head = List.of(AtomMother.createAtom("P", "x", "y"));

                Set<Variable> existentialVars = new TGD(body, head).getExistentialVariables();

                assertThat(existentialVars)
                        .isNotEmpty()
                        .contains(new Variable("y"))
                        .doesNotContain(new Variable("x"));
            }

            @Test
            void getExistentialVariablesFromHeadWithExistentialVariableAndConstant() {
                List<Atom> head = List.of(AtomMother.createAtom("P", "y", "4"));

                Set<Variable> existentialVars = new TGD(trueBooleanBuiltInLiteralBody, head).getExistentialVariables();

                assertThat(existentialVars)
                        .isNotEmpty()
                        .contains(new Variable("y"))
                        .doesNotContain(new Variable("4"));
            }
        }
    }

    @Nested
    class LinearTGDCheckTests {

        @Test
        void shouldReturnTrue_whenCheckingIfLinear_withTGDWithASingleBodyLiteral() {
            List<Literal> singleLiteralBody = List.of(LiteralMother.createOrdinaryLiteral("P"));

            boolean isLinear = new TGD(singleLiteralBody, singleAtomHead).isLinear();

            assertThat(isLinear).isTrue();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfLinear_withTGDWithMultipleLiteralsInBody() {
            List<Literal> multipleLiteralBody = List.of(
                    LiteralMother.createOrdinaryLiteral("P", List.of(new Constant("1"))),
                    LiteralMother.createOrdinaryLiteral("Q", List.of(new Constant("2"))),
                    LiteralMother.createOrdinaryLiteral("T", List.of(new Constant("3")))
            );
            List<Atom> head = List.of(AtomMother.createAtom("P"));

            boolean isLinear = new TGD(multipleLiteralBody, head).isLinear();

            assertThat(isLinear).isFalse();
        }

        @Test
        void linearTGDShouldAlwaysBeGuardedAsWell() {
            List<Atom> head = List.of(AtomMother.createAtom("P", "x"));

            assertThat(new TGD(trueBooleanBuiltInLiteralBody, head).isLinear()).isTrue();
            assertThat(new TGD(trueBooleanBuiltInLiteralBody, head).isGuarded()).isTrue();
        }
    }

    @Nested
    class GuardedTGDCheckTests {

        @Test
        void guardedTGDShouldBeDetected_CheckShouldReturnTrue() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("R", List.of(
                            new Variable("x"),
                            new Variable("y")
                    )),
                    LiteralMother.createOrdinaryLiteral("S", List.of(
                            new Variable("y"),
                            new Variable("x"),
                            new Variable("z")
                    ))
            );
            List<Atom> head = List.of(AtomMother.createAtom("P", "z", "x", "w"));

            assertThat(new TGD(body, head).isGuarded()).isTrue();
        }

        @Test
        void nonGuardedTGDShouldBeDetected_CheckShouldReturnFalse() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("R", List.of(
                            new Variable("x"),
                            new Variable("y")
                    )),
                    LiteralMother.createOrdinaryLiteral("R", List.of(
                            new Variable("y"),
                            new Variable("z")
                    ))
            );
            List<Atom> head = List.of(AtomMother.createAtom("R", "x", "z"));

            assertThat(new TGD(body, head).isGuarded()).isFalse();
        }

        @Test
            //És aquest el comportament que desitgem? Segons la formalització del paper sembla que si.
        void checkingGuardednessWhenNoVariablesArePresent_CheckShouldReturnTrue() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("P", List.of(new Constant("1"))),
                    LiteralMother.createOrdinaryLiteral("Q", List.of(new Constant("2"))),
                    LiteralMother.createOrdinaryLiteral("T", List.of(new Constant("3")))
            );
            List<Atom> head = List.of(AtomMother.createAtom("P"));

            assertThat(new TGD(body, head).isGuarded()).isTrue();
        }

        @Test
        void guardedTGDNotAlwaysShouldBeLinear() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("P", List.of(new Constant("x"))),
                    LiteralMother.createOrdinaryLiteral("Q", List.of(new Constant("x"))),
                    LiteralMother.createOrdinaryLiteral("T", List.of(new Constant("x")))
            );
            List<Atom> head = List.of(AtomMother.createAtom("P"));

            assertThat(new TGD(body, head).isGuarded()).isTrue();
            assertThat(new TGD(body, head).isLinear()).isFalse();
        }
    }
}