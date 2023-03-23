package edu.upc.imp.logics.schema;


import edu.upc.imp.logics.schema.visitor.LogicSchemaVisitor;

import java.util.List;

/**
 * Implementation of a logic derivation rule. That is, a NormalClause with head
 * E.g. "P(x) :- R(x, y)"
 */
public class DerivationRule extends NormalClause {
    /**
     * Invariants:
     * - head cannot be null
     * - head's predicate must be a derived predicate
     */
    private final Atom head;

    /**
     * DerivationRules should be created by their corresponding DerivedLiteral.
     *
     * @param head non-null
     * @param body non-null and not empty
     */
    protected DerivationRule(Atom head, List<Literal> body) {
        super(body);
        this.head = head;
    }

    public Atom getHead() {
        return head;
    }

    @Override
    public String toString() {
        return head.toString() + " :- " + this.getBody();
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
