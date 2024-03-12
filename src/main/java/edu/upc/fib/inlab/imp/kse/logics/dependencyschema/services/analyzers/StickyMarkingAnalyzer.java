package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class is the responsible to make the marking of literal positions of the TGDs
 * according to the 2010 25th Annual IEEE Symposium on Logic in Computer Science paper
 * "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for
 * New Applications"
 */
public class StickyMarkingAnalyzer {
    private StickyMarkingAnalyzer() {
    }

    /**
     * @param tgds not null, might be empty
     * @return the set of marked LiteralPositions according to the stickiness marking
     */
    public static Set<LiteralPosition> getStickyMarking(List<TGD> tgds) {
        if (Objects.isNull(tgds)) throw new IllegalArgumentException("List of tgds cannot be null");
        return getStickyMarkingPropagation(getInitialStickyMarking(tgds), tgds);
    }

    static Set<LiteralPosition> getInitialStickyMarking(List<TGD> tgds) {
        Set<LiteralPosition> initialMarking = new HashSet<>();
        for (TGD tgd : tgds) {
            initialMarking.addAll(getInitialStickyMarking(tgd));
        }
        return initialMarking;
    }

    private static Set<LiteralPosition> getInitialStickyMarking(TGD tgd) {
        Set<LiteralPosition> result = new HashSet<>();
        for (Literal lit : tgd.getBody()) {
            if (lit instanceof OrdinaryLiteral ordinaryLiteral) {
                for (int position = 0; position < ordinaryLiteral.getArity(); ++position) {
                    Term term = ordinaryLiteral.getTerms().get(position);
                    boolean isVarNotAppearingInSomeHead = isVarNotAppearingInSomeHead(tgd, term);

                    if (isVarNotAppearingInSomeHead) {
                        result.add(new LiteralPosition(lit, position));
                    }
                }
            }
        }
        return result;
    }

    private static boolean isVarNotAppearingInSomeHead(TGD tgd, Term term) {
        if (term instanceof Constant) return false;

        for (Atom atom : tgd.getHead()) {
            if (!atom.getTerms().contains(term)) {
                return true;
            }
        }

        return false;
    }

    static Set<LiteralPosition> getStickyMarkingPropagation(Set<LiteralPosition> initialMarking, List<TGD> tgDs) {
        for (TGD tgd : tgDs) {
            Set<LiteralPosition> newMarking = getStickyMarkingPropagation(tgd, initialMarking);
            if (newMarking.size() > initialMarking.size()) return getStickyMarkingPropagation(newMarking, tgDs);
        }
        return initialMarking;
    }

    private static Set<LiteralPosition> getStickyMarkingPropagation(TGD tgd, Set<LiteralPosition> initialMarking) {
        Set<LiteralPosition> result = new HashSet<>(initialMarking);
        for (Atom atomHead : tgd.getHead()) {
            for (int position = 0; position < atomHead.getTerms().size(); ++position) {
                Term term = atomHead.getTerms().get(position);
                if (term instanceof Variable variable &&
                        headPositionMatchesMarkedLiteralPosition(initialMarking, atomHead, position)) {
                    result.addAll(tgd.getBody().getLiteralPositionWithVariable(variable));
                }
            }
        }
        return result;
    }

    private static boolean headPositionMatchesMarkedLiteralPosition(Set<LiteralPosition> initialMarking, Atom atomHead, int position) {
        return initialMarking.stream().anyMatch(litPos -> litPos.literal() instanceof OrdinaryLiteral oLiteral &&
                oLiteral.getPredicateName().equals(atomHead.getPredicateName()) && litPos.position() == position);
    }
}
