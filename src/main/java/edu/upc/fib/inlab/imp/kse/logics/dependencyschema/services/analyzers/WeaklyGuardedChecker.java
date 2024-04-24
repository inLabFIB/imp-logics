package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.NonConflictingEGDsAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WeaklyGuardedChecker extends DatalogPlusMinusLanguageChecker {

    /**
     * @param tgd               not null
     * @param affectedPositions not null, might be empty
     * @return whether the given tgd is weakly acyclic according to the given set of affected positions
     */
    static boolean isWeaklyGuarded(TGD tgd, Set<PredicatePosition> affectedPositions) {
        Set<Variable> universalVars = tgd.getUniversalVariables();
        List<Variable> affectedVars = universalVars.stream().filter(u ->
                                                                            affectedPositions.containsAll(tgd.getBody().getPredicatePositionsWithVar(u)))
                .toList();

        //Searching the guard
        for (Literal lit : tgd.getBody()) {
            if (lit instanceof OrdinaryLiteral &&
                    lit.getTerms().containsAll(affectedVars)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method implements the affected positions definition given in IEEE Symposion on Logic in Computer Science
     * 2010 "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for New Applications" by Cali,
     * et al.
     *
     * @return those predicate positions that might contain null values when chasing the schema dependencies.
     */
    public static Set<PredicatePosition> getAffectedPositions(DependencySchema dependencySchema) {
        Set<PredicatePosition> positionsWithExistsVars = getPositionsWithExistentialVars(dependencySchema);
        return getAffectedPositions(dependencySchema, positionsWithExistsVars);
    }

    private static Set<PredicatePosition> getPositionsWithExistentialVars(DependencySchema dependencySchema) {
        Set<PredicatePosition> result = new HashSet<>();
        for (TGD tgd : dependencySchema.getAllTGDs()) {
            Set<Variable> existentialVariables = tgd.getExistentialVariables();
            if (existentialVariables.isEmpty()) continue;

            for (Atom headAtom : tgd.getHead()) {
                for (int position = 0; position < headAtom.getPredicate().getArity(); ++position) {
                    Term term = headAtom.getTerms().get(position);
                    if (term instanceof Variable variable && existentialVariables.contains(variable)) {
                        result.add(new PredicatePosition(headAtom.getPredicate(), position));
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method computes the affected positions of this schema by saturating the set of affectedPositions. That is,
     * it recursively keeps adding predicatePositions to affectedPositions until no more predicatePositions can be
     * added. When no more predicatePositions can be added, the algorithm finishes
     *
     * @param affectedPositions not null
     * @return the set of affected positions given the initial set of affected positions
     */
    private static Set<PredicatePosition> getAffectedPositions(DependencySchema dependencySchema, Set<PredicatePosition> affectedPositions) {
        Set<PredicatePosition> newAffectedPositions = new HashSet<>(affectedPositions);
        for (Dependency dependency : dependencySchema.getAllDependencies()) {
            if (dependency instanceof TGD tgd) {
                Set<Variable> frontierVariables = tgd.getFrontierVariables();
                for (Variable variable : frontierVariables) {
                    Set<PredicatePosition> bodyPositions = tgd.getBody().getPredicatePositionsWithVar(variable);
                    if (affectedPositions.containsAll(bodyPositions)) {
                        newAffectedPositions.addAll(tgd.getHead().getPredicatePositionsWithVar(variable));
                    }
                }
            }
        }
        if (!affectedPositions.containsAll(newAffectedPositions))
            return getAffectedPositions(dependencySchema, newAffectedPositions);
        else return newAffectedPositions;
    }

    public boolean isWeaklyGuarded(DependencySchema dependencySchema) {
        return satisfies(dependencySchema);
    }

    @Override
    public boolean satisfies(DependencySchema dependencySchema) {
        if (someDependencyContainsBuiltInOrNegatedLiteralInBody(dependencySchema)) {
            throw new UnsupportedOperationException("Weakly guarded analysis does not currently support negated nor built-in literals");
        }
        if (!new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(dependencySchema)) return false;

        Set<PredicatePosition> affectedPositions = getAffectedPositions(dependencySchema);
        for (TGD tgd : dependencySchema.getAllTGDs()) {
            if (!isWeaklyGuarded(tgd, affectedPositions)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName() {
        return DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage.WEAKLY_GUARDED;
    }


}
