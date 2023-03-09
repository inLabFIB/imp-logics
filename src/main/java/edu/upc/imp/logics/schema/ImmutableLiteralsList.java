package edu.upc.imp.logics.schema;

import java.util.*;

/**
 * An immutable list of literals
 */
public class ImmutableLiteralsList implements List<Literal> {
    /**
     * Invariants:
     * - literalsList is not null
     * - literalsList has no nulls
     */
    private final List<Literal> literalList;

    public ImmutableLiteralsList(List<Literal> literalList) {
        if (Objects.isNull(literalList)) throw new IllegalArgumentException("LiteralList cannot be null");
        if (literalList.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("LiteralList cannot contain null elements");
        this.literalList = Collections.unmodifiableList(literalList);
    }

    public ImmutableLiteralsList(Literal... literal) {
        this(Arrays.stream(literal).toList());
    }

    @Override
    public int size() {
        return literalList.size();
    }

    @Override
    public boolean isEmpty() {
        return literalList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return literalList.contains(o);
    }

    @Override
    public Iterator<Literal> iterator() {
        return literalList.iterator();
    }

    @Override
    public Object[] toArray() {
        return literalList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return literalList.toArray(a);
    }

    @Deprecated
    @Override
    public boolean add(Literal literal) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(literalList).containsAll(c);
    }

    @Deprecated
    @Override
    public boolean addAll(Collection<? extends Literal> c) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean addAll(int index, Collection<? extends Literal> c) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Literal get(int index) {
        return literalList.get(index);
    }

    @Deprecated
    @Override
    public Literal set(int index, Literal element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void add(int index, Literal element) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public Literal remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return literalList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return literalList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Literal> listIterator() {
        return literalList.listIterator();
    }

    @Override
    public ListIterator<Literal> listIterator(int index) {
        return literalList.listIterator(index);
    }

    @Override
    public ImmutableLiteralsList subList(int fromIndex, int toIndex) {
        return new ImmutableLiteralsList(literalList.subList(fromIndex, toIndex));
    }
}
