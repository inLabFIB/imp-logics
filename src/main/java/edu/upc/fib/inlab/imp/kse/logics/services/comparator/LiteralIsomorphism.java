package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableTermList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.LiteralAlreadyMappedInIsomorphismException;

import java.util.Map;

public class LiteralIsomorphism {
    private final BiMap<Literal, Literal> map;

    public LiteralIsomorphism() {
        map = new BiMap<>();
    }

    public LiteralIsomorphism(LiteralIsomorphism literalIsomorphism) {
        this.map = new BiMap<>(literalIsomorphism.map);
    }

    public <T extends Literal> void add(T firstLiteral, T secondLiteral) {
        if (map.containsKey(firstLiteral) || map.containsValue(secondLiteral))
            throw new LiteralAlreadyMappedInIsomorphismException(firstLiteral, secondLiteral);
        this.map.put(firstLiteral, secondLiteral);
    }

    public boolean containsInDomain(Literal literal) {
        return map.containsKey(literal);
    }

    public boolean containsInRange(Literal literal) {
        return map.containsValue(literal);
    }

    public boolean termsAreCompatibleWithIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
        TermIsomorphism termIsomorphism = computeTermIsomorphism();
        return termIsomorphism.termsAreCompatibleWithIsomorphism(terms1, terms2);
    }

    private TermIsomorphism computeTermIsomorphism() {
        TermIsomorphism termIsomorphism = new TermIsomorphism();
        for (Map.Entry<Literal, Literal> entry : this.map.entrySet()) {
            addTermsIntoTermIsomorphism(termIsomorphism, entry.getKey(), entry.getValue());
        }
        return termIsomorphism;
    }

    private void addTermsIntoTermIsomorphism(TermIsomorphism termIsomorphism, Literal l1, Literal l2) {
        for (int i = 0; i < l1.getArity(); ++i) {
            Term t1 = l1.getTerms().get(i);
            Term t2 = l2.getTerms().get(i);
            termIsomorphism.put(t1, t2);
        }
    }

    public <T extends Literal> T get(T firstOl) {
        return (T) map.get(firstOl);
    }

    private static class TermIsomorphism {
        private final BiMap<Term, Term> map;

        private TermIsomorphism() {
            map = new BiMap<>();
        }

        public TermIsomorphism(TermIsomorphism termIsomorphism) {
            this.map = new BiMap<>(termIsomorphism.map);
        }

        public void put(Term t1, Term t2) {
            if (t1.isConstant() && t2.isConstant()) {
                if (!t1.getName().equals(t2.getName()))
                    throw new RuntimeException("Cannot map constants to other constants");
            } else if (t1.isConstant() != t2.isConstant()) {
                throw new RuntimeException("Cannot map variables to constants");
            } else {
                //Both are variables
                if (map.containsKey(t1)) {
                    if (!map.get(t1).equals(t2))
                        throw new RuntimeException("Cannot map a variable to a different variable");
                } else if (map.containsValue(t2)) {
                    throw new RuntimeException("Cannot map a variable to a different variable");
                }
                map.put(t1, t2);
            }
        }

        public boolean termsAreCompatibleWithIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
            try {
                TermIsomorphism newTermIsomorphism = new TermIsomorphism(this);
                for (int i = 0; i < terms1.size(); ++i) {
                    Term t1 = terms1.get(i);
                    Term t2 = terms2.get(i);
                    newTermIsomorphism.put(t1, t2);
                }
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }
}
