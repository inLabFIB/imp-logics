package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;

import static org.assertj.core.util.Lists.newArrayList;

public class ImmutableTermListAssert extends AbstractListAssert<ImmutableTermListAssert, ImmutableTermList, Term, TermAssert> {
    protected ImmutableTermListAssert(ImmutableTermList terms) {
        super(terms, ImmutableTermListAssert.class);
    }

    public static ImmutableTermListAssert assertThat(ImmutableTermList actual) {
        return new ImmutableTermListAssert(actual);
    }

    public ImmutableTermListAssert containsVariable(int index, String variableName) {
        TermAssert.assertThat(actual.get(index))
                .isVariable(variableName);
        return this;
    }

    public ImmutableTermListAssert containsConstant(int index, String constantName) {
        TermAssert.assertThat(actual.get(index))
                .isConstant(constantName);
        return this;
    }

    public ImmutableTermListAssert hasSize(int size) {
        Assertions.assertThat(actual).hasSize(size);
        return this;
    }

    @Override
    protected TermAssert toAssert(Term value, String description) {
        return TermAssert.assertThat(value).as(description);
    }

    @Override
    protected ImmutableTermListAssert newAbstractIterableAssert(Iterable<? extends Term> iterable) {
        return assertThat(new ImmutableTermList(newArrayList(iterable)));
    }
}
