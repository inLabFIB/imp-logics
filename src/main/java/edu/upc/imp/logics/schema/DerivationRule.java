package edu.upc.imp.logics.schema;


import java.util.List;

/**
 * Implementation of a logic derivation rule. That is, a NormalClause with head
 * E.g. "P(x) :- R(x, y)"
 *
 */
public class DerivationRule extends NormalClause {
    private final Atom head;

    public DerivationRule(Atom head, List<Literal> body) {
        super(body);
        this.head = head;
    }

    public Atom getHead() {
        return head;
    }
}
