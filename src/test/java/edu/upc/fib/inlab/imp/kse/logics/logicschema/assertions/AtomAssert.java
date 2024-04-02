package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

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

    /**
     * Asserts that the actual atom should have the very same predicate (i.e., same object reference)
     * as the one given by parameter
     *
     * @param predicate not null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public AtomAssert hasPredicate(Predicate predicate) {
        Assertions.assertThat(actual.getPredicate()).isSameAs(predicate);
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

    @SuppressWarnings("UnusedReturnValue")
    public AtomAssert hasTerms(ImmutableTermList terms) {
        ImmutableTermListAssert.assertThat(actual.getTerms())
                .containsExactlyElementsOf(terms);
        return this;
    }

}
