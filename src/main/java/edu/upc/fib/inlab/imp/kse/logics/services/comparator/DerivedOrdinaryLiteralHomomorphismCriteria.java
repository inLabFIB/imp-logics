package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

import java.util.Optional;

public interface DerivedOrdinaryLiteralHomomorphismCriteria {

    Optional<Substitution> computeHomomorphismExtensionForDerivedOrdinaryLiteral(HomomorphismFinder homomorphismFinder, Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral);
}
