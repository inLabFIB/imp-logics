package edu.upc.imp.logics.assertions;

import edu.upc.imp.logics.schema.Atom;
import org.assertj.core.api.AbstractAssert;

public class AtomAssert extends AbstractAssert<AtomAssert, Atom> {
    public AtomAssert(Atom atom) {
        super(atom, AtomAssert.class);
    }

    public static AtomAssert assertThat(Atom actual) {
        return new AtomAssert(actual);
    }

    public AtomAssert hasPredicate(String predicateName, int arity) {
        PredicateAssert.assertThat(actual.getPredicate())
                .hasName(predicateName)
                .hasArity(arity);
        return this;
    }


}
