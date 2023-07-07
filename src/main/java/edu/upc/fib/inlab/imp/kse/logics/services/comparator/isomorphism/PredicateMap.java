package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;

class PredicateMap {
    private final BiMap<Predicate, Predicate> map;

    PredicateMap() {
        map = new BiMap<>();
    }

    void put(Predicate domain, Predicate range) {
        map.put(domain, range);
    }

    void removeDomain(Predicate domain) {
        map.remove(domain);
    }

    boolean isIncompatibleWithMap(Predicate domain, Predicate range) {
        return !isCompatibleWithMap(domain, range);
    }

    boolean isCompatibleWithMap(Predicate domain, Predicate range) {
        if (!map.containsKey(domain) && !map.containsValue(range)) return true;
        if (map.containsKey(domain)) {
            return map.get(domain).equals(range);
        }
        return false;
    }
}
