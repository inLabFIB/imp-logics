package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class FunctionalDependency {
    private final Predicate predicate;
    private final Set<Integer> keyPositions;
    private final Set<Integer> determinedPositions;

    /**
     * Invariants:
     * <li> keyPositions are between 0 and predicate arity </li>
     * <li> determinedPositions are between 0 and predicate arity </li>
     * <li> keyPositions and determinedPositions are disjoint </li>
     */

    FunctionalDependency(Predicate predicate, Set<Integer> keyPositions, Set<Integer> determinedPositions) {
        checkArgsInvariants(
                predicate, keyPositions, determinedPositions);

        this.predicate = predicate;
        this.keyPositions = Collections.unmodifiableSet(keyPositions);
        this.determinedPositions = Collections.unmodifiableSet(determinedPositions);
    }

    private static void checkArgsInvariants(Predicate predicate, Set<Integer> keyPositions, Set<Integer> determinedPositions) {
        int arity = predicate.getArity();
        if (keyPositions.stream().anyMatch(kp -> positionOutOfRange(kp, arity)))
            throw new IllegalArgumentException("KeyPositions " + keyPositions + " should be between 0 and " + arity);
        if (determinedPositions.stream().anyMatch(dp -> positionOutOfRange(dp, arity)))
            throw new IllegalArgumentException("DeterminedPositions " + determinedPositions + " should be between 0 and " + arity);
        if (!Collections.disjoint(keyPositions, determinedPositions))
            throw new IllegalArgumentException("KeyPositions" + keyPositions + " should be disjoint with determinedPositions " + determinedPositions);
    }

    private static boolean positionOutOfRange(Integer position, int arity) {
        return position < 0 || position >= arity;
    }

    public String getPredicateName() {
        return predicate.getName();
    }

    public boolean isKeyDependency() {
        return predicate.getArity() == keyPositions.size() + determinedPositions.size();
    }

    public Predicate predicate() {
        return predicate;
    }

    public Set<Integer> keyPositions() {
        return keyPositions;
    }

    public Set<Integer> determinedPositions() {
        return determinedPositions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (FunctionalDependency) obj;
        return Objects.equals(this.predicate, that.predicate) &&
                Objects.equals(this.keyPositions, that.keyPositions) &&
                Objects.equals(this.determinedPositions, that.determinedPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicate, keyPositions, determinedPositions);
    }

    @Override
    public String toString() {
        return "FunctionalDependency[" +
                "predicate=" + predicate + ", " +
                "keyPositions=" + keyPositions + ", " +
                "determinedPositions=" + determinedPositions + ']';
    }

}
