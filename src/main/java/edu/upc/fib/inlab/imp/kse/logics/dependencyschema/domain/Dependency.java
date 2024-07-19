package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<Variable> getUniversalVariables() {
        return getBody().stream()
                .flatMap(l -> l.getTerms().stream())
                .filter(Variable.class::isInstance)
                .map(t -> (Variable) t)
                .collect(Collectors.toSet());
    }

    public ImmutableLiteralsList getBody() {
        return body;
    }

    public boolean containsBuiltInOrNegatedLiteralInBody() {
        return this.getBody().stream().anyMatch(lit ->
                                                        (lit instanceof OrdinaryLiteral oLit && oLit.isNegative()) ||
                                                                (lit instanceof BuiltInLiteral));
    }

    public abstract <T> T accept(DependencySchemaVisitor<T> visitor);

}
