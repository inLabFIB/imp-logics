package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.AtomAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.ArityMismatch;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.ImmutableLiteralsListMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.HomomorphismFinder;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AtomTest {

    @Nested
    class CreationTests {
        @Test
        public void should_ThrowException_WhenCreatingAtomWithNullPredicate() {
            assertThatThrownBy(() -> new Atom(null, List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_ThrowException_WhenCreatingAtomWithNullListOfTerms() {
            assertThatThrownBy(() -> new Atom(new MutablePredicate("P", 0), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_MakeTermsListImmutable_WhenCreatingTheAtom() {
            List<Term> terms = new LinkedList<>();
            terms.add(new Variable("x"));
            Atom atom = new Atom(new MutablePredicate("P", 1), terms);
            assertThat(atom.getTerms()).isUnmodifiable();
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
        public void should_ThrowException_WhenCreatingAtomWithWrongArity(int arity, List<Term> terms) {
            assertThatThrownBy(() -> new Atom(new MutablePredicate("P", arity), terms))
                    .isInstanceOf(ArityMismatch.class);
        }
    }

    @Test
    public void should_ReturnNewAtomReplacingTerms_WhenApplyingSubstitution() {
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

    @Nested
    class UnfoldTests {
        @Test
        public void should_ReturnLiteralListWithThisAtom_WhenPredicateIsBase() {
            Atom atom = AtomMother.createAtomWithVariableNames("P", List.of("x"));

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(1)
                    .containsOrdinaryLiteral("P", "x");
        }

        @Test
        public void should_ReturnSingleLiteralsList_WhenPredicateHasUniqueDefinitionRule() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, z)");
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        public void should_ReturnSeveralLiteralsList_WhenPredicateHasUniqueDefinitionRule() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                        P(x, y) :- R(x, y), S(y, z)
                        P(x, y) :- R(x, y), T(y, z)
                    """);
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            assertThat(unfoldedAtom).hasSize(2);
            ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("R(a, b), S(b, z)");
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList1)
                    .containsOrdinaryLiteral("R", "a", "b");
            ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("R(a, b), T(b, z)");
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(1))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList2)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        public void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms() {
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("P(x, y) :- R(x, y), S(y, a, b)");
            Atom atom = AtomMother.createAtom(logicSchema, "P", "a", "b");

            List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

            ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("R(a, b), S(b, z, w)");
            assertThat(unfoldedAtom).hasSize(1);
            ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                    .hasSize(2)
                    .isLogicallyEquivalentTo(expectedLiteralsList)
                    .containsOrdinaryLiteral("R", "a", "b");
        }

        @Test
        public void should_ReturnLiteralsList_ReplacingTerms_WhenDefinitionRuleTermsClashes_WithThisTerms_evenInTheHead() {
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
            public void should_obtainOneRule_whenThereIsOneDerivationRule() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("R(a, 1) :- T(a, b)");
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList = ImmutableLiteralsListMother.create("T(x, b), y=1");
                assertThat(unfoldedAtom).hasSize(1);
                ImmutableLiteralsListAssert.assertThat(unfoldedAtom.get(0))
                        .hasSize(2)
                        .isLogicallyEquivalentTo(expectedLiteralsList)
                        .containsOrdinaryLiteral("T", "x", "b");
            }

            @Test
            public void should_obtainTwoRules_whenThereAreTwoRules_EachOneWithDifferentConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                                        R(a, 2) :- TT(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, y), y=1");
                ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("TT(x, y), y=2");
                assertThat(unfoldedAtom).hasSize(2)
                        .anyMatch(literals -> isEquivalentWithSameVariables(literals, expectedLiteralsList1, "x", "y"))
                        .anyMatch(literals -> isEquivalentWithSameVariables(literals, expectedLiteralsList2, "x", "y"));
            }

            @Test
            public void should_obtainTwoRules_whenThereAreTwoRules_WithOnlyOneWithConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                                        R(a, b) :- TT(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "y");


                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();

                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, y), y=1");
                ImmutableLiteralsList expectedLiteralsList2 = ImmutableLiteralsListMother.create("TT(x, y)");

                assertThat(unfoldedAtom).hasSize(2)
                        .anyMatch(literals -> isEquivalentWithSameVariables(literals, expectedLiteralsList1, "x", "y"))
                        .anyMatch(literals -> isEquivalentWithSameVariables(literals, expectedLiteralsList2, "x", "y"));
            }

            @Test
            public void should_obtainOneRuleWithContradictoryBuiltIn_whenThereIsOneRule_NotMatchingConstants() {
                LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse("""
                                        R(a, 1) :- T(a, b)
                        """);
                Atom atom = AtomMother.createAtom(logicSchema, "R", "x", "2");

                List<ImmutableLiteralsList> unfoldedAtom = atom.unfold();
                ImmutableLiteralsList expectedLiteralsList1 = ImmutableLiteralsListMother.create("T(x, y), 2=1");

                assertThat(unfoldedAtom).hasSize(1)
                        .anyMatch(literals -> isEquivalentWithSameVariables(literals, expectedLiteralsList1, "x"));
            }


        }
    }

    private static boolean isEquivalentWithSameVariables(ImmutableLiteralsList expectedLiteralsList1, ImmutableLiteralsList expectedLiteralsList2, String... varNames) {
        Optional<Substitution> homomorphism = new HomomorphismFinder().findHomomorphism(expectedLiteralsList1, expectedLiteralsList2);
        Optional<Substitution> homomorphismRespectingVariables = homomorphism.filter(substitution -> {
            for (String varName : varNames) {
                Optional<Term> subsituttedTerm = substitution.getTerm(new Variable(varName));
                Optional<Term> termSubstitutedToDifferentVariable = subsituttedTerm.filter(replacedTerm ->
                        !(replacedTerm.isVariable() && replacedTerm.getName().equals(varName))
                );
                if (termSubstitutedToDifferentVariable.isPresent()) {
                    return false;
                }
            }
            return true;
        });
        return homomorphismRespectingVariables.isPresent();
    }
}
