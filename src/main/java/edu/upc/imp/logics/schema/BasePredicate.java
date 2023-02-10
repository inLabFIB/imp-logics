package edu.upc.imp.logics.schema;

/**
 * A predicate whose evaluation does not depend on derivation rules, but on the contents of the database.
 */
public class BasePredicate extends Predicate {
    public BasePredicate(String name, Arity arity) {
        super(name, arity);
    }
}
