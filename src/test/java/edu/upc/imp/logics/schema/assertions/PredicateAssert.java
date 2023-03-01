package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.Predicate;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class PredicateAssert extends AbstractAssert<PredicateAssert, Predicate> {

    public PredicateAssert(Predicate predicate) {
        super(predicate, PredicateAssert.class);
    }

    public static PredicateAssert assertThat(Predicate actual) {
        return new PredicateAssert(actual);
    }

    public PredicateAssert hasName(String name) {
        Assertions.assertThat(actual.getName()).isEqualTo(name);
        return this;
    }

    public PredicateAssert hasArity(int arity) {
        Assertions.assertThat(actual.getArity()).isEqualTo(arity);
        return this;
    }

}
