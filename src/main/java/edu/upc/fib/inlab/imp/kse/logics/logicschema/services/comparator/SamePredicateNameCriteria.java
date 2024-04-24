package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;

import java.util.Optional;

/**
 * Under this strategy, two ordinary literals are considered homomorphic if they have the same name, and their terms are
 * homomorphic. This is even true when one of the literals is base, and the other is derived
 */
public class SamePredicateNameCriteria implements DerivedOrdinaryLiteralHomomorphismCriteria {
    @Override
    public Optional<Substitution> computeHomomorphismExtensionForDerivedOrdinaryLiteral(HomomorphismFinder homomorphismFinder, Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral) {
        if (domainLiteral.isPositive() != rangeLiteral.isPositive()) return Optional.empty();
        return homomorphismFinder.computeHomomorphismExtensionForAtom(currentSubstitution, domainLiteral.getAtom(), rangeLiteral.getAtom());
    }
}
