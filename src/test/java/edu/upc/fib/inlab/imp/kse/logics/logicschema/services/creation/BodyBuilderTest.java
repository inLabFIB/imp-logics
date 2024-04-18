package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LiteralAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BooleanBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.CustomBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteralException;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.TermSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers.StringToTermSpecFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.LogicSchemaAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class BodyBuilderTest {

    @Test
    void should_addOrdinaryLiteral_whenAddingOrdinaryLiteralSpec() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        OrdinaryLiteralSpec literalSpec = new OrdinaryLiteralSpec("P", termFactory.createTermSpecs("x", "y"), true);

        final Map<String, Predicate> predicateMap = Map.of("P", new Predicate("P", 2));
        BodyBuilder bodyBuilder = new BodyBuilder(new LiteralFactory(predicateMap));
        ImmutableLiteralsList body = bodyBuilder
                .addLiteral(literalSpec)
                .build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(literalSpec)
        );
    }

    @Test
    void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithComparisonOperator() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);

        ImmutableLiteralsList body = new BodyBuilder(new LiteralFactory(Collections.emptyMap()))
                .addLiteral(builtInLiteralSpec)
                .build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(builtInLiteralSpec)
        );
    }

    @Test
    void should_ThrowException_whenAddingBuiltInLiteralSpec_WithComparisonOperator_AndWrongNumberOfTerms() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y", "z");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);

        BodyBuilder bodyBuilder = new BodyBuilder(new LiteralFactory(Collections.emptyMap()));
        assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                WrongNumberOfTermsInBuiltInLiteralException.class
        );
    }

    @Test
    void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithUnrecognizedOperator() {
        StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("ANYEQ", termSpecList);

        BodyBuilder bodyBuilder = new BodyBuilder(new LiteralFactory(Collections.emptyMap()));
        bodyBuilder.addLiteral(builtInLiteralSpec);
        ImmutableLiteralsList body = bodyBuilder.build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(builtInLiteralSpec));
    }

    @Nested
    class BooleanBuiltInLiteralCreation {

        @ParameterizedTest
        @ValueSource(strings = {"TRUE", "FALSE"})
        void should_throwException_when_booleanBuiltInLiteralSpecContainsTerms(String booleanOperator) {
            StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
            List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
            BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec(booleanOperator, termSpecList);

            BodyBuilder bodyBuilder = new BodyBuilder(new LiteralFactory(Collections.emptyMap()));
            assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                    WrongNumberOfTermsInBuiltInLiteralException.class
            );
        }


        @ParameterizedTest
        @ValueSource(strings = {"TRUE", "FALSE"})
        void should_createBooleanBuiltIn_when_booleanBuiltInLiteralSpecContainsTerms(String booleanOperator) {
            BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec(booleanOperator, Collections.emptyList());

            ImmutableLiteralsList body = new BodyBuilder(new LiteralFactory(Collections.emptyMap()))
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

    @Nested
    class CustomBuiltInLiteralCreation {

        @Test
        void should_createCustomBuiltInLiteral_whenOperationNameIsNotTrueNorFalse() {
            StringToTermSpecFactory termFactory = new StringToTermSpecFactory();
            List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
            BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("customBuiltInLiteral", termSpecList);

            ImmutableLiteralsList body = new BodyBuilder(new LiteralFactory(Collections.emptyMap()))
                    .addLiteral(builtInLiteralSpec)
                    .build();

            assertThat(body).hasSize(1);
            assertThat(body).first()
                    .asInstanceOf(InstanceOfAssertFactories.type(CustomBuiltInLiteral.class))
                    .satisfies(
                            customBuiltInLiteral -> assertThat(customBuiltInLiteral)
                                    .hasOperationName("customBuiltInLiteral")
                                    .hasVariable(0, "x")
                                    .hasVariable(1, "y")
                    );
        }

    }

}