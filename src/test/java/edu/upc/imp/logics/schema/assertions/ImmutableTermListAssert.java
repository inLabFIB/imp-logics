package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.ImmutableTermList;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ImmutableTermListAssert extends AbstractAssert<ImmutableTermListAssert, ImmutableTermList> {
    protected ImmutableTermListAssert(ImmutableTermList terms) {
        super(terms, ImmutableTermListAssert.class);
    }

    public static ImmutableTermListAssert assertThat(ImmutableTermList actual) {
        return new ImmutableTermListAssert(actual);
    }

    public ImmutableTermListAssert isEmpty() {
        Assertions.assertThat(actual).isEmpty();
        return this;
    }

    public ImmutableTermListAssert isNotEmpty() {
        Assertions.assertThat(actual).isNotEmpty();
        return this;
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
}
