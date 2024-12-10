package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicElementSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record HeadAtomsSpec(List<OrdinaryLiteralSpec> atoms) implements LogicElementSpec {
    public Set<String> getAllVariableNames() {
        return atoms.stream()
                .flatMap(a -> a.getAllVariableNames().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
