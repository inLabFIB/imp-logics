package edu.upc.fib.inlab.imp.kse.logics.schema;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic normal clause. Normal clauses might be LogicConstraints, or DerivationRules
 * A NormalClause should be used, at most, in one LogicSchema.
 */
public abstract class NormalClause {
    /**
     * Invariants:
     * - body must not be null
     * - body must not be empty
     * - body must be immutable
     */
    private final ImmutableLiteralsList body;

    public NormalClause(List<Literal> body) {
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");
        this.body = new ImmutableLiteralsList(body);
    }

    /**
     * @return an immutable list of literals that forms the body of this normal clause
     */
    public ImmutableLiteralsList getBody() {
        return body;
    }

    public abstract boolean isSafe();

}
