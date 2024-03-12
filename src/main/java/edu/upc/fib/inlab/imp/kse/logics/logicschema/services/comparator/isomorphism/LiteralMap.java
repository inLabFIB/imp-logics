package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;

class LiteralMap {

    private final BiMap<Literal, Literal> map;

    LiteralMap() {
        map = new BiMap<>();
    }

    public LiteralMap(LiteralMap literalMap) {
        map = new BiMap<>(literalMap.map);
    }

    public void put(Literal literal1, Literal literal2) {
        map.put(literal1, literal2);
    }

    boolean containsInRange(Literal literal) {
        return map.containsValue(literal);
    }
}
