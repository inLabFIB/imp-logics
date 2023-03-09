package edu.upc.imp.logics.schema.assertions;

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


    public AtomAssert containsConstant(int index, String constantName) {
        ImmutableTermListAssert.assertThat(actual.getTerms()).containsConstant(index, constantName);
        return this;
    }

    public AtomAssert hasVariable(int index, String variableName) {
        ImmutableTermListAssert.assertThat(actual.getTerms()).containsVariable(index, variableName);
        return this;
    }
}
