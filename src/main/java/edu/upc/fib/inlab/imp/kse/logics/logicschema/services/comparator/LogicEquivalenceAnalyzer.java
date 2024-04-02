package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LogicConstraint;

import java.util.List;
import java.util.Optional;

public interface LogicEquivalenceAnalyzer {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<Boolean> UNKNOWN = Optional.empty();

    Optional<Boolean> areEquivalent(List<Literal> first, List<Literal> second);

    Optional<Boolean> areEquivalent(LogicConstraint first, LogicConstraint second);

    Optional<Boolean> areEquivalent(DerivationRule first, DerivationRule second);
}
