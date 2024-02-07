package edu.upc.fib.inlab.imp.kse.logics.dependencies.assertions;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.schema.assertions.ImmutableLiteralsListAssert;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class DependencyAssert extends AbstractAssert<DependencyAssert, Dependency> {

    public DependencyAssert(Dependency dependency) {
        super(dependency, DependencyAssert.class);
    }

    public static DependencyAssert assertThat(Dependency actual) {
        return new DependencyAssert(actual);
    }

    public DependencyAssert isTGD() {
        Assertions.assertThat(actual).isInstanceOf(TGD.class);
        return this;
    }

    public TGDAssert asTGD() {
        objects.assertIsInstanceOf(info, actual, TGD.class);
        return new TGDAssert((TGD) actual).as(info.description());
    }

    public DependencyAssert isEGD() {
        Assertions.assertThat(actual).isInstanceOf(EGD.class);
        return this;
    }

    @SuppressWarnings("unused")
    public EGDAssert asEGD() {
        objects.assertIsInstanceOf(info, actual, EGD.class);
        return new EGDAssert((EGD) actual).as(info.description());
    }

    @SuppressWarnings("UnusedReturnValue")
    public ImmutableLiteralsListAssert body() {
        return ImmutableLiteralsListAssert.assertThat(actual.getBody());
    }
}
