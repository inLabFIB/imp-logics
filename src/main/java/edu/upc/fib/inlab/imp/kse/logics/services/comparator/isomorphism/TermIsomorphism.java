package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;

/**
 * This class is responsible to remember the bidirectional correspondence between variables that exists,
 * implicitly, in the LiteralIsomorphism.
 */
class TermIsomorphism {
    private final BiMap<Term, Term> map;

    TermIsomorphism() {
        map = new BiMap<>();
    }

    TermIsomorphism(TermIsomorphism termIsomorphism) {
        this.map = new BiMap<>(termIsomorphism.map);
    }

    /**
     * @param t1 a term
     * @param t2 a term
     * @throws CannotIncludeIsomorphismException if we cannot extend this isomorphism to include a map between t1 and t2
     */
    void put(Term t1, Term t2) {
        if (!canIncludeIntoIsomorphism(t1, t2)) {
            throw new CannotIncludeIsomorphismException("Cannot map " + t1 + " to " + t2);
        }
        map.put(t1, t2);
    }

    private boolean canIncludeIntoIsomorphism(Term t1, Term t2) {
        if (t1.isConstant() && t2.isConstant()) {
            return t1.getName().equals(t2.getName());
        } else if (t1.isConstant() != t2.isConstant()) {
            return false;
        } else {
            //Both are variables
            if (map.containsKey(t1)) return map.get(t1).equals(t2);
            else return !map.containsValue(t2);
        }
    }

    boolean canIncludeIntoIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
        try {
            TermIsomorphism newTermIsomorphism = new TermIsomorphism(this);
            for (int i = 0; i < terms1.size(); ++i) {
                Term t1 = terms1.get(i);
                Term t2 = terms2.get(i);
                newTermIsomorphism.put(t1, t2);
            }
            return true;
        } catch (CannotIncludeIsomorphismException e) {
            return false;
        }
    }

}
