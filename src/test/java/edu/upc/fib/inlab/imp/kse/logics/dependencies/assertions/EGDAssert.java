package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ComparisonBuiltInLiteralAssert;
import org.assertj.core.api.AbstractAssert;

public class EGDAssert extends AbstractAssert<EGDAssert, EGD> {

    public EGDAssert(EGD egd) {
        super(egd, EGDAssert.class);
    }

    public static EGDAssert assertThat(EGD actual) {
        return new EGDAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public EGDAssert hasEquality(String expectedEqualityString) {
        ComparisonBuiltInLiteralAssert.assertThat(actual.getHead())
                .isEquality()
                .correspondsTo(expectedEqualityString);
        return this;
    }
}
