package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A literal isomorphism is a 1-to-1 map between literals where:
 * - Mapped based literals have the same predicate
 * - Mapped literals have the same polarity
 * <p>
 * The implicit term map is also an isomorphism (i.e., a 1-to-1 map between variables)
 * The implicit derived predicate map is also an isomorphism (i.e., a 1-to-1 map between derived predicate with isomorphic derivation rules)
 */
class LiteralIsomorphism {

    private final BiMap<Literal, Literal> map;
    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;
    private final TermIsomorphism initialTermIsomorphism;
    private final DerivedPredicateIsomorphism initialDerivedPredicateIsomorphism;

    private LiteralIsomorphism(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed, TermIsomorphism termIsomorphism, DerivedPredicateIsomorphism derivedPredicateIsomorphism, BiMap<Literal, Literal> map) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
        this.map = new BiMap<>(map);
        this.initialTermIsomorphism = termIsomorphism;
        this.initialDerivedPredicateIsomorphism = derivedPredicateIsomorphism;
    }

    LiteralIsomorphism(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed, TermIsomorphism termIsomorphism, DerivedPredicateIsomorphism derivedPredicateIsomorphism) {
        this(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed, termIsomorphism, derivedPredicateIsomorphism, new BiMap<>());
    }

    LiteralIsomorphism(LiteralIsomorphism literalIsomorphism) {
        this(
                literalIsomorphism.changeVariableNamesAllowed,
                literalIsomorphism.changeLiteralOrderAllowed,
                literalIsomorphism.changingDerivedPredicateNameAllowed,
                new TermIsomorphism(literalIsomorphism.initialTermIsomorphism),
                new DerivedPredicateIsomorphism(literalIsomorphism.initialDerivedPredicateIsomorphism),
                literalIsomorphism.map
        );
    }

    LiteralIsomorphism(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed) {
        this(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed,
                new TermIsomorphism(changeVariableNamesAllowed),
                new DerivedPredicateIsomorphism(changeLiteralOrderAllowed, changeVariableNamesAllowed, changingDerivedPredicateNameAllowed),
                new BiMap<>()
        );
    }

    Set<Map.Entry<Literal, Literal>> entrySet() {
        return map.entrySet();
    }

    void add(Literal l1, Literal l2) {
        if (canBeIsomorphic(l1, l2)) {
            this.map.put(l1, l2);
        } else throw new RuntimeException("Cannot map " + l1 + " to " + l2);
    }

    boolean canBeIsomorphic(Literal l1, Literal l2) {
        if (this.containsInRange(l2)) return false;
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            return canBeIsomorphic(ol1, ol2);
        } else if (l1 instanceof BuiltInLiteral bl1 && l2 instanceof BuiltInLiteral bl2) {
            return canBeIsomorphic(bl1, bl2);
        } else if (!l1.getClass().getName().equals(l2.getClass().getName())) {
            return false;
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    @SuppressWarnings("unchecked")
    private <L extends Literal> L get(L l) {
        return (L) map.get(l);
    }

    private boolean containsInDomain(Literal l) {
        return map.containsKey(l);
    }

    private boolean containsInRange(Literal l) {
        return map.containsValue(l);
    }

    private boolean termsAreCompatibleWithIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
        if (changeVariableNamesAllowed) {
            TermIsomorphism termIsomorphism = computeTermIsomorphism();
            return termIsomorphism.canIncludeIntoIsomorphism(terms1, terms2);
        } else {
            return terms1.hasSameTerms(terms2);
        }
    }

    private TermIsomorphism computeTermIsomorphism() {
        TermIsomorphism result = new TermIsomorphism(initialTermIsomorphism);
        for (Map.Entry<Literal, Literal> entry : this.map.entrySet()) {
            addTermsIntoTermIsomorphism(entry.getKey(), entry.getValue(), result);
        }
        return result;
    }

    private void addTermsIntoTermIsomorphism(Literal l1, Literal l2, TermIsomorphism termIsomorphism) {
        for (int i = 0; i < l1.getArity(); ++i) {
            Term t1 = l1.getTerms().get(i);
            Term t2 = l2.getTerms().get(i);
            termIsomorphism.put(t1, t2);
        }
    }

    /**
     * Precondition:
     * - ol1 should not map to ol2
     *
     * @param ol1 is an ordinary literal
     * @param ol2 is an ordinary literal
     */
    private boolean canBeIsomorphic(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        if (ol1.getArity() != ol2.getArity()) return false;
        if (ol1.isBase() != ol2.isBase()) return false;
        if (ol1.isBase() && !haveSamePredicateNames(ol1, ol2)) return false;
        if (ol1.isDerived()) {
            if (!checkDerivedPredicatesAreIsomorphic(ol1.getPredicate(), ol2.getPredicate())) return false;
        }
        if (!haveSamePolarity(ol1, ol2)) return false;
        if (haveDifferentMap(ol1, ol2)) return false;
        return termsAreCompatibleWithIsomorphism(ol1.getTerms(), ol2.getTerms());
    }

    private boolean canBeIsomorphic(BuiltInLiteral bl1, BuiltInLiteral bl2) {
        if (bl1 instanceof ComparisonBuiltInLiteral cbl1 && bl2 instanceof ComparisonBuiltInLiteral cbl2) {
            return canBeIsomorphic(cbl1, cbl2);
        } else if (bl1 instanceof BooleanBuiltInLiteral bbl1 && bl2 instanceof BooleanBuiltInLiteral bbl2) {
            return canBeIsomorphic(bbl1, bbl2);
        } else if (bl1 instanceof CustomBuiltInLiteral cbl1 && bl2 instanceof CustomBuiltInLiteral cbl2) {
            return canBeIsomorphic(cbl1, cbl2);
        } else if (!bl1.getClass().getName().equals(bl2.getClass().getName())) {
            return false;
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    private boolean canBeIsomorphic(ComparisonBuiltInLiteral cbl1, ComparisonBuiltInLiteral cbl2) {
        ComparisonOperator operator1 = cbl1.getOperator();
        ComparisonOperator operator2 = cbl2.getOperator();
        if (operator1.equals(operator2)) {
            if (ComparisonOperator.EQUALS.equals(operator1) || ComparisonOperator.NOT_EQUALS.equals(operator1)) {
                return termsAreCompatibleWithIsomorphism(cbl1.getTerms(), cbl2.getTerms())
                        || termsAreCompatibleWithIsomorphism(cbl1.getTerms(), reverseTerms(cbl2.getTerms()));
            } else {
                return termsAreCompatibleWithIsomorphism(cbl1.getTerms(), cbl2.getTerms());
            }
        } else if (operator1.isSymmetric(operator2)) {
            return termsAreCompatibleWithIsomorphism(cbl1.getTerms(), reverseTerms(cbl2.getTerms()));
        }
        return false;
    }

    private static ImmutableTermList reverseTerms(ImmutableTermList terms2) {
        LinkedList<Term> auxTermsToReverse = new LinkedList<>(terms2);
        Collections.reverse(auxTermsToReverse);
        return new ImmutableTermList(auxTermsToReverse);
    }

    private boolean canBeIsomorphic(BooleanBuiltInLiteral bbl1, BooleanBuiltInLiteral bbl2) {
        return bbl1.isTrue() && bbl2.isTrue() || bbl1.isFalse() && bbl2.isFalse();
    }

    private boolean canBeIsomorphic(CustomBuiltInLiteral cbl1, CustomBuiltInLiteral cbl2) {
        if (!cbl1.getOperationName().equals(cbl2.getOperationName())) return false;
        return termsAreCompatibleWithIsomorphism(cbl1.getTerms(), cbl2.getTerms());
    }

    private boolean checkDerivedPredicatesAreIsomorphic(Predicate p1, Predicate p2) {
        //Check derivation rules, taking into account that, maybe, we have currently mapped two other literals with such predicates
        DerivedPredicateIsomorphism derivedPredicateIsomorphism = new DerivedPredicateIsomorphism(changeLiteralOrderAllowed,
                changeVariableNamesAllowed,
                changingDerivedPredicateNameAllowed,
                this);
        return derivedPredicateIsomorphism.canDerivedPredicatesBeIsomorphic(p1, p2);
    }

    private boolean haveDifferentMap(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        if (containsInDomain(ol1)) {
            return !get(ol1).equals(ol2);
        }
        if (containsInRange(ol2)) {
            return !get(ol2).equals(ol1);
        }
        return false;
    }

    private static boolean haveSamePolarity(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.isPositive() == ol2.isPositive();
    }

    private static boolean haveSamePredicateNames(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.getPredicateName().equals(ol2.getPredicateName());
    }


    DerivedPredicateIsomorphism getInitialDerivedPredicateIsomorphism() {
        return this.initialDerivedPredicateIsomorphism;
    }
}
