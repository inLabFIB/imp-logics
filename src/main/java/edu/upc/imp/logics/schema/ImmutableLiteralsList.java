package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.utils.NewFreshVariable;
import edu.upc.imp.logics.schema.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

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

    public ImmutableLiteralsList applySubstitution(Substitution substitution) {
        return new ImmutableLiteralsList(
                this.literalList.stream()
                        .map(l -> l.applySubstitution(substitution))
                        .toList());
    }

    public Set<Variable> getUsedVariables() {
        return this.literalList.stream()
                .map(Literal::getUsedVariables)
                .reduce(new HashSet<>(),
                        (subtotal, element) -> {
                            subtotal.addAll(element);
                            return subtotal;
                        });
    }


    /**
     * <p>Unfolding a literal returns a list of literals' list, one for each derivation rule of this literal.
     * In particular, for each derivation rule, it returns a literals' list replacing the variables of the derivation rule's head
     * for the terms appearing in this literal. </p>
     *
     * <p>For instance, if we have the literal list "P(1), Q(z)", with derivation rules "P(x) :- R(x), S(x)" and "P(y) :- T(y), U(y)",
     * unfolding the first literal of "P(1), Q(z)" will return two literals' list: "R(1), S(1), Q(z)" and "T(1), U(1), Q(z)". </p>
     *
     * <p>This unfolding avoids clashing the variables inside the derivation rule's body with the variables appearing in this literal.
     * For instance, if we have the list "P(a, b), Q(z)" with a derivation rule "P(x, y) :- R(x, y, a, b, z)", and we unfold the first literal
     * it will return "R(a, b, a', b', z'), Q(z)" </p>
     *
     * <p>If the literal proposed to be unfolded, is base, or it is negated, or it is built in, it returns the very same literals' list. </p>
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding the index-th literal
     */
    public List<ImmutableLiteralsList> unfold(int index) {
        Literal literal = this.literalList.get(index);
        if (literal instanceof OrdinaryLiteral ordinaryLiteral) {
            ImmutableLiteralsList previousLiterals = this.subList(0, index);
            List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfold();
            ImmutableLiteralsList nextLiterals = this.subList(index + 1, literalList.size());

            List<ImmutableLiteralsList> result = new LinkedList<>();
            for (ImmutableLiteralsList unfoldedLiterals : unfoldedLiteralsList) {
                result.add(combineLiteralsAvoidingClash(previousLiterals, unfoldedLiterals, nextLiterals, literal.getUsedVariables()));
            }
            return result;
        } else return List.of(this);
    }

    private ImmutableLiteralsList combineLiteralsAvoidingClash(ImmutableLiteralsList previousLiterals, ImmutableLiteralsList unfoldedLiterals, ImmutableLiteralsList nextLiterals, Set<Variable> sharedVariables) {
        Substitution substitutionForClashingTerms = computeSubstitutionForAvoidingClash(previousLiterals, unfoldedLiterals, nextLiterals, sharedVariables);

        List<Literal> result = new LinkedList<>();
        result.addAll(previousLiterals);
        result.addAll(unfoldedLiterals.applySubstitution(substitutionForClashingTerms));
        result.addAll(nextLiterals);
        return new ImmutableLiteralsList(result);
    }

    private Substitution computeSubstitutionForAvoidingClash(ImmutableLiteralsList previousLiterals, ImmutableLiteralsList unfoldedLiterals, ImmutableLiteralsList nextLiterals, Set<Variable> sharedVariables) {
        Set<Variable> potentiallyClashingVariables = computePotentiallyClashingVariables(sharedVariables, previousLiterals, nextLiterals);
        Set<Variable> currentlyUsedVariables = computeCurrentlyUsedVariables(previousLiterals, unfoldedLiterals, nextLiterals);
        Substitution substitutionForClashingTerms = new Substitution();
        for (Variable potentiallyClashingVariable : potentiallyClashingVariables) {
            Variable newFreshVariable = NewFreshVariable.computeNewFreshVariable(potentiallyClashingVariable.getName(), currentlyUsedVariables);
            substitutionForClashingTerms.addMapping(new Variable(potentiallyClashingVariable.getName()), newFreshVariable);
            currentlyUsedVariables.add(newFreshVariable);
        }
        return substitutionForClashingTerms;
    }

    private Set<Variable> computeCurrentlyUsedVariables(ImmutableLiteralsList previousLiterals, ImmutableLiteralsList unfoldedLiterals, ImmutableLiteralsList nextLiterals) {
        Set<Variable> usedVariables = new HashSet<>();
        usedVariables.addAll(previousLiterals.getUsedVariables());
        usedVariables.addAll(unfoldedLiterals.getUsedVariables());
        usedVariables.addAll(nextLiterals.getUsedVariables());
        return usedVariables;
    }

    private Set<Variable> computePotentiallyClashingVariables(Set<Variable> sharedVariables, ImmutableLiteralsList... literalListSet) {
        Set<Variable> potentiallyClashingVariables = Arrays.stream(literalListSet)
                .map(ImmutableLiteralsList::getUsedVariables)
                .reduce(new HashSet<>(), (element, subtotal) -> {
                    Set<Variable> total = new HashSet<>(subtotal);
                    total.addAll(element);
                    return total;
                });
        potentiallyClashingVariables.removeAll(sharedVariables);
        return potentiallyClashingVariables;
    }

    @Override
    public String toString() {
        return this.literalList.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
