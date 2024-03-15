package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.NonConflictingEGDsAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.LiteralPosition;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;

import java.util.Set;

public class StickyChecker extends DatalogPlusMinusLanguageChecker {

    public boolean isSticky(DependencySchema dependencySchema) {
        return satisfies(dependencySchema);
    }

    @Override
    public boolean satisfies(DependencySchema dependencySchema) {
        if (someDependencyContainsBuiltInOrNegatedLiteralInBody(dependencySchema)) {
            throw new UnsupportedOperationException("Sticky analysis does not currently support negated nor built-in literals");
        }
        if (!new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(dependencySchema)) return false;

        Set<LiteralPosition> stickyMarking = StickyMarkingAnalyzer.getStickyMarking(dependencySchema.getAllTGDs());

        for (TGD tgd : dependencySchema.getAllTGDs()) {
            if (hasVariableAppearingTwiceInMarkedPositions(tgd, stickyMarking)) {
                return false;
            }
        }

        return true;
    }

    private static boolean hasVariableAppearingTwiceInMarkedPositions(TGD tgd, Set<LiteralPosition> finalMarking) {
        for (Variable variable : tgd.getUniversalVariables()) {
            Set<LiteralPosition> positionsWithVar = tgd.getBody().getLiteralPositionWithVariable(variable);
            positionsWithVar.retainAll(finalMarking);
            if (positionsWithVar.size() > 1) return true;
        }
        return false;
    }

    @Override
    public DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName() {
        return DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.STICKY;
    }


}
