package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.List;
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

    public boolean isNegative() {
        return !isPositive;
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
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isDerived() {
        return atom.isDerived();
    }

    public boolean isBase() {
        return atom.isBase();
    }

    /**
     * <p>Unfolding a positive ordinary literal returns a list of literals' list, one for each derivation rule of this ordinary literal.
     * In particular, for each derivation rule, it returns a literalsList replacing the variables of the derivation rule's head
     * for the terms appearing in this literal. </p>
     *
     * <p>For instance, if we have the ordinary literal "P(1)", with derivation rules "P(x) :- R(x), S(x)" and "P(y) :- T(y), U(y)",
     * unfolding "P(1)" will return two literals' list: "R(1), S(1)" and "T(1), U(1)". </p>
     *
     * <p>This unfolding avoids clashing the variables inside the derivation rule's body with the variables appearing in this literal.
     * For instance, if we have the ordinary literal "P(a, b)" with a derivation rule "P(x, y) :- R(x, y, a, b)" it will return
     * "R(a, b, a', b')" </p>
     *
     * <p>If the ordinary literal is base, or it is negated, it returns the very same literal. </p>
     *
     * @return a list of ImmutableLiteralsList representing the result of unfolding this literal
     */
    public List<ImmutableLiteralsList> unfold() {
        if (!this.isPositive) return List.of(new ImmutableLiteralsList(this));
        return atom.unfold();
    }

    @Override
    public String toString() {
        if (isPositive) return atom.toString();
        else return "not(" + atom.toString() + ")";
    }
}
