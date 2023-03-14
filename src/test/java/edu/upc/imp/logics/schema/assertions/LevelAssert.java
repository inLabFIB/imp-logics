package edu.upc.imp.logics.schema.assertions;

import edu.upc.imp.logics.schema.Predicate;
import edu.upc.imp.logics.schema.utils.Level;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;


public class LevelAssert extends AbstractAssert<LevelAssert, Level> {

    public LevelAssert(Level actual) {
        super(actual, LevelAssert.class);
    }

    public static LevelAssert assertThat(Level actual) {
        return new LevelAssert(actual);
    }

    public LevelAssert containsExactlyPredicateNames(String... expectedPredicateNames) {
        List<String> actualPredicateNamesList = actual.getAllPredicates().stream()
                .map(Predicate::getName)
                .toList();
        Assertions.assertThat(actualPredicateNamesList).containsExactlyInAnyOrder(expectedPredicateNames);
        return this;
    }


}
