package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.OrdinaryLiteral;

import java.util.Optional;

interface DerivedOrdinaryLiteralHomomorphismCriteria {

    Optional<Substitution> computeHomomorphismExtensionForDerivedOrdinaryLiteral(HomomorphismFinder homomorphismFinder, Substitution currentSubstitution, OrdinaryLiteral domainLiteral, OrdinaryLiteral rangeLiteral);
}
