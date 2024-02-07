package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import org.assertj.core.api.AbstractAssert;

public class EGDAssert extends AbstractAssert<EGDAssert, EGD> {

    public EGDAssert(EGD egd) {
        super(egd, EGDAssert.class);
    }

    public static EGDAssert assertThat(EGD actual) {
        return new EGDAssert(actual);
    }

}
