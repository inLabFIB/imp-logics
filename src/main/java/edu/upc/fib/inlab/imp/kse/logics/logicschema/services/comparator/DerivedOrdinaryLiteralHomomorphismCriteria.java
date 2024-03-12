package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;

import java.util.Optional;

public interface DerivedOrdinaryLiteralHomomorphismCriteria {

    Optional<Substitution> computeHomomorphismExtensionForDerivedOrdinaryLiteral(HomomorphismFinder homomorphismFinder, Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral);
}
