package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Order of derivation rules of a predicate is not important.
 */
public class IsomorphismComparator {

    private final boolean changeVariableNamesAllowed;
    private final boolean changeLiteralOrderAllowed;
    private final boolean changingDerivedPredicateNameAllowed;

    public IsomorphismComparator(boolean changeVariableNamesAllowed, boolean changeLiteralOrderAllowed, boolean changingDerivedPredicateNameAllowed) {
        this.changeVariableNamesAllowed = changeVariableNamesAllowed;
        this.changeLiteralOrderAllowed = changeLiteralOrderAllowed;
        this.changingDerivedPredicateNameAllowed = changingDerivedPredicateNameAllowed;
    }

    public boolean areIsomorphic(DerivationRule dr1, DerivationRule dr2) {
        Atom h1 = dr1.getHead();
        Atom h2 = dr2.getHead();
        if (!areDerivationRuleHeadsIsomorphic(h1, h2)) return false;
        LiteralIsomorphism literalIsomorphism = initializeLiteralIsomorphism(h1, h2);
        return computeIsomorphismRecursive(dr1.getBody(), dr2.getBody(), literalIsomorphism).isPresent();
    }

    private boolean areDerivationRuleHeadsIsomorphic(Atom h1, Atom h2) {
        if (!h1.getPredicateName().equals(h2.getPredicateName())) return false;
        return new TermIsomorphism().canIncludeIntoIsomorphism(h1.getTerms(), h2.getTerms());
    }

    public boolean areIsomorphic(ImmutableLiteralsList literals1, ImmutableLiteralsList literals2) {
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
                Optional<LiteralIsomorphism> isomorphicRecursive = computeIsomorphismRecursive(newListRemovingLiteral(literals1, l1), newListRemovingLiteral(literals2, l2), newLiteralIsomorphism);
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

    private LiteralIsomorphism initializeLiteralIsomorphism(Atom h1, Atom h2) {
        TermIsomorphism termIsomorphism = computeTermIsomorphism(h1.getTerms(), h2.getTerms());
        return newLiteralIsomorphism(termIsomorphism);
    }

    private TermIsomorphism computeTermIsomorphism(ImmutableTermList terms1, ImmutableTermList terms2) {
        TermIsomorphism result = new TermIsomorphism();
        for (int i = 0; i < terms1.size(); ++i) {
            result.put(terms1.get(i), terms2.get(i));
        }
        return result;
    }

    private LiteralIsomorphism newLiteralIsomorphism(TermIsomorphism termIsomorphism) {
        return new LiteralIsomorphism(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed, termIsomorphism);
    }

    private LiteralIsomorphism newLiteralIsomorphism() {
        return new LiteralIsomorphism(changeVariableNamesAllowed, changeLiteralOrderAllowed, changingDerivedPredicateNameAllowed);
    }

    private LiteralIsomorphism newLiteralIsomorphism(LiteralIsomorphism literalIsomorphism) {
        return new LiteralIsomorphism(literalIsomorphism);
    }
}
