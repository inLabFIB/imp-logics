package edu.upc.imp.logics.schema;

import java.util.Objects;

/**
 * Implementation of a logic OrdinaryLiteral.
 * E.g. "not Emp(x)"
 *
 */
public class OrdinaryLiteral extends Literal {
    /**
     * Invariants:
     * - Atom must not be null
     */
    private final Atom atom;
    private final boolean sign;


    public OrdinaryLiteral(Atom atom, boolean sign) {
        if(Objects.isNull(atom)) throw new IllegalArgumentException("Atom cannot be null");
        this.atom = atom;
        this.sign = sign;
    }

    /**
     * Creates an ordinary literal with a positive sign
     *
     * @param atom non-null
     */
    public OrdinaryLiteral(Atom atom) {
        this(atom, true);
    }

    public boolean isPositive(){
        return sign;
    }

    public Atom getAtom(){
        return atom;
    }
}
