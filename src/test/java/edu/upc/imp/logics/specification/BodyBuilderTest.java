package edu.upc.imp.logics.specification;

import edu.upc.imp.logics.assertions.LiteralAssert;
import edu.upc.imp.logics.schema.Arity;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.MutablePredicate;
import edu.upc.imp.logics.specification.exceptions.UnrecognizedBuiltInOperator;
import edu.upc.imp.logics.specification.exceptions.WrongNumberOfTermsInBuiltInLiteral;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BodyBuilderTest {

    @Test
    public void should_addOrdinaryLiteral_whenAddingOrdinaryLiteralSpec() {
        BodyBuilder bodyBuilder = new BodyBuilder(Map.of("P", new MutablePredicate("P", new Arity(2))));
        StringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        OrdinaryLiteralSpec literalSpec = new OrdinaryLiteralSpec("P", termFactory.createTermSpecs("x", "y"), true);
        bodyBuilder.addLiteral(literalSpec);

        List<Literal> body = bodyBuilder.build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(literalSpec)
        );
    }

    @Test
    public void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithComparisonOperator() {
        BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
        DefaultStringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);
        bodyBuilder.addLiteral(builtInLiteralSpec);

        List<Literal> body = bodyBuilder.build();

        assertThat(body).hasSize(1);
        assertThat(body).first().satisfies(
                l -> LiteralAssert.assertThat(l).correspondsSpec(builtInLiteralSpec)
        );
    }

    @Test
    public void should_ThrowException_whenAddingBuiltInLiteralSpec_WithComparisonOperator_AndWrongNumberOfTerms() {
        BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
        DefaultStringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y", "z");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("=", termSpecList);
        assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                WrongNumberOfTermsInBuiltInLiteral.class
        );
    }

    @Test
    public void should_addComparisonBuiltInLiteral_whenAddingBuiltInLiteralSpec_WithUnrecognizedOperator() {
        BodyBuilder bodyBuilder = new BodyBuilder(Collections.emptyMap());
        DefaultStringToTermSpecFactory termFactory = new DefaultStringToTermSpecFactory();
        List<TermSpec> termSpecList = termFactory.createTermSpecs("x", "y");
        BuiltInLiteralSpec builtInLiteralSpec = new BuiltInLiteralSpec("ANYEQ", termSpecList);

        assertThatThrownBy(() -> bodyBuilder.addLiteral(builtInLiteralSpec)).isInstanceOf(
                UnrecognizedBuiltInOperator.class
        );
    }

}