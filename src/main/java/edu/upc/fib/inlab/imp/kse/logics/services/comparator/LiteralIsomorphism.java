package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.exceptions.LiteralAlreadyMappedInIsomorphismException;

import java.util.Map;

public class LiteralIsomorphism {
    private final BiMap<Literal, Literal> map;
    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    public LiteralIsomorphism(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed, LiteralIsomorphism literalIsomorphism) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
        this.map = new BiMap<>(literalIsomorphism.map);
    }

    public LiteralIsomorphism(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
        this.map = new BiMap<>();
    }

    //TODO: this add operation assumes that, previously, we have ensured that l1 and l2 are compatible
    // (e.g., they have compatible predicate names, compatible polarities, etc)
    public void add(Literal l1, Literal l2) {
        if (map.containsKey(l1) || map.containsValue(l2))
            throw new LiteralAlreadyMappedInIsomorphismException(l1, l2);
        this.map.put(l1, l2);
    }

    public boolean containsInDomain(Literal l) {
        return map.containsKey(l);
    }

    public boolean containsInRange(Literal l) {
        return map.containsValue(l);
    }

    public boolean termsAreCompatibleWithIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
        TermIsomorphism termIsomorphism = computeTermIsomorphism();
        return termIsomorphism.termsAreCompatibleWithIsomorphism(terms1, terms2);
    }

    private TermIsomorphism computeTermIsomorphism() {
        TermIsomorphism termIsomorphism = new TermIsomorphism();
        for (Map.Entry<Literal, Literal> entry : this.map.entrySet()) {
            addTermsIntoTermIsomorphism(entry.getKey(), entry.getValue(), termIsomorphism);
        }
        return termIsomorphism;
    }

    private void addTermsIntoTermIsomorphism(Literal l1, Literal l2, TermIsomorphism termIsomorphism) {
        for (int i = 0; i < l1.getArity(); ++i) {
            Term t1 = l1.getTerms().get(i);
            Term t2 = l2.getTerms().get(i);
            termIsomorphism.put(t1, t2);
        }
    }

    //TODO: I need to call comparator. I am not sure whether passing it as argument is the best option or not.
    //If I do not pass it by parameter, I have to pass all the comparator parameters to ensure the consistency
    //when searching for the isomorphism
    public boolean predicatesAreCompatibleWithIsomorphism(Predicate predicate1, Predicate predicate2, IsomorphismComparator comparator) {
        DerivedPredicateIsomorphism predicateIsomorphism = computePredicateIsomorphism(comparator);
        return predicateIsomorphism.predicateAreCompatibleWithIsomorphism(predicate1, predicate2);
    }

    private DerivedPredicateIsomorphism computePredicateIsomorphism(IsomorphismComparator comparator) {
        DerivedPredicateIsomorphism result = new DerivedPredicateIsomorphism(comparator);
        for (Map.Entry<Literal, Literal> entry : this.map.entrySet()) {
            Literal l1 = entry.getKey();
            Literal l2 = entry.getValue();
            if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
                if (ol1.isDerived()) {
                    result.put(ol1.getPredicate(), ol2.getPredicate());
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public <L extends Literal> L get(L l) {
        return (L) map.get(l);
    }

    boolean canBeIsomorphic(Literal l1, Literal l2) {
        if (this.containsInDomain(l1) && !map.get(l1).equals(l2)) return false;
        if (this.containsInRange(l2)) return false;
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            return canBeIsomorphic(ol1, ol2);
        } else {
            throw new RuntimeException("To be implemented");
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


//            if (changingDerivedPredicateNameAllowed) {
//                if (!this.predicatesAreCompatibleWithIsomorphism(ol1.getPredicate(), ol2.getPredicate(), ))
//                    return false;
//            } else {
//                if (!haveSamePredicateNames(ol1, ol2)) return false;
//            }
        }
        if (!haveSamePolarity(ol1, ol2)) return false;
        if (haveDifferentMap(ol1, ol2)) return false;
        if (changeVariableNamesAllowed) {
            return termsAreCompatibleWithIsomorphism(ol1.getTerms(), ol2.getTerms());
        } else {
            return ol1.getTerms().hasSameTerms(ol2.getTerms());
        }
    }

    private boolean checkDerivedPredicatesAreIsomorphic(Predicate p1, Predicate p2) {
        //Check names
        if (!p1.getName().equals(p2.getName())) return false;
        //Check derivation rules, taking into account that, maybe, we have currently mapped two other literals with such predicates
        return false;
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


    /**
     * This class is responsible to remember the bidirectional correspondence between derived predicates that exists,
     * implicitly, in the LiteralIsomorphism
     */
    private static class DerivedPredicateIsomorphism {
        private final BiMap<Predicate, Predicate> map;
        private final IsomorphismComparator comparator;

        public DerivedPredicateIsomorphism(IsomorphismComparator comparator) {
            this.comparator = comparator;
            this.map = new BiMap<>();

        }

        public DerivedPredicateIsomorphism(DerivedPredicateIsomorphism derivedPredicateIsomorphism) {
            this.comparator = derivedPredicateIsomorphism.comparator;
            this.map = new BiMap<>(derivedPredicateIsomorphism.map);
        }

        public void put(Predicate p1, Predicate p2) {
            if (!haveIsomorphicDerivationRules(p1, p2))
                throw new RuntimeException("Derived predicates have no isomorphic rules");
            if (map.containsKey(p1)) {
                if (!map.get(p1).getName().equals(p2.getName()))
                    throw new RuntimeException("Cannot map a predicate, twice, to a different predicate");
            } else if (map.containsValue(p2)) {
                throw new RuntimeException("Cannot map a predicate, twice, to a different predicate");
            }
        }

        private boolean haveIsomorphicDerivationRules(Predicate p1, Predicate p2) {
            //TODO: implement this method
            return false;
        }

        public boolean predicateAreCompatibleWithIsomorphism(Predicate predicate1, Predicate predicate2) {
            try {
                DerivedPredicateIsomorphism newTermIsomorphism = new DerivedPredicateIsomorphism(this);
                newTermIsomorphism.put(predicate1, predicate2);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }


    /**
     * This class is responsible to remember the bidirectional correspondence between variables that exists,
     * implicitly, in the LiteralIsomorphism.
     */
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
                        throw new RuntimeException("Cannot map a variable, twice, to a different variable");
                } else if (map.containsValue(t2)) {
                    throw new RuntimeException("Cannot map a variable, twice, to a different variable");
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
