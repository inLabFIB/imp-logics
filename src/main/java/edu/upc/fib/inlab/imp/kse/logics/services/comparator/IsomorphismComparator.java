package edu.upc.fib.inlab.imp.kse.logics.services.comparator;

import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableLiteralsList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;

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
        LiteralIsomorphism literalIsomorphism = newLiteralIsomorphism();
        return computeIsomorphismRecursive(literals1, literals2, literalIsomorphism).isPresent();
    }

    private Optional<LiteralIsomorphism> computeIsomorphismRecursive(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2, LiteralIsomorphism literalIsomorphism) {
        if (literals1.isEmpty() && literals2.isEmpty()) return Optional.of(newLiteralIsomorphism());
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
        if (changeLiteralOrderAllowed) {
            for (Literal l2 : literals2) {
                if (literalIsomorphism.canBeIsomorphic(l1, l2)) {
                    secondLiteralCandidates.add(l2);
                }
            }
        } else {
            Literal l2 = literals2.get(0);
            if (literalIsomorphism.canBeIsomorphic(l1, l2)) {
                secondLiteralCandidates.add(l2);
            }
        }
        return secondLiteralCandidates;
    }

    private LiteralIsomorphism createNewIsomorphism(Literal l1, Literal l2, LiteralIsomorphism literalIsomorphism) {
        LiteralIsomorphism newLiteralIsomorphism = newLiteralIsomorphism(literalIsomorphism);
        newLiteralIsomorphism.add(l1, l2);
        return newLiteralIsomorphism;
    }

    private ImmutableLiteralsList newListRemovingLiteral(ImmutableLiteralsList literals, Literal l) {
        List<Literal> newLiteralList = new ArrayList<>(literals);
        newLiteralList.remove(l);
        return new ImmutableLiteralsList(newLiteralList);
    }

    private LiteralIsomorphism newLiteralIsomorphism() {
        return new LiteralIsomorphism(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed);
    }

    private LiteralIsomorphism newLiteralIsomorphism(LiteralIsomorphism literalIsomorphism) {
        return new LiteralIsomorphism(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed, literalIsomorphism);
    }

}
