package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.operations.Substitution;
import edu.upc.imp.logics.schema.visitor.Visitor;

import java.util.Objects;

/**
 * Implementation of a logic OrdinaryLiteral.
 * E.g. "not Emp(x)"
 * Ordinary literals should not be reused among several normal clauses
 *
 */
public class OrdinaryLiteral extends Literal {
    /**
     * Invariants:
     * - Atom must not be null
     */
    private final Atom atom;
    private final boolean isPositive;


    public OrdinaryLiteral(Atom atom, boolean isPositive) {
        if(Objects.isNull(atom)) throw new IllegalArgumentException("Atom cannot be null");
        this.atom = atom;
        this.isPositive = isPositive;
    }

    /**
     * Creates an ordinary literal with a positive sign
     *
     * @param atom non-null
     */
    public OrdinaryLiteral(Atom atom) {
        this(atom, true);
    }

    public boolean isPositive() {
        return isPositive;
    }

    public Atom getAtom() {
        return atom;
    }

    @Override
    public ImmutableTermList getTerms() {
        return atom.getTerms();
    }

    @Override
    public OrdinaryLiteral applySubstitution(Substitution substitution) {
        return new OrdinaryLiteral(atom.applySubstitution(substitution), isPositive);
    }


    @Override
    public <T, R> T accept(Visitor<T, R> visitor, R context) {
        return visitor.visitOrdinaryLiteral(this, context);
    }

    public boolean isDerived() {
        return atom.isDerived();
    }

    public boolean isBase() {
        return atom.isBase();
    }

//    public List<ImmutableLiteralsList> unfold() {
//        if (this.isBase()) return List.of(new ImmutableLiteralsList(this));
//        if (!this.isPositive) return List.of(new ImmutableLiteralsList(this));
//        return null;
//    }
}
