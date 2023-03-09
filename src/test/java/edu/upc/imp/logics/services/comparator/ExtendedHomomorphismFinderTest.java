package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.ConstraintID;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.LogicSchema;
import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.services.comparator.assertions.SubstitutionAssert;
import edu.upc.imp.logics.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendedHomomorphismFinderTest {

    @Test
    public void should_findHomomorphism_whenDomainDerivedLiteralDerivationRule_IsHomomorphicToRangeDerivedLiteralDerivationRule() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived(x)
                Derived(x) :- Q(x,x)
                """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2 :- Base(a), Derived(a)
                Derived(y) :- Q(y,y)
                """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isPresent();
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
    }

    @Test
    public void should_findHomomorphism_whenDomainDerived_hasDifferentName_thanRangeDerivedLiteral() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived1(x)
                Derived1(x) :- Q(x,x)
                """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2 :- Base(a), Derived2(a)
                Derived2(y) :- Q(y,y)
                """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isPresent();
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
    }

    @ParameterizedTest
    @MethodSource("provideLogicSchemasBaseFails")
    public void should_notFindHomomorphism_whenDomainBaseLiteral_hasDifferentPredicateName_thanRangeBaseLiteral(String domainLogicSchemaString, String rangeLogicSchemaString) {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse(domainLogicSchemaString);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse(rangeLogicSchemaString);

        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isNotPresent();
    }

    public static Stream<Arguments> provideLogicSchemasBaseFails() {
        return Stream.of(
                Arguments.of("""
                                @1 :- Base(x)
                                """,
                        """
                                @2 :- BaseFail(a)
                                """),
                Arguments.of("""
                                @1 :- Base(x), Derived(x)
                                Derived(x) :- Q(x,x)
                                """,
                        """
                                @2 :- BaseFail(a), Derived(a)
                                Derived(y) :- Q(y,y)
                                """)
        );
    }

    @Test
    public void should_findHomomorphism_whenRangeDerivationRule_usesVariablesAppearingInMainRule() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived(x, y)
                Derived(x, y) :- Q(x,y)
                           """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2 :- Base(a), Derived(a, b)
                Derived(b, a) :- Q(b,a)
                           """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isPresent();
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("y", "b");
    }

    @Test
    public void should_notFindHomomorphism_whenDomainDerivationRule_isDifferentThanRangeDerivationRule() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived(x, y)
                Derived(x, y) :- Q(x,y)
                   """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2 :- Base(a), Derived(a, b)
                Derived(a, b) :- R(a,b)
                   """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isNotPresent();
    }

    @Test
    public void should_findHomomorphism_whenDomainHasSeveralDerivationRules_includedInRange() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived(x, y)
                Derived(x, y) :- Q(x,y)
                Derived(x, y) :- R(x,y)
                   """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2:- Base(a), Derived(a, b)
                Derived(a, b) :- Q(a,b)
                Derived(a, b) :- R(a,b)
                Derived(a, b) :- S(a,b)
                   """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isPresent();
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("x", "a");
        SubstitutionAssert.assertThat(substitutionOpt.get()).mapsToVariable("y", "b");
    }

    @Test
    public void should_notFindHomomorphism_whenDomainHasSomeDerivationRules_notIncludedInRange() {
        LogicSchema domainLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @1 :- Base(x), Derived(x, y)
                Derived(x, y) :- Q(x,y)
                Derived(x, y) :- T(x,y)
                   """);
        LogicSchema rangeLogicSchema = new LogicSchemaWithIDsParser().parse("""
                @2 :- Base(a), Derived(a, b)
                Derived(a, b) :- Q(a,b)
                Derived(a, b) :- R(a,b)
                Derived(a, b) :- S(a,b)
                   """);
        List<Literal> domainLiteralsList = domainLogicSchema.getLogicConstraintByID(new ConstraintID("1")).getBody();
        List<Literal> rangeLiteralsList = rangeLogicSchema.getLogicConstraintByID(new ConstraintID("2")).getBody();

        ExtendedHomomorphismFinder extendedHomomorphismFinder = new ExtendedHomomorphismFinder();
        Optional<Substitution> substitutionOpt = extendedHomomorphismFinder.findHomomorphismForLiteralsList(domainLiteralsList, rangeLiteralsList);

        assertThat(substitutionOpt).isNotPresent();
    }

}