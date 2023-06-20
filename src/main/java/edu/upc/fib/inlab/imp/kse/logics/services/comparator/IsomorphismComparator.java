package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.OrdinaryLiteral;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IsomorphismComparator {

    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    public IsomorphismComparator(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
    }

    public boolean isIsomorphic(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2) {
        return computeIsomorphismRecursive(literals1, literals2, new LiteralIsomorphism()).isPresent();
    }

    private Optional<LiteralIsomorphism> computeIsomorphismRecursive(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2, LiteralIsomorphism literalIsomorphism) {
        if (literals1.isEmpty() && literals2.isEmpty()) return Optional.of(new LiteralIsomorphism());
        else {
            Literal l1 = literals1.get(0);
            List<Literal> secondLiteralCandidates = obtainLiteralCandidates(l1, literals2, literalIsomorphism);
            for (Literal l2 : secondLiteralCandidates) {
                LiteralIsomorphism newLiteralIsomorphism = createNewIsomorphism(l1, l2, literalIsomorphism);
                Optional<LiteralIsomorphism> isomorphicRecursive = computeIsomorphismRecursive(
                        newListRemovingLiteral(literals1, l1),
                        newListRemovingLiteral(literals2, l2),
                        newLiteralIsomorphism);
                if (isomorphicRecursive.isPresent()) return isomorphicRecursive;
            }
            return Optional.empty();
        }
    }

    private List<Literal> obtainLiteralCandidates(Literal l1, ImmutableLiteralsList literals2, LiteralIsomorphism literalIsomorphism) {
        List<Literal> secondLiteralCandidates = new ArrayList<>();
        if (!changeLiteralOrderAllowed) {
            Literal l2 = literals2.get(0);
            if (canBeIsomorphic(l1, l2, literalIsomorphism)) {
                secondLiteralCandidates.add(l2);
            }
            return secondLiteralCandidates;
        } else {
            for (Literal secondLiteral : literals2) {
                if (canBeIsomorphic(l1, secondLiteral, literalIsomorphism)) {
                    secondLiteralCandidates.add(secondLiteral);
                }
            }
            return secondLiteralCandidates;
        }
    }

    private boolean canBeIsomorphic(Literal l1, Literal l2, LiteralIsomorphism literalIsomorphism) {
        if (literalIsomorphism.containsInRange(l2)) return false;
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            return canBeIsomorphic(ol1, ol2, literalIsomorphism);
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    private boolean canBeIsomorphic(OrdinaryLiteral ol1, OrdinaryLiteral ol2, LiteralIsomorphism literalIsomorphism) {
        if (ol1.isBase() != ol2.isBase()) return false;
        if (ol1.isBase() && !haveSamePredicateNames(ol1, ol2)) return false;
        if (ol1.isDerived()) {
            if (changingDerivedPredicateNameAllowed) {
                if (!literalIsomorphism.predicatesAreCompatibleWithIsomorphism(ol1.getPredicate(), ol2.getPredicate()))
                    return false;
            } else {
                if (!haveSamePredicateNames(ol1, ol2)) return false;
            }
        }
        if (!haveSamePolarity(ol1, ol2)) return false;
        if (haveDifferentMap(ol1, ol2, literalIsomorphism)) return false;
        if (ol1.getArity() != ol2.getArity()) return false;
        if (changeVariableNamesAllowed) {
            return literalIsomorphism.termsAreCompatibleWithIsomorphism(ol1.getTerms(), ol2.getTerms());
        } else {
            return ol1.getTerms().hasSameTerms(ol2.getTerms());
        }
    }


    private static boolean haveDifferentMap(OrdinaryLiteral ol1, OrdinaryLiteral ol2, LiteralIsomorphism literalIsomorphism) {
        if (literalIsomorphism.containsInDomain(ol1)) {
            return !literalIsomorphism.get(ol1).equals(ol2);
        }
        if (literalIsomorphism.containsInRange(ol2)) {
            return !literalIsomorphism.get(ol2).equals(ol1);
        }
        return false;
    }


    private static boolean haveSamePolarity(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.isPositive() == ol2.isPositive();
    }


    private static boolean haveSamePredicateNames(OrdinaryLiteral ol1, OrdinaryLiteral ol2) {
        return ol1.getPredicateName().equals(ol2.getPredicateName());
    }

    private LiteralIsomorphism createNewIsomorphism(Literal l1, Literal l2, LiteralIsomorphism literalIsomorphism) {
        LiteralIsomorphism newLiteralIsomorphism = new LiteralIsomorphism(literalIsomorphism);
        newLiteralIsomorphism.add(l1, l2);
        return newLiteralIsomorphism;
    }

    private ImmutableLiteralsList newListRemovingLiteral(ImmutableLiteralsList literals, Literal l) {
        List<Literal> newLiteralList = new ArrayList<>(literals);
        newLiteralList.remove(l);
        return new ImmutableLiteralsList(newLiteralList);
    }


}
