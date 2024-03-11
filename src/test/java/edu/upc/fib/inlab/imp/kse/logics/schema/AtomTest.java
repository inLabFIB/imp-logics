package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.AtomAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.ArityMismatch;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AtomTest {

    @Nested
    class CreationTests {
        @Test
        void should_ThrowException_WhenCreatingAtomWithNullPredicate() {
            assertThatThrownBy(() -> new Atom(null, List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_ThrowException_WhenCreatingAtomWithNullListOfTerms() {
            assertThatThrownBy(() -> new Atom(new MutablePredicate("P", 0), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_MakeTermsListImmutable_WhenCreatingTheAtom() {
            List<Term> terms = new LinkedList<>();
            terms.add(new Variable("x"));
            Atom atom = new Atom(new MutablePredicate("P", 1), terms);
            Assertions.assertThat(atom.getTerms()).isUnmodifiable();
        }

        private static Stream<Arguments> provideWrongAritiesAndLists() {
            return Stream.of(
                    Arguments.of(0, List.of(new Constant("1"))),
                    Arguments.of(1, List.of()),
                    Arguments.of(2, List.of(new Constant("1"))),
                    Arguments.of(1, List.of(new Constant("1"), new Constant("2")))
            );
        }

        @ParameterizedTest
        @MethodSource("provideWrongAritiesAndLists")
        void should_ThrowException_WhenCreatingAtomWithWrongArity(int arity, List<Term> terms) {
            assertThatThrownBy(() -> new Atom(new MutablePredicate("P", arity), terms))
                    .isInstanceOf(ArityMismatch.class);
        }
    }

    @Nested
    class SubstitutionTests {

        @Test
        void should_ReturnNewAtomReplacingTerms_WhenApplyingSubstitution() {
            Atom atom = AtomMother.createAtomWithVariableNames("P", List.of("a", "b"));
            Substitution substitution = new SubstitutionBuilder()
                    .addMapping("a", "1")
                    .addMapping("b", "c")
                    .build();

            Atom replacedAtom = atom.applySubstitution(substitution);

            AtomAssert.assertThat(replacedAtom)
                    .isNotSameAs(atom)
                    .hasPredicate("P", 2)
                    .containsConstant(0, "1")
                    .hasVariable(1, "c");
        }
    }

    @Nested
    class GroundTests {

        @Test
        void should_beGround_whenAllTermsAreConstants() {
            Atom atom = AtomMother.createAtom("P", "1", "2.0");

            assertThat(atom.isGround()).isTrue();
        }

        @Test
        void should_notBeGround_whenAnyTermIsNotConstant() {
            Atom atom = AtomMother.createAtom("P", "a", "b", "1");
            assertThat(atom.isGround()).isFalse();
        }

    }

    @Nested
    class UnfoldTests {
        @Test
        void should_ReturnLiteralListWithThisAtom_WhenPredicateIsBase() {
            Atom atom = AtomMother.createAtomWithVariableNames("P", List.of("x"));

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(1)
                    .containsOrdinaryLiteral("P", "x");
        }

        @Test
        void should_ReturnSingleLiteralsList_WhenPredicateHasUniqueDefinitionRule() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, z)");
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList, "a", "b");
        }

        @Test
        void should_ReturnSeveralLiteralsList_WhenPredicateHasUniqueDefinitionRule() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                        P(x, y) :- R(x, y), S(y, z)
                        P(x, y) :- R(x, y), T(y, z)
                    """);
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            assertThat(unfoldedAtom).hasSize(2);
            ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList1, "a", "b");
            ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("R(a, b), T(b, z)");
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(1))
                    .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList2, "a", "b");
        }

        @Test
        void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, a, b)");
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(b, z, w)");
            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList, "a", "b");
        }

        @Test
        void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms_evenInTheHead() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(b, a) :- R(b, a)");
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(1)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Nested
        class UnfoldingWithConstantsInHeadTests {
            @Test
            void should_obtainOneRule_whenThereIsOneDerivationRule() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("R(a, 1) :- T(a, b)");
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("T(x, b), y=1");
                assertThat(unfoldedAtom).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                        .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList, "x", "y");
            }

            @Test
            void should_obtainTwoRules_whenThereAreTwoRules_EachOneWithDifferentConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                                        R(a, 2) :- TT(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, b), y=1");
                ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("TT(x, b), y=2");
                assertThat(unfoldedAtom).hasSize(2)
                        .anySatisfy(literals -> ImmutableLiteralsListAssert.assertThat(literals)
                                .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList1, "x", "y"))
                        .anySatisfy(literals -> ImmutableLiteralsListAssert.
                                assertThat(literals)
                                .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList2, "x", "y"));
            }

            @Test
            void should_obtainTwoRules_whenThereAreTwoRules_WithOnlyOneWithConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                                        R(a, b) :- TT(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");


                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, b), y=1");
                ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("TT(x, y)");

                assertThat(unfoldedAtom).hasSize(2)
                        .anySatisfy(literals -> ImmutableLiteralsListAssert.assertThat(literals)
                                .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList1, "x", "y"))
                        .anySatisfy(literals -> ImmutableLiteralsListAssert.assertThat(literals)
                                .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList2, "x", "y"));
            }

            @Test
            void should_obtainOneRuleWithContradictoryBuiltIn_whenThereIsOneRule_NotMatchingConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "2");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();
                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, y), 2=1");

                assertThat(unfoldedAtom).hasSize(1)
                        .anySatisfy(literals -> ImmutableLiteralsListAssert.assertThat(literals)
                                .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList1, "x"));
            }
        }

        @Nested
        class UnfoldingWithRepeatedVariablesInHeadTests {
            @Test
            void should_addBuiltInLiteral_whenFindingRepeatedVariablesInHead() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("R(a, b, a) :- T(a, b)");
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y", "z");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("T(x, y), x=z");
                assertThat(unfoldedAtom).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                        .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList, "x", "y");
            }

            @Test
            void should_addBuiltInLiteral_whenFindingRepeatedVariablesInHead_andAtomHasRepeatedVariables() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("R(a, a, b) :- T(a, b)");
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y", "y");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("T(x, y), x=y");
                assertThat(unfoldedAtom).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                        .isIsomorphicToWithoutReplacingVariables(expectedLiteralsList, "x", "y");
            }
        }
    }


}
