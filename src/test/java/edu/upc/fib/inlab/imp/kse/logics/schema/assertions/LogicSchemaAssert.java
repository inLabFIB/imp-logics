package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.Predicate;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.Set;

public class LogicSchemaAssert extends AbstractAssert<LogicSchemaAssert, LogicSchema> {
    public LogicSchemaAssert(LogicSchema logicSchema) {
        super(logicSchema, LogicSchemaAssert.class);
    }

    public static LogicSchemaAssert assertThat(LogicSchema actual) {
        return new LogicSchemaAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsPredicate(String predicateName, int arity) {
        Assertions.assertThat(actual.getAllPredicates())
                .anySatisfy(predicate -> PredicateAssert.assertThat(predicate)
                        .hasName(predicateName)
                        .hasArity(arity));
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsConstraintID(String constraintID) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .anySatisfy(constraint -> LogicConstraintAssert.assertThat(constraint)
                        .hasID(constraintID));
        return this;
    }

    public LogicSchemaAssert containsExactlyThesePredicateNames(String... predicateNames) {
        Assertions.assertThat(actual.getAllPredicates())
                .map(Predicate::getName)
                .containsExactlyInAnyOrder(predicateNames);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert containsExactlyTheseConstraintIDs(String... constraintIDs) {
        Assertions.assertThat(actual.getAllLogicConstraints())
                .map(LogicConstraint::getID)
                .map(ConstraintID::id)
                .containsExactlyInAnyOrder(constraintIDs);
        return this;
    }

    public void hasConstraintsSize(int size) {
        Assertions.assertThat(actual.getAllLogicConstraints()).hasSize(size);
    }


    /**
     * Asserts whether the actual logicSchema is equivalent to the expectedSchema.
     * This comparison is NOT agnostic with the name of the derived predicates.
     *
     * @param expectedSchema not-null
     * @return this assert
     */
    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert isLogicallyEquivalentTo(LogicSchema expectedSchema) {
        assertAllPredicatesAreEquivalent(expectedSchema);
        assertAllLogicConstraintsAreEquivalent(expectedSchema);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert assertAllLogicConstraintsAreEquivalent(LogicSchema expectedSchema) {
        for (LogicConstraint actualConstraint : actual.getAllLogicConstraints()) {
            boolean actualIsExpected = logicConstraintIsContainedInList(actualConstraint, expectedSchema.getAllLogicConstraints());
            if (!actualIsExpected) {
                Assertions.fail("Actual constraint \"" + actualConstraint + "\" is not expected");
            }
        }

        for (LogicConstraint expectedConstraint : expectedSchema.getAllLogicConstraints()) {
            boolean expectedIsFound = logicConstraintIsContainedInList(expectedConstraint, actual.getAllLogicConstraints());
            if (!expectedIsFound) {
                Assertions.fail("Expected constraint \"" + expectedConstraint + "\" is missing");
            }
        }

        return this;
    }

    private boolean logicConstraintIsContainedInList(LogicConstraint constraint, Set<LogicConstraint> constraintSet) {
        LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new LogicEquivalenceAnalyzer();
        for (LogicConstraint aConstraintFromSet : constraintSet) {
            if (logicEquivalenceAnalyzer.areEquivalent(constraint, aConstraintFromSet)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicSchemaAssert assertAllPredicatesAreEquivalent(LogicSchema expectedSchema) {
        for (Predicate actualPredicate : actual.getAllPredicates()) {
            try {
                Predicate expectedPredicate = expectedSchema.getPredicateByName(actualPredicate.getName());
                PredicateAssert.assertThat(actualPredicate).isLogicallyEquivalentTo(expectedPredicate);
            } catch (PredicateNotExists predicateNotExists) {
                Assertions.fail("Actual predicate " + actualPredicate.getName() + " is not expected");
            }
        }

        for (Predicate expectedPredicate : expectedSchema.getAllPredicates()) {
            try {
                actual.getPredicateByName(expectedPredicate.getName());
            } catch (PredicateNotExists predicateNotExists) {
                Assertions.fail("Missing expected predicate " + expectedPredicate.getName());
            }
        }

        return this;
    }

    public LogicSchemaAssert isEmpty() {
        Assertions.assertThat(actual.isEmpty())
                .describedAs("Actual logic schema is not empty")
                .isTrue();
        return this;
    }
}
