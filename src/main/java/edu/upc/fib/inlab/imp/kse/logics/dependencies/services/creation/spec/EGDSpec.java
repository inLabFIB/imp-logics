package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.creation.spec;

import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BodySpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicElementSpec;

import java.util.List;
import java.util.Objects;

/**
 * Specification of a logic constraint including an ID for it.
 */
public class EGDSpec extends DependencySpec implements LogicElementSpec {

    private final BuiltInLiteralSpec head;

    public EGDSpec(BodySpec body, BuiltInLiteralSpec head) {
        super(body);
        if (Objects.isNull(head)) throw new IllegalArgumentException("Head cannot be null");
        //TODO: check it is equality
        this.head = head;
    }

    public EGDSpec(List<LiteralSpec> bodyLiterals, BuiltInLiteralSpec head) {
        this(new BodySpec(bodyLiterals), head);
    }

    public BuiltInLiteralSpec getHead() {
        return head;
    }
}
