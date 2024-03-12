package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.schema.EqualityComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;

import java.util.List;
import java.util.Objects;

/**
 * Equality-Generating Dependency
 */
public class EGD extends Dependency {
    /**
     * Invariants:
     * <ul>
     *     <li>head must not be null</li>
     *     <li>head must be immutable</li>
     * </ul>
     */
    private final EqualityComparisonBuiltInLiteral head;

    public EGD(List<Literal> body, EqualityComparisonBuiltInLiteral head) {
        super(body);
        if (Objects.isNull(head)) throw new IllegalArgumentException("Head cannot be null");
        this.head = head;
    }

    public EqualityComparisonBuiltInLiteral getHead() {
        return head;
    }

    @Override
    public <T> T accept(DependencySchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return getBody().toString() + " -> " + head.toString();
    }

}
