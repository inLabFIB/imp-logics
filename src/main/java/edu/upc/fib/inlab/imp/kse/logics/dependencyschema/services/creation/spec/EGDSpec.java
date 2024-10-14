package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Specification of a logic constraint including an ID for it.
 */
public class EGDSpec extends DependencySpec {

    private final BuiltInLiteralSpec head;

    public EGDSpec(List<LiteralSpec> bodyLiterals, BuiltInLiteralSpec head) {
        this(new BodySpec(bodyLiterals), head);
    }

    public EGDSpec(BodySpec body, BuiltInLiteralSpec head) {
        super(body);
        if (Objects.isNull(head)) throw new IllegalArgumentException("Head cannot be null");
        //TODO: check it is equality
        this.head = head;
    }

    public BuiltInLiteralSpec getHead() {
        return head;
    }

    @Override
    public Set<String> getAllVariableNames() {
        Set<String> result = new LinkedHashSet<>(head.getAllVariableNames());
        result.addAll(new BodySpec(getBody()).getAllVariableNames());
        return result;
    }
}
