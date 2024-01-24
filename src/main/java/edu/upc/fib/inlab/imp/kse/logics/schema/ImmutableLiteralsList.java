package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LiteralComparator;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.NewFreshVariable;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An immutable list of literals.
 * <br>
 * When the immutable list has been created through an unfolding, or applying a substitution, it remembers
 * which literals has been replaced with which other literals
 */
public class ImmutableLiteralsList implements List<Literal> {
    /**
     * Invariants:
     * - literalsList is not null
     * - literalsList has no nulls
     * - originalLiteralMap maps the literals of literalList to the original literals of the ImmutableLiteralsList
     * that has created this literal through an unfolding, or through applying a substitution.
     */
    private final List<Literal> literalList;

    private final Map<Literal, Literal> originalLiteralMap;
    private final Map<LiteralPosition, LiteralPosition> originalLiteralPositionMap;

    public ImmutableLiteralsList(List<Literal> literalList) {
        this(literalList, Collections.emptyMap(), Collections.emptyMap());
    }

    public ImmutableLiteralsList(Literal... literal) {
        this(Arrays.stream(literal).toList());
    }

    private ImmutableLiteralsList(List<Literal> literalList, Map<Literal, Literal> originalLiteralMap, Map<LiteralPosition, LiteralPosition> originalLiteralPositionMap) {
        if (Objects.isNull(literalList)) throw new IllegalArgumentException("LiteralList cannot be null");
        if (literalList.stream().anyMatch(Objects::isNull))
            throw new IllegalArgumentException("LiteralList cannot contain null elements");
        this.literalList = Collections.unmodifiableList(literalList);
        this.originalLiteralMap = Collections.unmodifiableMap(originalLiteralMap);
        this.originalLiteralPositionMap = Collections.unmodifiableMap(originalLiteralPositionMap);
    }

    /**
     * Sort literals in next order: positive literals, negative literals, built-in literals
     *
     * @return a new sorted list of literals
     * @deprecated Use {@link #sortLiterals(Comparator)} instead
     */
    @Deprecated()
    public ImmutableLiteralsList getSorted() {
        return sortLiterals(new LiteralComparator());
    }

    /**
     * Sort literals in next order: positive literals, negative literals, built-in literals
     *
     * @return a new sorted list of literals
     */
    public ImmutableLiteralsList sortLiterals(Comparator<Literal> literalComparator) {
        List<Literal> sortedBody = stream().sorted(literalComparator).toList();
        return new ImmutableLiteralsList(sortedBody);
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

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean add(Literal literal) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new LinkedHashSet<>(literalList).containsAll(c);
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean addAll(Collection<? extends Literal> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean addAll(int index, Collection<? extends Literal> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Literal get(int index) {
        return literalList.get(index);
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public Literal set(int index, Literal element) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
    @Deprecated
    @Override
    public void add(int index, Literal element) {
        throw new UnsupportedOperationException();
    }

    /**
     * @deprecated Unsupported operation.
     */
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
        List<Literal> substitutedLiteralsList = this.literalList.stream()
                .map(l -> l.applySubstitution(substitution))
                .toList();

        Map<Literal, Literal> newOriginalLiteralMap = new HashMap<>();
        for (int i = 0; i < literalList.size(); ++i) {
            Literal originalLiteral = literalList.get(i);
            Literal literalAfterSubs = substitutedLiteralsList.get(i);
            if (literalAfterSubs != originalLiteral) {
                newOriginalLiteralMap.put(literalAfterSubs, originalLiteral);
            }
        }

        Map<LiteralPosition, LiteralPosition> newOriginalLiteralPositionMap = new HashMap<>();
        return new ImmutableLiteralsList(substitutedLiteralsList, newOriginalLiteralMap, newOriginalLiteralPositionMap);

    }

    public Set<Variable> getUsedVariables() {
        return this.literalList.stream()
                .map(Literal::getUsedVariables)
                .reduce(new LinkedHashSet<>(),
                        (subtotal, element) -> {
                            subtotal.addAll(element);
                            return subtotal;
                        });
    }

    public Set<Variable> getVariablesInPositiveOrdinaryLiterals() {
        return literalList.stream()
                .filter(OrdinaryLiteral.class::isInstance)
                .map(OrdinaryLiteral.class::cast)
                .filter(OrdinaryLiteral::isPositive)
                .flatMap(ordinaryLiteral -> ordinaryLiteral.getUsedVariables().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Variable> getVariablesInNegativeOrdinaryLiterals() {
        return literalList.stream()
                .filter(l -> l instanceof OrdinaryLiteral ol && ol.isNegative())
                .flatMap(l -> l.getUsedVariables().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Variable> getVariablesInBuiltInLiterals() {
        return literalList.stream()
                .filter(BuiltInLiteral.class::isInstance)
                .flatMap(l -> l.getUsedVariables().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public List<ImmutableLiteralsList> unfold(int index, boolean unfoldNegatedLiterals) {
        Literal literal = this.literalList.get(index);
        if (literal instanceof OrdinaryLiteral ordinaryLiteral) {
            ImmutableLiteralsList previousLiterals = this.subList(0, index);
            List<ImmutableLiteralsList> unfoldedLiteralsList = ordinaryLiteral.unfold(unfoldNegatedLiterals);
            ImmutableLiteralsList nextLiterals = this.subList(index + 1, literalList.size());

            List<ImmutableLiteralsList> result = new LinkedList<>();
            for (ImmutableLiteralsList unfoldedLiterals : unfoldedLiteralsList) {
                result.add(combineLiteralsAvoidingClash(previousLiterals, unfoldedLiterals, nextLiterals, literal));
            }
            return result;
        } else return List.of(this);
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
        return unfold(index, false);
    }

    /**
     * This is an extension of the unfold method that also applies an unfolding for negated literals whose
     * derivation rules does not contain existential variables.
     * E.g.: Suppose the literals list "P(x), not(Derived(x))" with derivation rule "Derived(x) :- A(x), not(B(x))"
     * <p>
     * Unfolding not(Derived()) in such literals list will return two literals list:
     * "P(x), not(A(x))" and "P(x), B(x)".
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding the index-th literal
     */
    public List<ImmutableLiteralsList> unfoldWithNegationExtension(int index) {
        return unfold(index, true);
    }

    /**
     * @param currentLiteral not null
     * @return the literal that has created the given literal through an unfolding, or through applying a substitution
     * to a previous ImmutableLiteralsList.
     */
    public Optional<Literal> getOriginalLiteral(Literal currentLiteral) {
        requireNonNull(currentLiteral);
        return Optional.ofNullable(originalLiteralMap.get(currentLiteral));
    }


    public Optional<LiteralPosition> getOriginalLiteralPosition(Literal currentLiteral, int termIndex) {
        requireNonNull(currentLiteral);
        if (termIndex < 0 || termIndex >= currentLiteral.getTerms().size()) {
            throw new IllegalArgumentException("Term index out of bounds in current Literal " + currentLiteral);
        }
        return Optional.ofNullable(originalLiteralPositionMap.get(new LiteralPosition(currentLiteral, termIndex)));
    }


    private ImmutableLiteralsList combineLiteralsAvoidingClash(ImmutableLiteralsList previousLiterals, ImmutableLiteralsList unfoldedLiterals, ImmutableLiteralsList nextLiterals, Literal unfoldedLiteral) {
        Substitution substitutionForClashingTerms = computeSubstitutionForAvoidingClash(previousLiterals, unfoldedLiterals, nextLiterals, unfoldedLiteral.getUsedVariables());

        List<Literal> result = new LinkedList<>(previousLiterals);
        ImmutableLiteralsList substitutedLiterals = unfoldedLiterals.applySubstitution(substitutionForClashingTerms);
        result.addAll(substitutedLiterals);
        result.addAll(nextLiterals);

        Map<Literal, Literal> newOriginalLiteralMap = new HashMap<>();
        substitutedLiterals.forEach(l -> newOriginalLiteralMap.put(l, unfoldedLiteral));

        Map<LiteralPosition, LiteralPosition> newOriginalLiteralPositionMap = new HashMap<>();
        return new ImmutableLiteralsList(result, newOriginalLiteralMap, newOriginalLiteralPositionMap);
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
        Set<Variable> usedVariables = new LinkedHashSet<>();
        usedVariables.addAll(previousLiterals.getUsedVariables());
        usedVariables.addAll(unfoldedLiterals.getUsedVariables());
        usedVariables.addAll(nextLiterals.getUsedVariables());
        return usedVariables;
    }

    private Set<Variable> computePotentiallyClashingVariables(Set<Variable> sharedVariables, ImmutableLiteralsList... literalListSet) {
        Set<Variable> potentiallyClashingVariables = Arrays.stream(literalListSet)
                .map(ImmutableLiteralsList::getUsedVariables)
                .reduce(new LinkedHashSet<>(), (element, subtotal) -> {
                    Set<Variable> total = new LinkedHashSet<>(subtotal);
                    total.addAll(element);
                    return total;
                });
        potentiallyClashingVariables.removeAll(sharedVariables);
        return potentiallyClashingVariables;
    }

    @Override
    public String toString() {
        return this.literalList.stream()
                .map(Literal::toString)
                .collect(Collectors.joining(", "));
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutableLiteralsList that)) return false;
        return Objects.equals(literalList, that.literalList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(literalList);
    }
}
