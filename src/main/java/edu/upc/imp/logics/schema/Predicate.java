package edu.upc.imp.logics.schema;

import java.util.Objects;

/**
 * Representation of a logic predicate. E.g. Predicate "Emp" with arity 2.
 * A Predicate is a weak entity w.r.t. LogicSchema. That is:
 * - One Predicate can only belong to one LogicSchema
 * - A LogicSchema cannot contain two predicates with the same name
 *
 */
public abstract class Predicate {
    /**
     * I prefer having a Predicate, BasePredicate and DerivedPredicate hierarchy to ensure Liskov substitution principle
     * In this manner, we can control whether some contract expects a BasePredicate, or a DerivedPredicate
     * Invariants:
     * - name cannot be null
     * - arity cannot be null
     */
    private final String name;
    private final Arity arity;

    public Predicate(String name, Arity arity) {
        if(Objects.isNull(name)) throw new IllegalArgumentException("Name cannot be null");
        if(Objects.isNull(arity)) throw new IllegalArgumentException("Arity cannot be null");

        this.name = name;
        this.arity = arity;
    }

    public Arity getArity() {
        return arity;
    }

    public String getName() {
        return name;
    }

}
