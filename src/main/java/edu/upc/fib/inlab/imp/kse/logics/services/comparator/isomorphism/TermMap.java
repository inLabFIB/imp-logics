package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.Term;

class TermMap {

    private final BiMap<Term, Term> map;

    TermMap() {
        map = new BiMap<>();
    }

    TermMap(TermMap otherMap) {
        map = new BiMap<>(otherMap.map);
    }

    boolean isIncompatibleWith(Term domain, Term range) {
        return !isCompatibleWith(domain, range);
    }

    boolean isCompatibleWith(Term domain, Term range) {
        if (domain.isConstant() && range.isConstant()) return domain.getName().equals(range.getName());
        else if (domain.isConstant() != range.isConstant()) return false;
        if (!map.containsKey(domain) && !map.containsValue(range)) return true;
        if (map.containsKey(domain)) {
            return map.get(domain).equals(range);
        }
        return false;
    }

    void put(Term domain, Term range) {
        map.put(domain, range);
    }

}
