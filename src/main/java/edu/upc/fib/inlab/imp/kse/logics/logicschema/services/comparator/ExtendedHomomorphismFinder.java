package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

/**
 * Homomorphism finder that considers two derived ordinary literals to be homomorphic, even whey they have different
 * predicate names, if they have homomorphic derivation rules.
 */
public class ExtendedHomomorphismFinder extends HomomorphismFinder {

    public ExtendedHomomorphismFinder() {
        super(new HomomorphicRulesHomomorphismCriteria());
    }

}
