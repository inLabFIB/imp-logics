package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.Constant;
import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;
import edu.upc.imp.logics.services.comparator.assertions.SubstitutionAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class SubstitutionTest {


    @Test
    public void should_containAllMappings_whenCopyingSubstitution() {
        Substitution originalSubstitution = new Substitution();
        originalSubstitution.addMappingIfNotIncluded(new Variable("x"), new Variable("y"));

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
        SubstitutionAssert.assertThat(new Substitution(domainTerms, rangeTerms)).encodesMapping(expected);
    }

    public static Stream<Arguments> provideCompatibleDomainTermsAndRangeTerms() {
        return Stream.of(
                Arguments.of(
                        List.of(new Variable("x"), new Variable("y")),
                        List.of(new Constant("1"), new Variable("z")),
                        Map.of(new Variable("x"), new Constant("1"), new Variable("y"), new Variable("z")),
                        "Mapping vars to vars and constants"),
                Arguments.of(
                        List.of(new Variable("x"), new Constant("1")),
                        List.of(new Variable("a"), new Constant("1")),
                        Map.of(new Variable("x"), new Variable("a")),
                        "Mapping vars to vars and constant to same constant"),
                Arguments.of(
                        List.of(new Variable("x"), new Variable("y")),
                        List.of(new Variable("a"), new Variable("b")),
                        Map.of(new Variable("x"), new Variable("a"), new Variable("y"), new Variable("b")),
                        "Mapping vars to vars"),
                Arguments.of(
                        List.of(new Variable("x"), new Variable("y"), new Variable("x")),
                        List.of(new Variable("a"), new Variable("b"), new Variable("a")),
                        Map.of(new Variable("x"), new Variable("a"), new Variable("y"), new Variable("b")),
                        "Mapping vars to vars, repeating domain var")
        );
    }

    @ParameterizedTest(name = "{index} {2}")
    @MethodSource("provideIncompatibleDomainTermsAndRangeTerms")
    public void should_throwException_whenUsingIncompatibleTermsList(List<Term> domainTerms, List<Term> rangeTerms, String message) {
        assertThatThrownBy(() -> new Substitution(domainTerms, rangeTerms)).isInstanceOf(SubstitutionException.class);
    }

    public static Stream<Arguments> provideIncompatibleDomainTermsAndRangeTerms() {
        return Stream.of(
                Arguments.of(
                        List.of(new Variable("x"), new Variable("x")),
                        List.of(new Variable("x"), new Variable("y")),
                        "Cannot map the same variable to different terms"),
                Arguments.of(
                        List.of(new Variable("x"), new Constant("1")),
                        List.of(new Variable("x"), new Constant("2")),
                        "Cannot map a constant to a different constant"),
                Arguments.of(
                        List.of(new Constant("x")),
                        List.of(new Variable("x")),
                        "Cannot map a constant to a variable with the same name"),
                Arguments.of(
                        List.of(new Variable("x")),
                        List.of(new Variable("x"), new Constant("1")),
                        "Mismatch between list sizes")
        );

    }

    @Test
    public void should_containAllMappings_whenAddingMappings() {
        Substitution substitution = new Substitution();
        substitution.addMappingIfNotIncluded(new Variable("x"), new Variable("y"));
        substitution.addMappingIfNotIncluded(new Variable("y"), new Variable("z"));

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
        Substitution substitution1 = new Substitution(List.of(new Variable("x")), List.of(new Variable("a")));
        Substitution substitution2 = new Substitution(List.of(new Variable("y")), List.of(new Variable("b")));
        Substitution union = substitution1.union(substitution2);
        SubstitutionAssert.assertThat(union).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(union).mapsToVariable("y", "b");
        SubstitutionAssert.assertThat(union).hasSize(2);
    }


    // {x->a} {x->a} OK {x->a}
    // {x->a} {x->b} Not Ok

}