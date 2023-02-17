package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.schema.OrdinaryLiteralTest;
import edu.upc.imp.logics.specification.LiteralSpec;
import edu.upc.imp.logics.specification.OrdinaryLiteralSpec;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class LiteralAssert extends AbstractAssert<LiteralAssert, Literal> {

    public LiteralAssert(Literal actual) {
        super(actual, LiteralAssert.class);
    }

    public static LiteralAssert assertThat(Literal actual) {
        return new LiteralAssert(actual);
    }

    public LiteralAssert correspondsSpec(LiteralSpec spec) {
        if(actual instanceof OrdinaryLiteral) {
            Assertions.assertThat(spec).isInstanceOf(OrdinaryLiteralSpec.class);
            OrdinaryLiteralAssert.assertThat((OrdinaryLiteral) actual).correspondsSpec((OrdinaryLiteralSpec)spec);
        }
        else throw new RuntimeException("Unrecognized literal " + actual.getClass().getName());
        return this;
    }
}
