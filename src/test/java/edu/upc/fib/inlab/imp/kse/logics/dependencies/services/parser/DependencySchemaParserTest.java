package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.parser;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.DependencySchema;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions.DependencySchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DependencySchemaParserTest {
//Todo: add tests!!!

// Examples:
// works(p,c) -> person(p), company(c)
// student(p) -> person(p)
// father(x,y) -> person(y)

    @Nested
    class PredicateContainmentTests {
        @Test
        void should_containPredicate_whenPredicateAppearsInDependency_zeroArity() {
            String schemaString = "q() -> p()";

            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema)
                    .containsPredicate("q", 0)
                    .containsPredicate("p", 0);
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
        @Test
        void should_containTGD() {
            String schemaString = "q() -> p()";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencySize(1);

            Dependency dependency = dependencySchema.getDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(1)
                    .hasLiteral(0, "q()");
            assertThat(dependency).asTGD()
                    .headOfSize(1)
                    .hasAtom(0, "p()");
        }
    }

    @Nested
    class EGDTests {
        @Test
        void should_containEGD() {
            String schemaString = "q(x,y) -> x=y";
            DependencySchema dependencySchema = new DependencySchemaParser().parse(schemaString);

            assertThat(dependencySchema).hasDependencySize(1);

            Dependency dependency = dependencySchema.getDependencies().stream().toList().get(0);
            assertThat(dependency).body()
                    .hasSize(1)
                    .hasLiteral(0, "q(x,y)");
            assertThat(dependency).asEGD()
                    .hasEquality("x=y");
        }

        @Disabled("ANTLR fixes this single-extra-token problems")
        @Test
        void should_throwException_whenEGDHasTwoEqualitiesInHead() {
            String schemaString = "q(x,y) -> x=y, x=y";

            DependencySchemaParser dependencySchemaParser = new DependencySchemaParser();
            assertThatThrownBy(() -> dependencySchemaParser.parse(schemaString));
        }
    }



}