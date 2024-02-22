package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * An immutable list of terms
 */
public class ImmutableTermList implements List<Term> {
    /**
     * Invariants:
     * - termsList is not null
     * - termsList does not contain nulls
     */
    private final List<Term> termsList;

    public ImmutableTermList(List<Term> termsList) {
        if (Objects.isNull(termsList)) throw new IllegalArgumentException("Term's list cannot be null");
        if (termsList.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("Term's list cannot contain null elements");
        this.termsList = Collections.unmodifiableList(termsList);
    }

    public ImmutableTermList(Term... terms) {
        this(Arrays.stream(terms).toList());
    }

    @Override
    public int size() {
        return termsList.size();
    }

    @Override
    public boolean isEmpty() {
        return termsList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return termsList.contains(o);
    }

    @Override
    public Iterator<Term> iterator() {
        return termsList.iterator();
    }

    @Override
    public Object[] toArray() {
        return termsList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return termsList.toArray(a);
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean add(Term term) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new LinkedHashSet<>(termsList).containsAll(c);
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean addAll(Collection<? extends Term> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean addAll(int index, Collection<? extends Term> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Term get(int index) {
        return termsList.get(index);
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public Term set(int index, Term element) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public void add(int index, Term element) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated This operation is not supported
     */
    @Deprecated(forRemoval = false)
    @Override
    public Term remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return termsList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return termsList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Term> listIterator() {
        return termsList.listIterator();
    }

    @Override
    public ListIterator<Term> listIterator(int index) {
        return termsList.listIterator(index);
    }

    @Override
    public ImmutableTermList subList(int fromIndex, int toIndex) {
        return new ImmutableTermList(termsList.subList(fromIndex, toIndex));
    }

    public ImmutableTermList applySubstitution(Substitution substitution) {
        List<Term> substitutedTerms = this.stream()
                .map(term -> term.applySubstitution(substitution))
                .toList();
        return new ImmutableTermList(substitutedTerms);
    }

    public Set<Variable> getUsedVariables() {
        return termsList.stream()
                .filter(Term::isVariable)
                .map(Variable.class::cast)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof List<?>)) return false;
        else return Objects.equals(termsList, o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(termsList);
    }
}
