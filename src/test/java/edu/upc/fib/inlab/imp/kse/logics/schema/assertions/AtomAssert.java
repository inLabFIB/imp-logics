package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
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

    @SuppressWarnings("UnusedReturnValue")
    public AtomAssert hasPredicateName(String predicateName) {
        PredicateAssert.assertThat(actual.getPredicate()).hasName(predicateName);
        return this;
    }

    public AtomAssert containsConstant(int index, String constantName) {
        ImmutableTermListAssert.assertThat(actual.getTerms()).containsConstant(index, constantName);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public AtomAssert hasVariable(int index, String variableName) {
        ImmutableTermListAssert.assertThat(actual.getTerms()).containsVariable(index, variableName);
        return this;
    }
}
