package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.ImmutableLiteralsListMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.assertions.SubstitutionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

class ExtendedHomomorphismFinderTest {

    @Test
    void should_findHomomorphism_whenDomainDerivedLiteralDerivationRule_IsHomomorphicToRangeDerivedLiteralDerivationRule() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived(x)",
                "Derived(x) :- Q(x,x)"
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived(a)",
                "Derived(y) :- Q(y,y)"
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isPresent();
        assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
    }

    @Test
    void should_findHomomorphism_whenDomainDerived_hasDifferentName_thanRangeDerivedLiteral() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived1(x)",
                "Derived1(x) :- Q(x,x)"
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived2(a)",
                "Derived2(y) :- Q(y,y)"
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isPresent();
        assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
    }

    @ParameterizedTest
    @MethodSource("provideLogicSchemasBaseFails")
    void should_notFindHomomorphism_whenDomainBaseLiteral_hasDifferentPredicateName_thanRangeBaseLiteral(ImmutableLiteralsList domainLiterals, ImmutableLiteralsList rangeLiterals) {
        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isNotPresent();
    }

    static Stream<Arguments> provideLogicSchemasBaseFails() {
        return Stream.of(
                Arguments.of(ImmutableLiteralsListMother.create("Base(x)"),
                        ImmutableLiteralsListMother.create("BaseFail(a)")),
                Arguments.of(ImmutableLiteralsListMother.create("""
                                        Base(x), Derived(x)
                                        """,
                                """
                                        Derived(x) :- Q(x,x)
                                        """),
                        ImmutableLiteralsListMother.create("""
                                BaseFail(a), Derived(a)
                                """, """
                                Derived(y) :- Q(y,y)
                                """))
        );
    }

    @Test
    void should_findHomomorphism_whenRangeDerivationRule_usesVariablesAppearingInMainRule() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived(x, y)",
                "Derived(x, y) :- Q(x,y)"
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived(a, b)",
                "Derived(b, a) :- Q(b,a)"
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isPresent();
        assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
        assertThat(substitutionOpt.get()).mapsToVariable("y", "b");
    }

    @Test
    void should_notFindHomomorphism_whenDomainDerivationRule_isDifferentThanRangeDerivationRule() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived(x, y)",
                "Derived(x, y) :- Q(x,y)"
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived(a, b)",
                "Derived(a, b) :- R(a,b)"
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isNotPresent();
    }

    @Test
    void should_findHomomorphism_whenDomainHasSeveralDerivationRules_includedInRange() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived(x, y)",
                """
                                Derived(x, y) :- Q(x,y)
                                Derived(x, y) :- R(x,y)
                        """
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived(a, b)",
                """
                        Derived(a, b) :- Q(a,b)
                        Derived(a, b) :- R(a,b)
                        Derived(a, b) :- S(a,b)
                         """
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isPresent();
        assertThat(substitutionOpt.get())
                .mapsToVariable("x", "a")
                .mapsToVariable("y", "b");
    }

    @Test
    void should_notFindHomomorphism_whenDomainHasSomeDerivationRules_notIncludedInRange() {
        ImmutableLiteralsList domainLiterals = ImmutableLiteralsListMother.create(
                "Base(x), Derived(x, y)",
                """
                                Derived(x, y) :- Q(x,y)
                                Derived(x, y) :- T(x,y)
                        """
        );
        ImmutableLiteralsList rangeLiterals = ImmutableLiteralsListMother.create(
                "Base(a), Derived(a, b)",
                """
                        Derived(a, b) :- Q(a,b)
                        Derived(a, b) :- R(a,b)
                        Derived(a, b) :- S(a,b)
                         """
        );

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphism(domainLiterals, rangeLiterals);

        assertThat(substitutionOpt).isNotPresent();
    }

}