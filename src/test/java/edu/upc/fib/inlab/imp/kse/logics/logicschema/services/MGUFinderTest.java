package edu.upc.fib.inlab.imp.kse.logics.logicschema.services;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.AtomAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.ImmutableTermListAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.TermMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.assertions.SubstitutionAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MGUFinderTest {

    @Nested
    class AtomsTests {
        @Nested
        class UnifyTwoAtoms {

            private static Stream<Arguments> provideUnifiableTermsLists() {
                return Stream.of(
                        Arguments.of("Terms containing equal constants",
                                     TermMother.createTerms("x", "1"),
                                     TermMother.createTerms("a", "1"),
                                     TermMother.createTerms("x", "1")
                        ),
                        Arguments.of("Terms containing variable unifiable with constants, twice",
                                     TermMother.createTerms("x", "x", "y"),
                                     TermMother.createTerms("1", "1", "z"),
                                     TermMother.createTerms("1", "1", "a")
                        ),
                        Arguments.of("Terms containing variables unifiable with constants",
                                     TermMother.createTerms("x", "y"),
                                     TermMother.createTerms("a", "1"),
                                     TermMother.createTerms("xa", "1")
                        ),
                        Arguments.of("Terms containing same variable names in different positions",
                                     TermMother.createTerms("x", "y"),
                                     TermMother.createTerms("y", "x"),
                                     TermMother.createTerms("xy", "yx")
                        ),
                        Arguments.of("Terms containing repeated variables in same position",
                                     TermMother.createTerms("x", "y"),
                                     TermMother.createTerms("a", "a"),
                                     TermMother.createTerms("a", "a")
                        ),
                        Arguments.of("Terms containing repeated variables using same names",
                                     TermMother.createTerms("x", "y"),
                                     TermMother.createTerms("x", "x"),
                                     TermMother.createTerms("x", "x")
                        ),
                        Arguments.of("Repeating different variables in different positions",
                                     TermMother.createTerms("x", "y", "x"),
                                     TermMother.createTerms("a", "b", "b"),
                                     TermMother.createTerms("x", "x", "x")
                        ),
                        Arguments.of("Combining variable repetitions and constants",
                                     TermMother.createTerms("x", "y", "x", "x"),
                                     TermMother.createTerms("a", "b", "b", "1"),
                                     TermMother.createTerms("1", "1", "1", "1")
                        ),
                        Arguments.of("Combining variable repetitions and constants reusing variable names",
                                     TermMother.createTerms("x", "y", "x", "x"),
                                     TermMother.createTerms("y", "x", "x", "1"),
                                     TermMother.createTerms("1", "1", "1", "1")
                        )
                );
            }

            private static Stream<Arguments> provideNonUnifiableTermsLists() {
                return Stream.of(
                        Arguments.of("Terms containing different constants",
                                     TermMother.createTerms("x", "2"),
                                     TermMother.createTerms("a", "1")
                        ),
                        Arguments.of("Combining variable repetitions and constants",
                                     TermMother.createTerms("x", "x"),
                                     TermMother.createTerms("2", "1")
                        ),
                        Arguments.of("Combining variable repetitions and constants",
                                     TermMother.createTerms("x", "2", "x"),
                                     TermMother.createTerms("b", "b", "1")
                        )
                );
            }

            @Test
            void should_notReturnMGU_WhenAtomsHaveDifferentPredicates() {
                Predicate pPred = new Predicate("P", 1);
                Predicate qPred = new Predicate("Q", 1);
                Atom pAtom = new Atom(pPred, TermMother.createTerms("x"));
                Atom qAtom = new Atom(qPred, TermMother.createTerms("x"));
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom, qAtom);
                assertThat(mgu).isNotPresent();
            }

            @Test
            void should_notReturnMGU_whenAtomsHaveDifferentPredicates_withSameName() {
                Predicate pPred1 = new Predicate("P", 1);
                Predicate pPred2 = new Predicate("P", 1);
                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x"));
                Atom pAtom2 = new Atom(pPred2, TermMother.createTerms("x"));
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2);
                assertThat(mgu).isNotPresent();
            }

            @Test
            void should_returnMGU_whenTermsAreEmpty() {
                Predicate pPred1 = new Predicate("P", 0);

                Atom pAtom1 = new Atom(pPred1, List.of());
                Atom pAtom2 = new Atom(pPred1, List.of());
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2);
                assertThat(mgu).isPresent();
                SubstitutionAssert.assertThat(mgu.get()).isEmpty();
            }

            /**
             * Happy path test
             */
            @Test
            void should_returnMGU_whenAtomsHaveAllDifferentVariables() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "z");
                Atom pAtom1 = new Atom(pPred1, terms1);
                ImmutableTermList terms2 = TermMother.createTerms("a", "b", "c");
                Atom pAtom2 = new Atom(pPred1, terms2);
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2);
                assertThat(mgu).isPresent();

                AtomAssert.assertThat(pAtom1.applySubstitution(mgu.get()))
                        .hasTerms(pAtom2.applySubstitution(mgu.get()).getTerms());
                SubstitutionAssert.assertThat(mgu.get()).mapsToDifferentVariables(terms1.getUsedVariables());
                SubstitutionAssert.assertThat(mgu.get()).mapsToDifferentVariables(terms2.getUsedVariables());
            }

            /**
             * Corner cases tests
             */
            @ParameterizedTest(name = "{0}. Terms1 = {1}; Terms2 = {2}")
            @MethodSource("provideUnifiableTermsLists")
            void should_returnMGU_whenTermsAreUnifiable(@SuppressWarnings("unused") String name, ImmutableTermList terms1, ImmutableTermList terms2, ImmutableTermList expectedTermsList) {
                Predicate pPred = new Predicate("P", terms1.size());

                Atom atom1 = new Atom(pPred, terms1);
                Atom atom2 = new Atom(pPred, terms2);

                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(atom1, atom2);
                assertThat(mgu).isPresent();

                AtomAssert.assertThat(atom1.applySubstitution(mgu.get()))
                        .hasTerms(atom2.applySubstitution(mgu.get()).getTerms());
                ImmutableTermListAssert.assertThat(terms1.applySubstitution(mgu.get())).isIsomorphicTo(expectedTermsList);
                ImmutableTermListAssert.assertThat(terms2.applySubstitution(mgu.get())).isIsomorphicTo(expectedTermsList);
            }

            @ParameterizedTest(name = "{0}. Terms1 = {1}; Terms2 = {2}")
            @MethodSource("provideNonUnifiableTermsLists")
            void should_notReturnMGU_whenTermsAreNotUnifiable(@SuppressWarnings("unused") String name, ImmutableTermList terms1, ImmutableTermList terms2) {
                Predicate pPred = new Predicate("P", terms1.size());

                Atom atom1 = new Atom(pPred, terms1);
                Atom atom2 = new Atom(pPred, terms2);

                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(atom1, atom2);
                assertThat(mgu).isNotPresent();
            }
        }

        @Nested
        class UnifySeveralAtoms {

            @Test
            void should_returnMGU_whenAtomsListIsEmpty() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                Atom pAtom1 = new Atom(pPred1, terms1);

                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1);
                assertThat(mgu).isPresent();

                SubstitutionAssert.assertThat(mgu.get()).mapsToDifferentVariables(terms1.getUsedVariables());
            }

            @Test
            void should_returnMGU_whenAtomsAreUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                Atom pAtom1 = new Atom(pPred1, terms1);
                ImmutableTermList terms2 = TermMother.createTerms("a", "b", "c");
                Atom pAtom2 = new Atom(pPred1, terms2);
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                Atom pAtom3 = new Atom(pPred1, terms3);
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2, pAtom3);
                assertThat(mgu).isPresent();

                ImmutableTermList expectedTermsList = TermMother.createTerms("a", "a", "a");
                AtomAssert.assertThat(pAtom1.applySubstitution(mgu.get()))
                        .hasTerms(pAtom2.applySubstitution(mgu.get()).getTerms());
                AtomAssert.assertThat(pAtom1.applySubstitution(mgu.get()))
                        .hasTerms(pAtom3.applySubstitution(mgu.get()).getTerms());
                ImmutableTermListAssert.assertThat(terms1.applySubstitution(mgu.get()))
                        .isIsomorphicTo(expectedTermsList);
            }

            @Test
            void should_notReturnMGU_whenTermsAreUnifiable_butFromDifferentPredicates() {
                Predicate pPred1 = new Predicate("P1", 3);
                Predicate pPred2 = new Predicate("P2", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                Atom pAtom1 = new Atom(pPred1, terms1);
                ImmutableTermList terms2 = TermMother.createTerms("x", "y", "a");
                Atom pAtom2 = new Atom(pPred1, terms2);
                ImmutableTermList terms3 = TermMother.createTerms("x", "y", "a");
                Atom pAtom3 = new Atom(pPred2, terms3);
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2, pAtom3);
                assertThat(mgu).isNotPresent();
            }

            @Test
            void should_notReturnMGU_whenTermsAreNotUnifiable() {
                Predicate pPred1 = new Predicate("P1", 3);
                Predicate pPred2 = new Predicate("P1", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "x");
                Atom pAtom1 = new Atom(pPred1, terms1);
                ImmutableTermList terms2 = TermMother.createTerms("x", "y", "1");
                Atom pAtom2 = new Atom(pPred1, terms2);
                ImmutableTermList terms3 = TermMother.createTerms("2", "y", "x");
                Atom pAtom3 = new Atom(pPred2, terms3);
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(pAtom1, pAtom2, pAtom3);
                assertThat(mgu).isNotPresent();
            }
        }

        @Nested
        class UnifyAtomsList {
            @Test
            void should_returnMGU_whenListIsUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d"));

                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(List.of(pAtom1, pAtom2, pAtom3));
                assertThat(mgu).isPresent();

                Atom unifiedAtom1 = pAtom1.applySubstitution(mgu.get());
                Atom unifiedAtom2 = pAtom2.applySubstitution(mgu.get());
                Atom unifiedAtom3 = pAtom3.applySubstitution(mgu.get());

                AtomAssert.assertThat(unifiedAtom1.applySubstitution(mgu.get()))
                        .hasTerms(unifiedAtom2.getTerms().applySubstitution(mgu.get()))
                        .hasTerms(unifiedAtom3.getTerms().applySubstitution(mgu.get()));

                ImmutableTermList expected = TermMother.createTerms("a", "a", "a", "b");
                ImmutableTermListAssert.assertThat(unifiedAtom1.getTerms())
                        .isIsomorphicTo(expected);
            }

            @Test
            void should_notReturnMGU_whenListIsNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "3", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "2", "x", "d"));

                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(List.of(pAtom1, pAtom2, pAtom3));
                assertThat(mgu).isEmpty();
            }

            @Test
            void should_notReturnMGU_whenAtomsIsNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                Atom lit1 = new Atom(pPred1, terms1);
                ImmutableTermList terms2 = TermMother.createTerms("1", "2", "c");
                Atom lit2 = new Atom(pPred1, terms2);
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                Atom lit3 = new Atom(pPred1, terms3);
                Optional<Substitution> mgu = MGUFinder.getAtomsMGU(List.of(lit1, lit2, lit3));
                assertThat(mgu).isNotPresent();
            }
        }

        @Nested
        class UnifiableAtoms {
            @Test
            void should_returnTrue_whenListIsUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d"));

                boolean isUnifiable = MGUFinder.areAtomsUnifiable(List.of(pAtom1, pAtom2, pAtom3));
                assertThat(isUnifiable).isTrue();
            }

            @Test
            void should_returnTrue_whenAtomsAreUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d"));

                boolean isUnifiable = MGUFinder.areAtomsUnifiable(pAtom1, pAtom2, pAtom3);
                assertThat(isUnifiable).isTrue();
            }

            @Test
            void should_returnFalse_whenAtomsAreNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "2", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "z", "3", "d"));

                boolean isUnifiable = MGUFinder.areAtomsUnifiable(pAtom1, pAtom2, pAtom3);
                assertThat(isUnifiable).isFalse();
            }

            @Test
            void should_returnFalse_whenListIsNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                Atom pAtom1 = new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b"));
                Atom pAtom2 = new Atom(pPred1, TermMother.createTerms("z", "2", "y", "b"));
                Atom pAtom3 = new Atom(pPred1, TermMother.createTerms("y", "z", "3", "d"));

                boolean isUnifiable = MGUFinder.areAtomsUnifiable(List.of(pAtom1, pAtom2, pAtom3));
                assertThat(isUnifiable).isFalse();
            }
        }
    }

    @Nested
    class LiteralsTests {
        @Nested
        class UnifyTwoLiterals {

            @Test
            void should_notUnify_TwoDifferentKindsOfLiterals() {
                OrdinaryLiteral ol1 = new OrdinaryLiteral(new Atom(new Predicate("P", 2), TermMother.createTerms("x", "x")));
                CustomBuiltInLiteral cust2 = new CustomBuiltInLiteral("Q", TermMother.createTerms("1", "2"));

                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(ol1, cust2);
                assertThat(mgu).isEmpty();
            }

            @Nested
            class UnifyTwoOrdinaryLiterals {
                @Test
                void should_notReturnMGU_WhenLiteralPolarityIsDifferent() {
                    Predicate pPred1 = new Predicate("P", 3);

                    ImmutableTermList terms1 = TermMother.createTerms("x", "y", "z");
                    Literal literal1 = new OrdinaryLiteral(new Atom(pPred1, terms1), true);
                    Literal literal2 = new OrdinaryLiteral(new Atom(pPred1, terms1), false);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(literal1, literal2);
                    assertThat(mgu).isEmpty();
                }

                @Test
                void should_returnMGU_whenAtomsAreUnifiable() {
                    Predicate pPred1 = new Predicate("P", 3);

                    ImmutableTermList terms1 = TermMother.createTerms("x", "y", "z");
                    Literal literal1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                    Literal literal2 = new OrdinaryLiteral(new Atom(pPred1, terms1));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(literal1, literal2);
                    assertThat(mgu).isPresent();

                    SubstitutionAssert.assertThat(mgu.get()).mapsToDifferentVariables(terms1.getUsedVariables());
                }

                @Test
                void should_notReturnMGU_whenAtomsAreNotUnifiable() {
                    Predicate pPred1 = new Predicate("P", 3);
                    Predicate qPred1 = new Predicate("Q", 3);

                    ImmutableTermList terms1 = TermMother.createTerms("x", "y", "z");
                    Literal literal1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                    Literal literal2 = new OrdinaryLiteral(new Atom(qPred1, terms1));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(literal1, literal2);
                    assertThat(mgu).isEmpty();
                }
            }

            @Nested
            class UnifyTwoBooleanBuiltInLiterals {
                @ParameterizedTest
                @ValueSource(booleans = {true, false})
                void should_returnEmptyMGU_whenBothBooleanBuiltInAreTheSame(boolean b) {
                    Literal literal1 = new BooleanBuiltInLiteral(b);
                    Literal literal2 = new BooleanBuiltInLiteral(b);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(literal1, literal2);
                    assertThat(mgu).isPresent();
                    SubstitutionAssert.assertThat(mgu.get()).isEmpty();
                }

                @ParameterizedTest
                @ValueSource(booleans = {true, false})
                void should_notReturnMGU_whenBothBooleanBuiltInAreDifferent(boolean b) {
                    Literal literal1 = new BooleanBuiltInLiteral(b);
                    Literal literal2 = new BooleanBuiltInLiteral(!b);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(literal1, literal2);
                    assertThat(mgu).isEmpty();
                }
            }

            @Nested
            class UnifyTwoComparisonBuiltInLiterals {
                @ParameterizedTest
                @EnumSource
                void should_returnMGU_whenComparatorIsTheSame_AndTermsAreUnifiable(ComparisonOperator operator) {
                    ComparisonBuiltInLiteral comp1 = new ComparisonBuiltInLiteral(new Variable("x"), new Variable("y"), operator);
                    ComparisonBuiltInLiteral comp2 = new ComparisonBuiltInLiteral(new Variable("a"), new Variable("b"), operator);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(comp1, comp2);
                    assertThat(mgu).isPresent();
                    SubstitutionAssert.assertThat(mgu.get())
                            .mapsToDifferentVariables(comp1.getUsedVariables())
                            .mapsToDifferentVariables(comp2.getUsedVariables());
                }

                @Test
                void should_notReturnMGU_whenComparatorIsDifferent() {
                    ComparisonBuiltInLiteral comp1 = new ComparisonBuiltInLiteral(new Variable("x"), new Variable("y"), ComparisonOperator.EQUALS);
                    ComparisonBuiltInLiteral comp2 = new ComparisonBuiltInLiteral(new Variable("a"), new Variable("b"), ComparisonOperator.LESS_THAN);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(comp1, comp2);
                    assertThat(mgu).isEmpty();
                }

                @ParameterizedTest
                @EnumSource
                void should_notReturnMGU_whenComparatorIsTheSame_butTermsAreNotUnifiable(ComparisonOperator operator) {
                    ComparisonBuiltInLiteral comp1 = new ComparisonBuiltInLiteral(new Variable("x"), new Variable("x"), operator);
                    ComparisonBuiltInLiteral comp2 = new ComparisonBuiltInLiteral(new Constant("1"), new Constant("2"), operator);

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(comp1, comp2);
                    assertThat(mgu).isEmpty();
                }

            }

            @Nested
            class UnifyTwoCustomBuiltInLiterals {
                @Test
                void should_returnMGU_whenOperatorIsTheSame_AndTermsAreUnifiable() {
                    CustomBuiltInLiteral cust1 = new CustomBuiltInLiteral("P", TermMother.createTerms("x", "y"));
                    CustomBuiltInLiteral cust2 = new CustomBuiltInLiteral("P", TermMother.createTerms("a", "b"));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(cust1, cust2);
                    assertThat(mgu).isPresent();
                    SubstitutionAssert.assertThat(mgu.get())
                            .mapsToDifferentVariables(cust1.getUsedVariables())
                            .mapsToDifferentVariables(cust2.getUsedVariables());
                }

                @Test
                void should_notReturnMGU_whenOperatorIsDifferent() {
                    CustomBuiltInLiteral cust1 = new CustomBuiltInLiteral("P", TermMother.createTerms("x", "y"));
                    CustomBuiltInLiteral cust2 = new CustomBuiltInLiteral("Q", TermMother.createTerms("a", "b"));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(cust1, cust2);
                    assertThat(mgu).isEmpty();
                }

                @Test
                void should_notReturnMGU_whenOperatorIsTheSame_butTermsAreNotUnifiable() {
                    CustomBuiltInLiteral cust1 = new CustomBuiltInLiteral("P", TermMother.createTerms("x", "x"));
                    CustomBuiltInLiteral cust2 = new CustomBuiltInLiteral("Q", TermMother.createTerms("1", "2"));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(cust1, cust2);
                    assertThat(mgu).isEmpty();
                }

                @Test
                void should_notReturnMGU_whenOperatorIsTheSame_butTermsSizeIsDifferent() {
                    CustomBuiltInLiteral cust1 = new CustomBuiltInLiteral("P", TermMother.createTerms("x", "x"));
                    CustomBuiltInLiteral cust2 = new CustomBuiltInLiteral("P", TermMother.createTerms("x"));

                    Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(cust1, cust2);
                    assertThat(mgu).isEmpty();
                }

            }

        }

        @Nested
        class UnifySeveralLiterals {
            @Test
            void should_returnMGU_whenLiteralsAreUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                OrdinaryLiteral lit1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                ImmutableTermList terms2 = TermMother.createTerms("a", "b", "c");
                OrdinaryLiteral lit2 = new OrdinaryLiteral(new Atom(pPred1, terms2));
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                OrdinaryLiteral lit3 = new OrdinaryLiteral(new Atom(pPred1, terms3));
                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(lit1, lit2, lit3);
                assertThat(mgu).isPresent();

                ImmutableTermList expectedTermsList = TermMother.createTerms("a", "a", "a");
                LiteralAssert.assertThat(lit1.applySubstitution(mgu.get()))
                        .asOrdinaryLiteral()
                        .hasTerms(lit2.applySubstitution(mgu.get()).getTerms());
                LiteralAssert.assertThat(lit1.applySubstitution(mgu.get()))
                        .asOrdinaryLiteral()
                        .hasTerms(lit3.applySubstitution(mgu.get()).getTerms());
                ImmutableTermListAssert.assertThat(terms1.applySubstitution(mgu.get()))
                        .isIsomorphicTo(expectedTermsList);
            }

            @Test
            void should_notReturnMGU_whenLiteralsAreNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                OrdinaryLiteral lit1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                ImmutableTermList terms2 = TermMother.createTerms("1", "2", "c");
                OrdinaryLiteral lit2 = new OrdinaryLiteral(new Atom(pPred1, terms2));
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                OrdinaryLiteral lit3 = new OrdinaryLiteral(new Atom(pPred1, terms3));
                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(lit1, lit2, lit3);
                assertThat(mgu).isNotPresent();
            }
        }

        @Nested
        class UnifyLiteralsList {
            @Test
            void should_returnEmptyMGU_whenListIsEmpty() {
                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(List.of());
                assertThat(mgu).isPresent();
                SubstitutionAssert.assertThat(mgu.get()).isEmpty();
            }

            @Test
            void should_returnMGU_whenListIsSingleton() {
                Predicate pPred1 = new Predicate("P", 3);
                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                OrdinaryLiteral lit1 = new OrdinaryLiteral(new Atom(pPred1, terms1));

                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(List.of(lit1));
                assertThat(mgu).isPresent();
                SubstitutionAssert.assertThat(mgu.get()).mapsToDifferentVariables(lit1.getUsedVariables());
            }

            @Test
            void should_returnMGU_whenLiteralsAreUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                OrdinaryLiteral lit1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                ImmutableTermList terms2 = TermMother.createTerms("a", "b", "c");
                OrdinaryLiteral lit2 = new OrdinaryLiteral(new Atom(pPred1, terms2));
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                OrdinaryLiteral lit3 = new OrdinaryLiteral(new Atom(pPred1, terms3));
                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(List.of(lit1, lit2, lit3));
                assertThat(mgu).isPresent();

                ImmutableTermList expectedTermsList = TermMother.createTerms("a", "a", "a");
                LiteralAssert.assertThat(lit1.applySubstitution(mgu.get()))
                        .asOrdinaryLiteral()
                        .hasTerms(lit2.applySubstitution(mgu.get()).getTerms());
                LiteralAssert.assertThat(lit1.applySubstitution(mgu.get()))
                        .asOrdinaryLiteral()
                        .hasTerms(lit3.applySubstitution(mgu.get()).getTerms());
                ImmutableTermListAssert.assertThat(terms1.applySubstitution(mgu.get()))
                        .isIsomorphicTo(expectedTermsList);
            }

            @Test
            void should_notReturnMGU_whenLiteralsAreNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 3);

                ImmutableTermList terms1 = TermMother.createTerms("x", "y", "a");
                OrdinaryLiteral lit1 = new OrdinaryLiteral(new Atom(pPred1, terms1));
                ImmutableTermList terms2 = TermMother.createTerms("1", "2", "c");
                OrdinaryLiteral lit2 = new OrdinaryLiteral(new Atom(pPred1, terms2));
                ImmutableTermList terms3 = TermMother.createTerms("d", "d", "f");
                OrdinaryLiteral lit3 = new OrdinaryLiteral(new Atom(pPred1, terms3));
                Optional<Substitution> mgu = MGUFinder.getLiteralsMGU(List.of(lit1, lit2, lit3));
                assertThat(mgu).isNotPresent();
            }
        }

        @Nested
        class UnifiableLiterals {
            @Test
            void should_returnTrue_whenListIsUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                OrdinaryLiteral pLit1 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b")));
                OrdinaryLiteral pLit2 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b")));
                OrdinaryLiteral pLit3 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d")));

                boolean isUnifiable = MGUFinder.areLiteralsUnifiable(List.of(pLit1, pLit2, pLit3));
                assertThat(isUnifiable).isTrue();
            }

            @Test
            void should_returnTrue_whenAtomsAreUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                OrdinaryLiteral pLit1 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b")));
                OrdinaryLiteral pLit2 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("z", "x", "y", "b")));
                OrdinaryLiteral pLit3 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d")));

                boolean isUnifiable = MGUFinder.areLiteralsUnifiable(pLit1, pLit2, pLit3);
                assertThat(isUnifiable).isTrue();
            }

            @Test
            void should_returnFalse_whenAtomsAreNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                OrdinaryLiteral pLit1 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b")));
                OrdinaryLiteral pLit2 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("1", "x", "2", "b")));
                OrdinaryLiteral pLit3 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d")));

                boolean isUnifiable = MGUFinder.areLiteralsUnifiable(pLit1, pLit2, pLit3);
                assertThat(isUnifiable).isFalse();
            }

            @Test
            void should_returnFalse_whenListIsNotUnifiable() {
                Predicate pPred1 = new Predicate("P", 4);

                OrdinaryLiteral pLit1 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("x", "y", "z", "b")));
                OrdinaryLiteral pLit2 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("1", "x", "2", "b")));
                OrdinaryLiteral pLit3 = new OrdinaryLiteral(new Atom(pPred1, TermMother.createTerms("y", "z", "x", "d")));

                boolean isUnifiable = MGUFinder.areLiteralsUnifiable(List.of(pLit1, pLit2, pLit3));
                assertThat(isUnifiable).isFalse();
            }
        }

    }

}