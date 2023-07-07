package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;

import java.util.List;
import java.util.Optional;

public interface LogicEquivalenceAnalyzer {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<Boolean> UNKNOWN = Optional.empty();

    Optional<Boolean> areEquivalent(List<Literal> first, List<Literal> second);

    Optional<Boolean> areEquivalent(LogicConstraint first, LogicConstraint second);

    Optional<Boolean> areEquivalent(DerivationRule first, DerivationRule second);
}
