package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableAtomList;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.Assertions;

import static org.assertj.core.util.Lists.newArrayList;

public class ImmutableAtomListAssert extends AbstractListAssert<ImmutableAtomListAssert, ImmutableAtomList, Atom, AtomAssert> {

    protected ImmutableAtomListAssert(ImmutableAtomList atoms) {
        super(atoms, ImmutableAtomListAssert.class);
    }

    public static ImmutableAtomListAssert assertThat(ImmutableAtomList actual) {
        return new ImmutableAtomListAssert(actual);
    }

    public ImmutableAtomListAssert hasSize(int size) {
        Assertions.assertThat(actual).hasSize(size);
        return this;
    }

    @Override
    protected AtomAssert toAssert(Atom value, String description) {
        return AtomAssert.assertThat(value).as(description);
    }

    @Override
    protected ImmutableAtomListAssert newAbstractIterableAssert(Iterable<? extends Atom> iterable) {
        return assertThat(new ImmutableAtomList(newArrayList(iterable)));
    }

    public ImmutableAtomListAssert containsAtom(int index, Atom atom) {
        Assertions.assertThat(actual.get(index)).isEqualTo(atom);
        return this;
    }
}
