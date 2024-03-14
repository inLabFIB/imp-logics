package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.services.analyzers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.DependencySchema;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class DatalogPlusMinusAnalyzer {
    public enum DatalogPlusMinusLanguage {LINEAR, GUARDED, WEAKLY_GUARDED, STICKY}

    private static final Map<DatalogPlusMinusLanguage, DatalogPlusMinusLanguageChecker> datalogCheckers;

    static {
        datalogCheckers = new LinkedHashMap<>();
        datalogCheckers.put(DatalogPlusMinusLanguage.LINEAR, new LinearChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.GUARDED, new GuardedChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.WEAKLY_GUARDED, new WeaklyGuardedChecker());
        datalogCheckers.put(DatalogPlusMinusLanguage.STICKY, new StickyChecker());
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
}
