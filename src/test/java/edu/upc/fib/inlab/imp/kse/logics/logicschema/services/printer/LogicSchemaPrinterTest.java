package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LogicSchemaWithCustomBuiltInMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class LogicSchemaPrinterTest {

    @Test
    void should_printLogicSchema() {
        String schemaString = """
                @1 :- WorksIn(E, D), not(Emp(E))
                @2 :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
                @3 :- Dept(D), not(MinOneSpecialEmployee(D))
                MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
                MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
                """;

        LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

        LogicSchemaPrinter printer = new LogicSchemaPrinter();
        String actualLogicSchema = printer.print(logicSchema);

        Set<String> actualNormalClauses = actualLogicSchema.lines().collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> expectedNormalClauses = schemaString.lines().collect(Collectors.toCollection(LinkedHashSet::new));

        assertThat(actualNormalClauses).containsExactlyInAnyOrderElementsOf(expectedNormalClauses);
    }

    @Nested
    class ConstraintPrintingTests {

        @Test
        void should_printLogicSchema_WithOneConstraint() {
            String schemaString = "@1 :- WorksIn(E, D), not(Emp(E))";
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            String result = new LogicSchemaPrinter().print(logicSchema);

            assertThat(result).isEqualToIgnoringWhitespace(schemaString);
        }

        @Test
        void should_printLogicSchema_WithSeveralConstraints() {
            String schemaString = """
                    @1 :- WorksIn(E, D), not(Emp(E))
                    @2 :- WorksIn(E, D), Manages(E, D), CrucialDept(D)
                    @3 :- Dept(D), not(MinOneSpecialEmployee(D))
                    """;
            LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schemaString);

            LogicSchemaPrinter printer = new LogicSchemaPrinter();
            String actualLogicSchema = printer.print(logicSchema);

            Set<String> actualNormalClauses = actualLogicSchema.lines().collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> expectedNormalClauses = schemaString.lines().collect(Collectors.toCollection(LinkedHashSet::new));

            assertThat(actualNormalClauses).containsExactlyInAnyOrderElementsOf(expectedNormalClauses);
        }
    }

    @Nested
    class DerivationRulesPrintingTests {
        @Test
        void should_printLogicSchema_WithOneDerivationRule() {
            String schemaString = "MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)";
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            LogicSchemaPrinter printer = new LogicSchemaPrinter();
            String result = printer.print(logicSchema);

            assertThat(result).isEqualToIgnoringWhitespace(schemaString);
        }

        @Test
        void should_printLogicSchema_WithSeveralDerivationRules() {
            String schemaString = """
                    MinOneSpecialEmployee(D) :- WorksIn(E, D), Happy(E)
                    MinOneSpecialEmployee(D) :- WorksIn(E, D), not(Rich(E))
                    """;
            LogicSchema logicSchema = LogicSchemaMother.buildLogicSchemaWithIDs(schemaString);

            LogicSchemaPrinter printer = new LogicSchemaPrinter();
            String actualLogicSchema = printer.print(logicSchema);

            Set<String> actualNormalClauses = actualLogicSchema.lines().collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> expectedNormalClauses = schemaString.lines().collect(Collectors.toCollection(LinkedHashSet::new));

            assertThat(actualNormalClauses).containsExactlyInAnyOrderElementsOf(expectedNormalClauses);
        }
    }

    @Nested
    class BuiltInLiteralPrintingTests {

        @Test
        void should_printLogicSchema_whenContainsDifferentBuiltInLiterals() {
            String schemaString = """
                    @1 :- 1<2, TRUE(), FALSE(), 1<>2, customBuiltIn(x)
                    """;

            LogicSchema logicSchema = LogicSchemaWithCustomBuiltInMother.buildLogicSchema(schemaString, "customBuiltIn");

            LogicSchemaPrinter printer = new LogicSchemaPrinter();
            String actualLogicSchema = printer.print(logicSchema);

            Set<String> actualNormalClauses = actualLogicSchema.lines().collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> expectedNormalClauses = schemaString.lines().collect(Collectors.toCollection(LinkedHashSet::new));

            assertThat(actualNormalClauses).containsExactlyInAnyOrderElementsOf(expectedNormalClauses);
        }
    }
}
