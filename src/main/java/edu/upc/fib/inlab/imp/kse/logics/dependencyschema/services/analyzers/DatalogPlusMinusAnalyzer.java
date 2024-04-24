package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;
import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers.egds.NonConflictingEGDsAnalyzer;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DatalogPlusMinusAnalyzer {


    private static final Map<DatalogPlusMinusLanguage, DatalogPlusMinusLanguageChecker> datalogCheckers;

    static {
        datalogCheckers = new LinkedHashMap<>();
        datalogCheckers.put(DatalogPlusMinusLanguage.LINEAR, new LinearChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.GUARDED, new GuardedChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.WEAKLY_GUARDED, new WeaklyGuardedChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.STICKY, new StickyChecker());
    }

    /**
     * Method responsible to check if the set of EGDs is non-conflicting from the set of TGDs according to the paper
     * "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for New Applications" published in
     * 2010 25th Annual IEEE Symposium on Logic in Computer Science
     *
     * @param schema not null
     * @return whether the egds of this schema are non-conflicting with the TGDs
     */
    public boolean areEGDsNonConflictingWithTGDs(DependencySchema schema) {
        return new NonConflictingEGDsAnalyzer().areEGDsNonConflictingWithTGDs(schema);
    }

    public Set<DatalogPlusMinusLanguage> getDatalogPlusMinusLanguages(DependencySchema dependencySchema) {
        Set<DatalogPlusMinusLanguage> result = new LinkedHashSet<>();
        for (DatalogPlusMinusLanguageChecker datalogChecker : datalogCheckers.values()) {
            if (datalogChecker.satisfies(dependencySchema)) {
                result.add(datalogChecker.getDatalogPlusMinusName());
            }
        }
        return result;
    }

    public enum DatalogPlusMinusLanguage {LINEAR, GUARDED, WEAKLY_GUARDED, STICKY;}
}
