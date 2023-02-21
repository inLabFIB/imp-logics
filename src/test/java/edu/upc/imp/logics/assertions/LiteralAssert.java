package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.imp.logics.schema.Literal;
import edu.upc.imp.logics.schema.OrdinaryLiteral;
import edu.upc.imp.logics.specification.BuiltInLiteralSpec;
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
        if (actual instanceof OrdinaryLiteral actualOrdinaryLiteral) {
            Assertions.assertThat(spec).isInstanceOf(OrdinaryLiteralSpec.class);
            OrdinaryLiteralAssert.assertThat(actualOrdinaryLiteral).correspondsSpec((OrdinaryLiteralSpec) spec);
        } else if (actual instanceof ComparisonBuiltInLiteral actualComparison) {
            Assertions.assertThat(spec).isInstanceOf(BuiltInLiteralSpec.class);
            ComparisonBuiltInLiteralAssert.assertThat(actualComparison).correspondsSpec((BuiltInLiteralSpec) spec);
        } else {
            throw new RuntimeException("Unrecognized literal " + actual.getClass().getName());
        }
        return this;
    }
}
