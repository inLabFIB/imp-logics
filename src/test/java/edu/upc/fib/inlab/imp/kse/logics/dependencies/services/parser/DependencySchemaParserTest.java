package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.DerivationRuleAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.PredicateAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.AllVariableTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.CapitalConstantsTermTypeCriteria;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.CustomBuiltInPredicateNameChecker;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.LogicSchemaWithIDsParser;
import edu.upc.fib.inlab.imp.kse.logics.services.parser.exceptions.ParserCanceledException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Stream;

import static edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions.DependencySchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencySchemaParserTest {

    @Nested
    class PredicateContainmentTests {
        @Test
        void should_containPredicateOf0Arity_whenPredicateAppearsInTGD() {
            String schemaString = "q() -> p()";

            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .hasPredicates(2)
                    .containsPredicate("q", 0)
                    .containsPredicate("p", 0);
        }

        @Test
        void should_containPredicateOf0Arity_whenPredicateAppearsInEGD() {
            String schemaString = "q() -> 1=1";

            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .hasPredicates(1)
                    .containsPredicate("q", 0);
        }

        @Test
        void should_containPredicate_whenPredicateAppearsInDependency_zeroArity_multipleDependencies() {
            String schemaString = """
                    q() -> p()
                    r() -> 1=1
                    """;

            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .hasPredicates(3)
                    .containsPredicate("q", 0)
                    .containsPredicate("p", 0)
                    .containsPredicate("r", 0);
        }

        @Test
        void should_containPredicate_whenPredicateAppearsInDependency() {
            String schemaString = "father(x,y) -> person(y)";

            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .containsPredicate("person", 1)
                    .containsPredicate("father", 2);
        }
    }

    @Nested
    class TGDTests {
        // Examples:
        // works(p,c) -> person(p), company(c)
        // student(p) -> person(p)
        // father(x,y) -> person(y)

        @Test
        void shouldContainTGD_whenParsingSchema() {
            String schemaString = "q() -> p()";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(1)
                    .hasLiteral(0, "q()");
            assertThat(dependency).asTGD()
                    .headOfSize(1)
                    .hasAtom(0, "p()");
        }

        @Test
        void shouldContainTGD_whenParsingSchema_withDifferentLiteralsTypes() {
            String schemaString = "q(x), x=1, x<2, TRUE() -> p()";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(4)
                    .hasLiteral(0, "q(x)")
                    .hasLiteral(1, "x=1")
                    .hasLiteral(2, "x<2")
                    .hasLiteral(3, "TRUE()");
            assertThat(dependency).asTGD()
                    .headOfSize(1)
                    .hasAtom(0, "p()");
        }

        @Test
        void shouldContainTGD_whenParsingSchema_withMultipleElementsInHeadAndBody() {
            String schemaString = "q(), q() -> p(), p()";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(2)
                    .hasLiteral(0, "q()")
                    .hasLiteral(1, "q()");
            assertThat(dependency).asTGD()
                    .headOfSize(2)
                    .hasAtom(0, "p()")
                    .hasAtom(1, "p()");
        }

        @Test
        void shouldContainTGD_whenParsingSchema_withMultipleElementsInHeadAndBody_andDifferentTerms() {
            String schemaString = "q(a,b), s(x,y,1) -> p(s,r,a), r(1)";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(2)
                    .hasLiteral(0, "q(a,b)")
                    .hasLiteral(1, "s(x,y,1)");
            assertThat(dependency).asTGD()
                    .headOfSize(2)
                    .hasAtom(0, "p(s,r,a)")
                    .hasAtom(1, "r(1)");
        }

        @Test
        void shouldContainTGD_whenParsingSchema_with2Dependencies() {
            String schemaString = """
                    q() -> p()
                    r() -> p()
                    """;
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .hasDependencies(2)
                    .dependencies()
                    .anySatisfy(dependency -> {
                        assertThat(dependency).body()
                                .hasSize(1)
                                .hasLiteral(0, "q()");
                        assertThat(dependency).asTGD()
                                .headOfSize(1)
                                .hasAtom(0, "p()");
                    })
                    .anySatisfy(dependency -> {
                        assertThat(dependency).body()
                                .hasSize(1)
                                .hasLiteral(0, "r()");
                        assertThat(dependency).asTGD()
                                .headOfSize(1)
                                .hasAtom(0, "p()");
                    });
        }
    }

    @Nested
    class EGDTests {

        @Test
        void should_containEGD_whenParsingSchema() {
            String schemaString = "q(x,y) -> x=y";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(1)
                    .hasLiteral(0, "q(x,y)");
            assertThat(dependency).asEGD()
                    .hasEquality("x=y");
        }

        @Test
        void should_containEGD_whenParsingSchema_withDifferentLiteralTypes() {
            String schemaString = "q(x,y), x=1, x<2, TRUE() -> x=y";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(4)
                    .hasLiteral(0, "q(x,y)")
                    .hasLiteral(1, "x=1")
                    .hasLiteral(2, "x<2")
                    .hasLiteral(3, "TRUE()");
            assertThat(dependency).asEGD()
                    .hasEquality("x=y");
        }

        @Test
        void should_containEGD_whenParsingSchema_with2EGDs() {
            String schemaString = """
                    q(x,y) -> x=y
                    r(x,y) -> x=y
                    """;
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .hasDependencies(2)
                    .dependencies()
                    .anySatisfy(dependency -> {
                        assertThat(dependency).body()
                                .hasSize(1)
                                .hasLiteral(0, "q(x,y)");
                        assertThat(dependency).asEGD()
                                .hasEquality("x=y");
                    })
                    .anySatisfy(dependency -> {
                        assertThat(dependency).body()
                                .hasSize(1)
                                .hasLiteral(0, "r(x,y)");
                        assertThat(dependency).asEGD()
                                .hasEquality("x=y");
                    });
        }

        @Test
        void should_containEGD_whenParsingSchema_withOnlyBuiltInLiterals() {
            String schemaString = "x=y -> y=x";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencies(1);

            Dependency dependency = dependencySchema.getAllDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(1)
                    .hasLiteral(0, "x=y");
            assertThat(dependency).asEGD()
                    .hasEquality("y=x");
        }

        @Test
        void should_throwException_whenEGDHasTwoEqualitiesInHead() {
            String schemaString = "q(x,y) -> x=y, x=y";

            DependencySchemaParser dependencySchemaParser = new DependencySchemaParser();
            assertThatThrownBy(() -> dependencySchemaParser.parse(schemaString))
                    .isInstanceOf(ParserCanceledException.class);
        }
    }

    @Test
    void should_containDifferentDependencies_whenParsingSchema() {
        String schemaString = """
                q() -> p()
                r() -> 1=1
                """;

        DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

        assertThat(dependencySchema)
                .hasDependencies(2)
                .dependencies()
                .anySatisfy(dependency -> {
                    assertThat(dependency).body()
                            .hasSize(1)
                            .hasLiteral(0, "q()");
                    assertThat(dependency).asTGD()
                            .headOfSize(1)
                            .hasAtom(0, "p()");
                })
                .anySatisfy(dependency -> {
                    assertThat(dependency).body()
                            .hasSize(1)
                            .hasLiteral(0, "r()");
                    assertThat(dependency).asEGD()
                            .hasEquality("1=1");
                });
    }

    @Test
    void should_containDifferentDependencies_whenParsingSchema_withDerivedLiterals() {
        String schemaString = """
                works(p,c) -> salary(v,c)
                            
                works(p,c) :- persona(p), company(c)
                salary(v,c) :- v > 0
                salary(v,c) :- v < 1000000
                """;

        DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

        assertThat(dependencySchema)
                .hasDependencies(1)
                .dependencies()
                .anySatisfy(dependency -> {
                    assertThat(dependency).body()
                            .hasSize(1)
                            .hasLiteral(0, "works(p,c)");
                    assertThat(dependency).asTGD()
                            .headOfSize(1)
                            .hasAtom(0, "salary(v,c)");
                });

        Set<Predicate> derivedPredicates = dependencySchema.getAllDerivedPredicates();
        Assertions.assertThat(derivedPredicates)
                .hasSize(2)
                .anySatisfy(derivedPredicate -> {
                    PredicateAssert.assertThat(derivedPredicate)
                            .hasName("works")
                            .hasArity(2)
                            .isDerived()
                            .hasDerivationRules(1);

                    DerivationRule derivationRule = derivedPredicate.getFirstDerivationRule();
                    DerivationRuleAssert.assertThat(derivationRule)
                            .body()
                            .hasSize(2)
                            .hasLiteral(0, "persona(p)")
                            .hasLiteral(1, "company(c)");
                })
                .anySatisfy(derivedPredicate -> {
                    PredicateAssert.assertThat(derivedPredicate)
                            .hasName("salary")
                            .hasArity(2)
                            .isDerived()
                            .hasDerivationRules(2);

                    DerivationRule derivationRule1 = derivedPredicate.getDerivationRules().get(0);
                    DerivationRuleAssert.assertThat(derivationRule1)
                            .body()
                            .hasSize(1)
                            .hasLiteral(0, "v > 0");
                    DerivationRule derivationRule2 = derivedPredicate.getDerivationRules().get(1);
                    DerivationRuleAssert.assertThat(derivationRule2)
                            .body()
                            .hasSize(1)
                            .hasLiteral(0, "v < 1000000");
                });
    }

    //TODO: duplicated code from LogicSchema grammar and parser

    @Nested
    class BooleanBuiltInLiteralTest {
        @ParameterizedTest
        @MethodSource("booleanValues")
        void should_containBooleanBuiltInLiteral_whenBodyContainsBooleanString(String booleanString, boolean booleanValue) {
            String schemaString = "@1 :- " + booleanString + "()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            LogicSchemaAssertions.assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            LogicSchemaAssertions.assertThat(logicConstraint)
                    .containsBooleanBuiltInLiteral(booleanValue);
        }

        private static Stream<Arguments> booleanValues() {
            return Stream.of(
                    Arguments.of("TRUE", true),
                    Arguments.of("FALSE", false)
            );
        }
    }

    @Nested
    class CustomBuiltInLiteralTest {

        @Test
        void should_containCustomBuiltInLiteral_whenConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("myCustomBuiltInPredicate"))
            ).parse(schemaString);

            LogicSchemaAssertions.assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            LogicSchemaAssertions.assertThat(logicConstraint)
                    .containsCustomBuiltInLiteral("myCustomBuiltInPredicate");
        }

        @Test
        void should_notContainCustomBuiltInLiteral_whenNotConfiguringCustomBuiltInPredicates() {
            String schemaString = "@1 :- myCustomBuiltInPredicate()";

            LogicSchema logicSchema = new LogicSchemaWithIDsParser(new AllVariableTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of("anotherPredicateName"))
            ).parse(schemaString);

            LogicSchemaAssertions.assertThat(logicSchema).containsConstraintID("1");
            LogicConstraint logicConstraint = logicSchema.getLogicConstraintByID(new ConstraintID("1"));

            LogicSchemaAssertions.assertThat(logicConstraint)
                    .containsOrdinaryLiteral("myCustomBuiltInPredicate", 0);
        }
    }

    @Nested
    class ConstantAndVariableParsingTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "0", "1", "01", "1000",
                "1.0", "0.0", "1000.0", ".000001",
                "1.0e-10", "1.0e10", "1.0e+10",
                "1.0E-10", "1.0E10", "1.0E+10"
        })
        void should_parseConstant_whenTermIsNumber(String numberString) {
            String schemaString = "@1 :- P(" + numberString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, numberString);
        }

        @ParameterizedTest
        @ValueSource(strings = {"''", "'1'", "'a'", "'abc'", "'Escape \\' test'", "'Hello \n World'"})
        void should_parseConstant_whenTermIsSingleQuotes(String constantString) {
            String schemaString = "@1 :- P(" + constantString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, constantString);
        }

        @ParameterizedTest
        @ValueSource(strings = {"\"\"", "\"1\"", "\"a\"", "\"abc\"", "\"Escape \\\" test\"", "\"Hello \n World\""})
        void should_parseConstant_whenTermIsDoubleQuotes(String constantString) {
            String schemaString = "@1 :- P(" + constantString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser().parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, constantString);
        }

        @Test
        void should_parseConstant_whenTermIsID_andCriteriaMakesItConstant() {
            String schemaString = "@1 :- P(A)";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser(new CapitalConstantsTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of())).parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasConstant(0, "A");
        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "a'", "a123"})
        void should_parseVariable_whenTermIsID_andCriteriaMakesItVariable(String variableString) {
            String schemaString = "@1 :- P(" + variableString + ")";

            LogicSchema parsedSchema = new LogicSchemaWithIDsParser(new CapitalConstantsTermTypeCriteria(),
                    new CustomBuiltInPredicateNameChecker(Set.of())).parse(schemaString);
            LogicConstraint logicConstraint = parsedSchema.getLogicConstraintByID(new ConstraintID("1"));
            OrdinaryLiteral ordinaryLiteral = (OrdinaryLiteral) logicConstraint.getBody().get(0);

            LiteralAssert.assertThat(ordinaryLiteral).hasVariable(0, variableString);
        }
    }
}