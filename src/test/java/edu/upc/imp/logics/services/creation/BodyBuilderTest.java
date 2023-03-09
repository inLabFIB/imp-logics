package edu.upc.imp.logics.services.creation;

import edu.upc.imp.logics.schema.ImmutableLiteralsList;
import edu.upc.imp.logics.schema.Predicate;
import edu.upc.imp.logics.schema.assertions.LiteralAssert;
import edu.upc.imp.logics.services.creation.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.imp.logics.services.creation.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import edu.upc.imp.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.OrdinaryLiteralSpec;
import edu.upc.imp.logics.services.creation.spec.TermSpec;
import edu.upc.imp.logics.services.creation.spec.helpers.StringToTermSpecFactory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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

}