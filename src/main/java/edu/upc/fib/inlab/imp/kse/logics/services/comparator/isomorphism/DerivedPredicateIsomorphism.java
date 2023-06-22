package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.DerivationRule;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible to remember the bidirectional correspondence between derived predicates that exists,
 * implicitly, in the LiteralIsomorphism
 */
class DerivedPredicateIsomorphism {
    private final BiMap<Predicate, Predicate> map;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changeVariableNamesAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    DerivedPredicateIsomorphism(boolean changeLiteralOrderAllowed, boolean changeVariableNamesAllowed, boolean changingDerivedPredicateNameAllowed, LiteralIsomorphism literalIsomorphism) {
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
        this.map = initializeMap(literalIsomorphism);
    }

    private BiMap<Predicate, Predicate> initializeMap(LiteralIsomorphism literalIsomorphism) {
        BiMap<Predicate, Predicate> map = new BiMap<>();
        for (Map.Entry<Literal, Literal> entry : literalIsomorphism.entrySet()) {
            Literal l1 = entry.getKey();
            Literal l2 = entry.getValue();
            if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
                if (ol1.isDerived()) {
                    map.put(ol1.getPredicate(), ol2.getPredicate());
                }
            }
        }
        return map;
    }

    boolean canDerivedPredicatesBeIsomorphic(Predicate p1, Predicate p2) {
        if (!changingDerivedPredicateNameAllowed && !p1.getName().equals(p2.getName())) return false;
        if (map.containsValue(p1) && !map.get(p1).equals(p2)) return false;
        if (map.containsValue(p2)) return false;
        return haveIsomorphicDerivationRules(p1, p2);
    }

    private boolean haveIsomorphicDerivationRules(Predicate p1, Predicate p2) {
        if (p1.getDerivationRules().size() != p2.getDerivationRules().size()) return false;
        return haveIsomorphicDerivationRulesRec(p1.getDerivationRules(), p2.getDerivationRules());
    }

    /**
     * rules1 have the same size as rules 2
     *
     * @param rules1 a list of derivation rules
     * @param rules2 a list of derivation rules
     * @return true if there is a bijection between rules1 and rules2 such that each pair of rules are isomorphic
     */
    private boolean haveIsomorphicDerivationRulesRec(List<DerivationRule> rules1, List<DerivationRule> rules2) {
        if (rules1.isEmpty()) return true;

        DerivationRule dr1 = rules1.get(0);
        for (DerivationRule dr2 : rules2) {
            if (areIsomorphic(dr1, dr2)) {
                List<DerivationRule> newRules1 = new LinkedList<>(rules1);
                newRules1.remove(dr1);
                List<DerivationRule> newRules2 = new LinkedList<>(rules2);
                newRules2.remove(dr2);
                if (haveIsomorphicDerivationRulesRec(newRules1, newRules2)) return true;
            }
        }
        return false;
    }

    private boolean areIsomorphic(DerivationRule dr1, DerivationRule dr2) {
        return new IsomorphismComparator(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed)
                .areIsomorphic(dr1, dr2);
    }

}
