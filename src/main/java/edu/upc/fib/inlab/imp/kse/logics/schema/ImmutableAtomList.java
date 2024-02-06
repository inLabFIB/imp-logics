package edu.upc.fib.inlab.imp.kse.logics.schema;

import java.util.*;

/**
 * An immutable list of atoms
 */
public class ImmutableAtomList implements List<Atom> {

    /**
     * Invariants:
     * - atomList is not null
     * - atomList does not contain nulls
     */
    private final List<Atom> atomList;

    public ImmutableAtomList(List<Atom> atomList) {
        if (Objects.isNull(atomList)) throw new IllegalArgumentException("Atom list cannot be null");
        if (atomList.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Atom list cannot contain null elements");
        this.atomList = Collections.unmodifiableList(atomList);
    }

    public ImmutableAtomList(Atom... atoms) {
        this(Arrays.stream(atoms).toList());
    }

    @Override
    public int size() {
        return atomList.size();
    }

    @Override
    public boolean isEmpty() {
        return atomList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return atomList.contains(o);
    }

    @Override
    public Iterator<Atom> iterator() {
        return atomList.iterator();
    }

    @Override
    public Object[] toArray() {
        return atomList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return atomList.toArray(a);
    }

    @Override
    public boolean add(Atom atom) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new LinkedHashSet<>(atomList).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Atom> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends Atom> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Atom get(int index) {
        return atomList.get(index);
    }

    @Override
    public Atom set(int index, Atom element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Atom element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Atom remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return atomList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return atomList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Atom> listIterator() {
        return atomList.listIterator();
    }

    @Override
    public ListIterator<Atom> listIterator(int index) {
        return atomList.listIterator(index);
    }

    @Override
    public List<Atom> subList(int fromIndex, int toIndex) {
        return new ImmutableAtomList(atomList.subList(fromIndex, toIndex));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List<?>)) return false;
        else return Objects.equals(atomList, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomList);
    }
}
