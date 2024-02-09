package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Dependency {

    /**
     * Invariants:
     * <ul>
     *     <li>body must not be null</li>
     *     <li>body must not be empty</li>
     *     <li>body must be immutable</li>
     * </ul>
     */
    private final ImmutableLiteralsList body;

    protected Dependency(List<Literal> body) {
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
        this.body = new ImmutableLiteralsList(body);
    }

    public ImmutableLiteralsList getBody() {
        return body;
    }

    public abstract Set<Variable> getUniversalVariables();

    public abstract Set<Variable> getExistentialVariables();

    public abstract <T> T accept(DependencySchemaVisitor<T> visitor);
}
