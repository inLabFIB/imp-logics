package edu.upc.imp.logics.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Implementation of a logic normal clause. Normal clauses might be LogicConstraints, or DerivationRules
 * A NormalClause should be used, at most, in one LogicSchema.
 * That is, NormalClause cannot be reused.
 */
public abstract class NormalClause {
    /**
     * Invariants:
     * - body must not be null
     * - body must not be empty
     * - body must be inmutable
     */
    private final List<Literal> body; //TODO: alias problem

    public NormalClause(List<Literal> body) {
        if (Objects.isNull(body)) throw new IllegalArgumentException("Body cannot be null");
        if (body.isEmpty()) throw new IllegalArgumentException("Body cannot be empty");

        this.body = body;
    }

    public List<Literal> getBody() {
        return Collections.unmodifiableList(body);
    }
}
