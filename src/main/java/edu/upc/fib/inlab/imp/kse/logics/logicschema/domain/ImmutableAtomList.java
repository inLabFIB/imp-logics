package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An immutable list of atoms
 */
public class ImmutableAtomList implements List<Atom> {

    /**
     * Invariants: - atomList is not null - atomList does not contain nulls
     */
    private final List<Atom> atomList;

    public ImmutableAtomList(Atom... atoms) {
        this(Arrays.stream(atoms).toList());
    }

    public ImmutableAtomList(List<Atom> atomList) {
        if (Objects.isNull(atomList)) throw new IllegalArgumentException("Atom list cannot be null");
        if (atomList.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Atom list cannot contain null elements");
        this.atomList = Collections.unmodifiableList(atomList);
    }

    /**
     * @param variable not null
     * @return a set of PredicatePositions appearing in body that contains the given variable
     */
    public Set<PredicatePosition> getPredicatePositionsWithVar(Variable variable) {
        Set<PredicatePosition> result = new LinkedHashSet<>();
        for (Atom atom : atomList) {
            for (int position = 0; position < atom.getPredicate().getArity(); ++position) {
                Term termInPosition = atom.getTerms().get(position);
                if (termInPosition.equals(variable)) {
                    result.add(new PredicatePosition(atom.getPredicate(), position));
                }
            }
        }
        return result;
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
    public int hashCode() {
        return Objects.hash(atomList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List<?>)) return false;
        else return Objects.equals(atomList, o);
    }

    @Override
    public String toString() {
        return atomList.stream()
                .map(Atom::toString)
                .collect(Collectors.joining(", "));
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
