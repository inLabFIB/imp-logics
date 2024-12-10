package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Specification of a body of a normal clause.
 */
public record BodySpec(List<LiteralSpec> literals) implements LogicElementSpec {

    public BodySpec {
        if (Objects.isNull(literals)) throw new IllegalArgumentException("Literals cannot be null");
        if (literals.isEmpty()) throw new IllegalArgumentException("Literals cannot be empty");
    }

    public Set<String> getAllVariableNames() {
        return literals.stream()
                .flatMap(literalSpec -> literalSpec.getAllVariableNames().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
