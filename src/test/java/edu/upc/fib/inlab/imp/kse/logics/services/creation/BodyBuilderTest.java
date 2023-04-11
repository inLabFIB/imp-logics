package edu.upc.fib.inlab.imp.kse.logics.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.schema.BooleanBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static edu.upc.fib.inlab.imp.kse.logics.schema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class BodyBuilderTest {

    @Test
    public void should_addOrdinaryLiteral_whenAddingOrdinaryLiteralSpec() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        OrdinaryLiteralSpec literalSpec = new OrdinaryLiteralSpec("P", termFactory.createTermSpecs("x", "y"), true);

        BodyBuilder bodyBuilder = new BodyBuilder(Map.of("P", new Predicate("P", 2)));
        ImmutableLiteralsList body = bodyBuilder
                .addLiteral(literalSpec)
                .build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(literalSpec)
        );
    }

    @Test
    public void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithComparisonOperator() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);

        ImmutableLiteralsList body = new BodyBuilder(Collections.emptyMap())
                .addLiteral(builtInLiteralSpec)
                .build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(builtInLiteralSpec)
        );
    }

    @Test
    public void should_ThrowException_whenAddingBuiltInLiteralSpec_WithComparisonOperator_AndWrongNumberOfTerms() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y", "z");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);

        BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
        assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                WrongNumberOfTermsInBuiltInLiteral.class
        );
    }

    @Test
    public void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithUnrecognizedOperator() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("ANYEQ", termSpecList);

        BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
        assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                UnrecognizedBuiltInOperator.class
        );
    }

    @Nested
    class BooleanBuiltInLiteralCreation {

        @ParameterizedTest
        @ValueSource(strings = {"TRUE", "FALSE"})
        public void should_throwException_when_booleanBuiltInLiteralSpecContainsTerms(String booleanOperator) {
            StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
            List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
            BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec(booleanOperator, termSpecList);

            BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
            assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                    WrongNumberOfTermsInBuiltInLiteral.class
            );
        }


        @ParameterizedTest
        @ValueSource(strings = {"TRUE", "FALSE"})
        public void should_createBooleanBuiltIn_when_booleanBuiltInLiteralSpecContainsTerms(String booleanOperator) {
            BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec(booleanOperator, Collections.emptyList());

            ImmutableLiteralsList body = new BodyBuilder(Collections.emptyMap())
                    .addLiteral(builtInLiteralSpec)
                    .build();

            assertThat(body).hasSize(1);
            assertThat(body).first()
                    .asInstanceOf(InstanceOfAssertFactories.type(BooleanBuiltInLiteral.class))
                    .satisfies(
                            booleanBuiltInLiteral -> assertThat(booleanBuiltInLiteral)
                                    .hasOperationName(booleanOperator)
                                    .hasNoTerms()
                    );
        }

    }

}