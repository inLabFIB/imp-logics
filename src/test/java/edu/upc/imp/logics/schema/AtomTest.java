package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.assertions.AtomAssert;
import edu.upc.imp.logics.schema.assertions.ImmutableLiteralsListAssert;
import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.utils.AtomMother;
import edu.upc.imp.logics.schema.utils.ImmutableLiteralsListMother;
import edu.upc.imp.logics.services.comparator.SubstitutionBuilder;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;
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
    }
}
