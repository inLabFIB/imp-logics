package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers.TGDMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BooleanBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LiteralMother;
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

                Set<Variable> universalVars = new TGD(body, singleAtomHead).getUniversalVariables();

                assertThat(universalVars)
                        .hasSize(2)
                        .contains(new Variable("x"))
                        .contains(new Variable("y"));
            }

            @Test
            void should_returnEmptySet_whenGettingUniversalVariables_fromBody_withOnlyConstants() {
                List<Literal> body = List.of(
                        LiteralMother.createOrdinaryLiteral("P", "1"),
                        LiteralMother.createOrdinaryLiteral("Q", "2"),
                        LiteralMother.createOrdinaryLiteral("T", "3")
                );

                Set<Variable> universalVars = new TGD(body, singleAtomHead).getUniversalVariables();

                assertThat(universalVars).isEmpty();
            }
        }

        @Nested
        class ExistentialVariablesTest {
            @Test
            void shouldReturnEmptySet_whenGettingExistentialVariables_withTrivialEmptyHead() {
                List<Atom> head = List.of(AtomMother.createAtom("P"));

                Set<Variable> existentialVars = new TGD(trueBooleanBuiltInLiteralBody, head).getExistentialVariables();

                assertThat(existentialVars).isEmpty();
            }

            @Test
            void shouldReturnSet_whenGettingExistentialVariables_fromHead_withExistentialAndUniversalVariables() {
                List<Literal> body = List.of(LiteralMother.createOrdinaryLiteral("P", "x"));
                List<Atom> head = List.of(AtomMother.createAtom("P", "x", "y"));

                Set<Variable> existentialVars = new TGD(body, head).getExistentialVariables();

                assertThat(existentialVars)
                        .isNotEmpty()
                        .contains(new Variable("y"))
                        .doesNotContain(new Variable("x"));
            }

            @Test
            void shouldReturnSet_whenGettingExistentialVariables_fromHead_withExistentialVariableAndConstant() {
                List<Atom> head = List.of(AtomMother.createAtom("P", "y", "4"));

                Set<Variable> existentialVars = new TGD(trueBooleanBuiltInLiteralBody, head).getExistentialVariables();

                assertThat(existentialVars)
                        .isNotEmpty()
                        .contains(new Variable("y"));
            }
        }

        @Nested
        class FrontierVariablesTest {
            @Test
            void shouldReturnFrontierVariables_whenTGDHasOneHeadAtom() {
                TGD tgd = TGDMother.createTGD("P(u1, u2, f1, f2), Q(u2, u3, f2, f3) -> R(f1, f2, f3, e1)");
                Set<Variable> frontierVars = tgd.getFrontierVariables();

                assertThat(frontierVars)
                        .hasSize(3)
                        .contains(new Variable("f1"))
                        .contains(new Variable("f2"))
                        .contains(new Variable("f3"));
            }

            @Test
            void shouldReturnFrontierVariables_whenTGDHasSeveralHeadAtoms() {
                TGD tgd = TGDMother.createTGD("P(u1, u2, f1, f2), Q(u2, u3, f2, f3) -> R1(f1, e1), R2(f2, f3)");
                Set<Variable> frontierVars = tgd.getFrontierVariables();

                assertThat(frontierVars)
                        .hasSize(3)
                        .contains(new Variable("f1"))
                        .contains(new Variable("f2"))
                        .contains(new Variable("f3"));
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
                    LiteralMother.createOrdinaryLiteral("P", "1"),
                    LiteralMother.createOrdinaryLiteral("Q", "2"),
                    LiteralMother.createOrdinaryLiteral("T", "3")
            );

            boolean isLinear = new TGD(multipleLiteralBody, singleAtomHead).isLinear();

            assertThat(isLinear).isFalse();
        }
    }

    @Nested
    class GuardedTGDCheckTests {

        /**
         * Guard is S(x,y,z)
         */
        @Test
        void shouldReturnTrue_whenCheckingIfGuarded_withTDGWithLiteralContainingAllUniversalVariables() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("R", "x", "y"),
                    LiteralMother.createOrdinaryLiteral("S", "x", "y", "z")
            );
            List<Atom> head = List.of(AtomMother.createAtom("P", "x", "z", "w"));

            boolean isGuarded = new TGD(body, head).isGuarded();

            assertThat(isGuarded).isTrue();
        }

        @Test
        void shouldReturnFalse_whenCheckingIfGround_withTGDWithoutLiteralContainingAllUniversalVariables() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("R", "x", "y"),
                    LiteralMother.createOrdinaryLiteral("R", "y", "z")
            );
            List<Atom> head = List.of(AtomMother.createAtom("R", "x", "z"));

            boolean isGuarded = new TGD(body, head).isGuarded();

            assertThat(isGuarded).isFalse();
        }

        @Test
        void shouldReturnTrue_whenCheckingIfGround_withTGDWithoutUniversalVariables() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("P", "1"),
                    LiteralMother.createOrdinaryLiteral("Q", "2")
            );

            boolean isGuarded = new TGD(body, singleAtomHead).isGuarded();

            assertThat(isGuarded).isTrue();
        }
    }
}