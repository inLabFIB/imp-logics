package edu.upc.fib.inlab.imp.kse.logics.schema.operations;

import edu.upc.fib.inlab.imp.kse.logics.schema.Constant;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.TermMother;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.SubstitutionBuilder;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.assertions.SubstitutionAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.SubstitutionException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SubstitutionTest {

    @Test
    public void should_containAllMappings_whenCopyingSubstitution() {
        Substitution originalSubstitution = new Substitution();
        originalSubstitution.addMapping(new Variable("x"), new Variable("y"));

        Substitution copiedSubstitution = new Substitution(originalSubstitution);

        SubstitutionAssert.assertThat(copiedSubstitution).hasSize(1);
        SubstitutionAssert.assertThat(copiedSubstitution).mapsToVariable("x", "y");
    }

    @Test
    public void should_throwException_whenCopyingNullSubstitution() {
        assertThatThrownBy(() -> new Substitution(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_whenCreatingSubstitution_withNullDomainTerms() {
        assertThatThrownBy(() -> new Substitution(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_whenCreatingSubstitution_withNullRangeTerms() {
        assertThatThrownBy(() -> new Substitution(List.of(), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_createEmptySubstitution_whenCreationSubstitution_withEmptyDomainTerms_andEmptyRangeTerms() {
        Substitution substitution = new Substitution(List.of(), List.of());
        SubstitutionAssert.assertThat(substitution).isEmpty();
    }

    @ParameterizedTest(name = "{index} {3}")
    @MethodSource("provideCompatibleDomainTermsAndRangeTerms")
    public void should_createSubstitution_whenUsingCompatibleTermsList(List<Term> domainTerms, List<Term> rangeTerms, Map<Variable, Term> expected, String message) {
        SubstitutionAssert.assertThat(new Substitution(domainTerms, rangeTerms)).describedAs(message).encodesMapping(expected);
    }

    public static Stream<Arguments> provideCompatibleDomainTermsAndRangeTerms() {
        return Stream.of(
                Arguments.of(
                        TermMother.createTerms("x", "y"),
                        TermMother.createTerms("1", "z"),
                        Map.of(
                                new Variable("x"), new Constant("1"),
                                new Variable("y"), new Variable("z")
                        ),
                        "Mapping vars to vars and constants"),
                Arguments.of(
                        TermMother.createTerms("x", "1"),
                        TermMother.createTerms("a", "1"),
                        Map.of(new Variable("x"), new Variable("a")),
                        "Mapping vars to vars and constant to same constant"),
                Arguments.of(
                        TermMother.createTerms("x", "y"),
                        TermMother.createTerms("a", "b"),
                        Map.of(
                                new Variable("x"), new Variable("a"),
                                new Variable("y"), new Variable("b")
                        ),
                        "Mapping vars to vars"),
                Arguments.of(
                        TermMother.createTerms("x", "y", "x"),
                        TermMother.createTerms("a", "b", "a"),
                        Map.of(
                                new Variable("x"), new Variable("a"),
                                new Variable("y"), new Variable("b")
                        ),
                        "Mapping vars to vars, repeating domain var")
        );
    }

    @ParameterizedTest(name = "{index} {2}")
    @MethodSource("provideIncompatibleDomainTermsAndRangeTerms")
    public void should_throwException_whenUsingIncompatibleTermsList(List<Term> domainTerms, List<Term> rangeTerms, String message) {
        assertThatThrownBy(() -> new Substitution(domainTerms, rangeTerms)).describedAs(message).isInstanceOf(SubstitutionException.class);
    }

    public static Stream<Arguments> provideIncompatibleDomainTermsAndRangeTerms() {
        return Stream.of(
                Arguments.of(
                        TermMother.createTerms("x", "x"),
                        TermMother.createTerms("x", "y"),
                        "Cannot map the same variable to different terms"),
                Arguments.of(
                        TermMother.createTerms("x", "1"),
                        TermMother.createTerms("x", "2"),
                        "Cannot map a constant to a different constant"),
                Arguments.of(
                        List.of(new Constant("x")),
                        List.of(new Variable("x")),
                        "Cannot map a constant to a variable with the same name"),
                Arguments.of(
                        TermMother.createTerms("x"),
                        TermMother.createTerms("x", "1"),
                        "Mismatch between list sizes")
        );

    }

    @Test
    public void should_containAllMappings_whenAddingMappings() {
        Substitution substitution = new Substitution();
        substitution.addMapping(new Variable("x"), new Variable("y"));
        substitution.addMapping(new Variable("y"), new Variable("z"));

        SubstitutionAssert.assertThat(substitution).hasSize(2);
        SubstitutionAssert.assertThat(substitution).mapsToVariable("x", "y");
        SubstitutionAssert.assertThat(substitution).mapsToVariable("y", "z");
    }

    @Test
    public void should_throwException_whenUnifyingSubstitution_withNullSubstitution() {
        Substitution substitution = new Substitution();
        assertThatThrownBy(() -> substitution.union(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_unifySubstitutions_whenBothSubstitutionsDoNotShareDomainVariables() {
        // {x->a} {y->b} OK {x->a, y->b}
        Substitution substitution1 = new SubstitutionBuilder().addMapping("x", "a").build();
        Substitution substitution2 = new SubstitutionBuilder().addMapping("y", "b").build();

        Substitution union = substitution1.union(substitution2);
        SubstitutionAssert.assertThat(union).hasSize(2);
        SubstitutionAssert.assertThat(union).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(union).mapsToVariable("y", "b");
    }

    @Test
    public void should_unifySubstitutions_whenBothSubstitutionsAreIdentical() {
        // {x->a} {x->a} OK {x->a}
        Substitution substitution1 = new SubstitutionBuilder().addMapping("x", "a").build();
        Substitution substitution2 = new SubstitutionBuilder().addMapping("x", "a").build();

        Substitution union = substitution1.union(substitution2);
        SubstitutionAssert.assertThat(union).hasSize(1);
        SubstitutionAssert.assertThat(union).mapsToVariable("x", "a");
    }

    @Test
    public void should_throwException_whenBothSubstitutionsShareDomainVariable() {
        // {x->a} {x->b} Not Ok
        Substitution substitution1 = new SubstitutionBuilder().addMapping("x", "a").build();
        Substitution substitution2 = new SubstitutionBuilder().addMapping("x", "b").build();

        assertThatThrownBy(() -> substitution1.union(substitution2))
                .isInstanceOf(SubstitutionException.class);
    }

    @Test
    public void should_throwException_whenAddingMapping_withNullDomainVariable() {
        Substitution substitution = new Substitution();
        assertThatThrownBy(() -> substitution.addMapping(null, new Variable("x")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_throwException_whenAddingMapping_withNullRangeTerm() {
        Substitution substitution = new Substitution();
        assertThatThrownBy(() -> substitution.addMapping(new Variable("x"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_containMapping_whenAddingMapping() {
        // {} add {x->1} OK
        Substitution substitution = new Substitution();
        substitution.addMapping(new Variable("x"), new Constant("1"));

        SubstitutionAssert.assertThat(substitution).hasSize(1);
        SubstitutionAssert.assertThat(substitution).mapsToConstant("x", "1");
    }

    @Test
    public void should_throwException_whenAddingMapping_withAlreadyMappedDomainVariable() {
        // {x -> 1} add {x -> 2} no ok
        Substitution substitution = new SubstitutionBuilder().addMapping("x", "1").build();
        assertThatThrownBy(() -> substitution.addMapping(new Variable("x"), new Constant("2")))
                .isInstanceOf(SubstitutionException.class);
    }

    @Test
    public void should_notAddMapping_whenAddingMapping_withExistingDomainVariableAndSameRangeTerm() {
        // {x -> 1} add {x -> 1} ok
        Substitution substitution = new SubstitutionBuilder().addMapping("x", "1").build();
        substitution.addMapping(new Variable("x"), new Constant("1"));

        SubstitutionAssert.assertThat(substitution).hasSize(1);
        SubstitutionAssert.assertThat(substitution).mapsToConstant("x", "1");
    }

    @Test
    public void should_throwException_whenAddingMapping_withExistingDomainVariable_mappedToDifferentTermKind_withSameName() {
        // {x -> Var(a)} add {x -> Const(a)} not ok
        Substitution substitution = new SubstitutionBuilder().addMapping("x", "a").build();
        assertThatThrownBy(() -> substitution.addMapping(new Variable("x"), new Constant("a")))
                .isInstanceOf(SubstitutionException.class);
    }

    @Test
    public void should_throwException_whenGettingNullTerm() {
        Substitution substitution = new Substitution();
        assertThatThrownBy(() -> substitution.getTerm(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_returnEmptyOptional_whenGettingTerm_withUnmappedVariable() {
        Substitution substitution = new Substitution();
        assertThat(substitution.getTerm(new Variable("x"))).isNotPresent();
    }

    @Test
    public void should_returnMappedTerm_whenGettingTerm_withMappedVariable() {
        Substitution substitution = new SubstitutionBuilder().addMapping("x", "a").build();
        assertThat(substitution.getTerm(new Variable("x"))).isPresent().contains(new Variable("a"));
    }

    @Test
    public void should_returnUsedVariables() {
        Substitution substitution = new SubstitutionBuilder().addMapping("x", "y").build();

        Set<Variable> usedVariables = substitution.getUsedVariables();

        assertThat(usedVariables).hasSize(2)
                .contains(new Variable("x"), new Variable("y"));
    }

    @Nested
    class IsIdentityTests {
        @Test
        public void should_returnTrue_whenSubstitutionIsEmpty() {
            Substitution originalSubstitution = new Substitution();

            assertThat(originalSubstitution.isIdentity()).isTrue();
        }

        @Test
        public void should_returnTrue_whenSubstitutionIsNonEmptyIdentity() {
            Substitution originalSubstitution = new Substitution();
            Variable b = new Variable("b");
            Variable anotherB = new Variable("b");
            originalSubstitution.addMapping(b, anotherB);

            assertThat(originalSubstitution.isIdentity()).isTrue();
        }

        @Test
        public void should_returnFalse_whenSubstitutionIsNotIdentity() {
            Substitution originalSubstitution = new Substitution();
            Variable b = new Variable("b");
            Variable anotherB = new Variable("b");
            Variable a = new Variable("a");
            originalSubstitution.addMapping(b, anotherB);
            originalSubstitution.addMapping(a, anotherB);

            assertThat(originalSubstitution.isIdentity()).isFalse();
        }
    }
}