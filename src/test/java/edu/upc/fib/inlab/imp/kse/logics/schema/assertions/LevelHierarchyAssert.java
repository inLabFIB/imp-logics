package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.utils.Level;
import edu.upc.fib.inlab.imp.kse.logics.schema.utils.LevelHierarchy;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class LevelHierarchyAssert extends AbstractAssert<LevelHierarchyAssert, LevelHierarchy> {

    public LevelHierarchyAssert(LevelHierarchy actual) {
        super(actual, LevelHierarchyAssert.class);
    }

    public static LevelHierarchyAssert assertThat(LevelHierarchy actual) {
        return new LevelHierarchyAssert(actual);
    }

    public LevelHierarchyAssert hasLevels(int expectedLevels) {
        Assertions.assertThat(actual.getNumberOfLevels()).isEqualTo(expectedLevels);
        return this;
    }

    public LevelHierarchyAssert containsExactlyPredicateNamesInLevel(int index, String... predicateNames) {
        Level actualLevel = actual.getLevel(index);
        LevelAssert.assertThat(actualLevel).containsExactlyPredicateNames(predicateNames);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LevelHierarchyAssert hasNoPredicateInLevel(int level) {
        Level actualLevel = actual.getLevel(level);
        LevelAssert.assertThat(actualLevel).isEmpty();
        return this;
    }
}
