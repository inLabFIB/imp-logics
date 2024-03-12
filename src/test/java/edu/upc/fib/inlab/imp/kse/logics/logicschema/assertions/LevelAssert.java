package edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Level;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Predicate;
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

    @SuppressWarnings("UnusedReturnValue")
    public LevelAssert containsExactlyPredicateNames(String... expectedPredicateNames) {
        List<String> actualPredicateNamesList = actual.getAllPredicates().stream()
                .map(Predicate::getName)
                .toList();
        Assertions.assertThat(actualPredicateNamesList).containsExactlyInAnyOrder(expectedPredicateNames);
        return this;
    }


    @SuppressWarnings("UnusedReturnValue")
    public LevelAssert isEmpty() {
        Assertions.assertThat(actual.getAllPredicates()).isEmpty();
        return this;
    }
}
