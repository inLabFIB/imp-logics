package edu.upc.imp.logics.services.creation.spec;

/**
 * Specification of a predicate. Do note that a predicate specification is just a name, and one arity.
 * I.e., a predicate specification has no derivation rules associated.
 *
 * @param name
 * @param arity
 */
public record PredicateSpec(String name, int arity) {
}
