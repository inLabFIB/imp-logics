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

    public IsomorphismComparator(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
    }

    public boolean isIsomorphic(ImmutableLiteralsList literalListFirst, ImmutableLiteralsList literalListSecond) {
        return computeIsomorphismRecursive(literalListFirst, literalListSecond, new LiteralIsomorphism()).isPresent();
    }

    private Optional<LiteralIsomorphism> computeIsomorphismRecursive(ImmutableLiteralsList literalListFirst, ImmutableLiteralsList literalListSecond, LiteralIsomorphism literalIsomorphism) {
        if (literalListFirst.isEmpty() && literalListSecond.isEmpty()) return Optional.of(new LiteralIsomorphism());
        else {
            Literal firstLiteral = literalListFirst.get(0);
            List<Literal> secondLiteralCandidates = obtainLiteralCandidates(firstLiteral, literalListSecond, literalIsomorphism);
            for (Literal secondLiteral : secondLiteralCandidates) {
                LiteralIsomorphism newLiteralIsomorphism = createNewIsomorphism(literalIsomorphism, firstLiteral, secondLiteral);
                Optional<LiteralIsomorphism> isomorphicRecursive = computeIsomorphismRecursive(
                        newListRemovingLiteral(literalListFirst, firstLiteral),
                        newListRemovingLiteral(literalListSecond, secondLiteral),
                        newLiteralIsomorphism);
                if (isomorphicRecursive.isPresent()) return isomorphicRecursive;
            }
            return Optional.empty();
        }
    }

    private List<Literal> obtainLiteralCandidates(Literal firstLiteral, ImmutableLiteralsList literalListSecond, LiteralIsomorphism literalIsomorphism) {
        List<Literal> secondLiteralCandidates = new ArrayList<>();
        if (!changeLiteralOrderAllowed) {
            Literal secondLiteral = literalListSecond.get(0);
            if (canBeIsomorphic(firstLiteral, secondLiteral, literalIsomorphism)) {
                secondLiteralCandidates.add(secondLiteral);
            }
            return secondLiteralCandidates;
        } else {
            for (Literal secondLiteral : literalListSecond) {
                if (canBeIsomorphic(firstLiteral, secondLiteral, literalIsomorphism)) {
                    secondLiteralCandidates.add(secondLiteral);
                }
            }
            return secondLiteralCandidates;
        }
    }

    private boolean canBeIsomorphic(Literal firstLiteral, Literal secondLiteral, LiteralIsomorphism literalIsomorphism) {
        if (literalIsomorphism.containsInRange(secondLiteral)) return false;
        if (firstLiteral instanceof OrdinaryLiteral firstOl && secondLiteral instanceof OrdinaryLiteral secondOl) {
            return canBeIsomorphic(literalIsomorphism, firstOl, secondOl);
        } else {
            throw new RuntimeException("To be implemented");
        }
    }

    private static boolean canBeIsomorphic(LiteralIsomorphism literalIsomorphism, OrdinaryLiteral firstOl, OrdinaryLiteral secondOl) {
        if (!haveSamePredicateNames(firstOl, secondOl)) return false;
        if (!haveSamePolarity(firstOl, secondOl)) return false;
        if (haveDifferentMap(literalIsomorphism, firstOl, secondOl)) return false;
        return literalIsomorphism.termsAreCompatibleWithIsomorphism(firstOl.getTerms(), secondOl.getTerms());
    }

    private static boolean haveDifferentMap(LiteralIsomorphism literalIsomorphism, OrdinaryLiteral firstOl, OrdinaryLiteral secondOl) {
        if (literalIsomorphism.containsInDomain(firstOl)) {
            return !literalIsomorphism.get(firstOl).equals(secondOl);
        }
        if (literalIsomorphism.containsInRange(secondOl)) {
            return !literalIsomorphism.get(secondOl).equals(firstOl);
        }
        return false;
    }


    private static boolean haveSamePolarity(OrdinaryLiteral firstOl, OrdinaryLiteral secondOl) {
        return firstOl.isPositive() == secondOl.isPositive();
    }


    private static boolean haveSamePredicateNames(OrdinaryLiteral firstOl, OrdinaryLiteral secondOl) {
        return firstOl.getPredicateName().equals(secondOl.getPredicateName());
    }

    private LiteralIsomorphism createNewIsomorphism(LiteralIsomorphism literalIsomorphism, Literal firstLiteral, Literal secondLiteral) {
        LiteralIsomorphism newLiteralIsomorphism = new LiteralIsomorphism(literalIsomorphism);
        newLiteralIsomorphism.add(firstLiteral, secondLiteral);
        return newLiteralIsomorphism;
    }

    private ImmutableLiteralsList newListRemovingLiteral(ImmutableLiteralsList literalList, Literal literal) {
        List<Literal> newLiteralList = new ArrayList<>(literalList);
        newLiteralList.remove(literal);
        return new ImmutableLiteralsList(newLiteralList);
    }


}
