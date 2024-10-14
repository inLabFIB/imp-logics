package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicElementSpec;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Specification of a Dependency. E.g. specification of a TGD, or EGD.
 */
public abstract class DependencySpec implements LogicElementSpec {
    private final BodySpec body;

    protected DependencySpec(BodySpec bodySpec) {
        if (Objects.isNull(bodySpec)) throw new IllegalArgumentException("Body cannot be null");
        body = bodySpec;
    }

    public List<LiteralSpec> getBody() {
        return body.literals();
    }

    public abstract Set<String> getAllVariableNames();

}
