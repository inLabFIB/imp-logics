package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.Dependency;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.EGDToFDAnalysisResult;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.EGDToFDAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.NonConflictingFDsAnalyzer;

public abstract class DatalogPlusMinusLanguageChecker {


    /**
     * Method responsible to check if the set of EGDs is non-conflicting from the set of TGDs according to
     * the paper "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for
     * New Applications" published in 2010 25th Annual IEEE Symposium on Logic in Computer Science
     *
     * @param dependencySchema not null
     * @return whether the egds of this schema are non-conflicting with the TGDs
     */
    public boolean areEGDsNonConflictingWithTGDs(DependencySchema dependencySchema) {
        EGDToFDAnalysisResult egdToFDAnalysisResult = new EGDToFDAnalyzer().analyze(dependencySchema.getAllEGDs());

        if (egdToFDAnalysisResult.allEGDsDefinesKeyDependencies()) {
            return new NonConflictingFDsAnalyzer().isNonConflicting(dependencySchema.getAllTGDs(), egdToFDAnalysisResult.getFunctionalDependencies());
        } else return false;
    }

    public abstract boolean satisfies(DependencySchema dependencySchema);

    public abstract DatalogPlusMinusAnalyzer.DatalogPlusMinusLanguage getDatalogPlusMinusName();

    protected static boolean someDependencyContainsBuiltInOrNegatedLiteralInBody(DependencySchema dependencySchema) {
        return dependencySchema.getAllDependencies().stream().anyMatch(Dependency::containsBuiltInOrNegatedLiteralInBody);
    }


}
