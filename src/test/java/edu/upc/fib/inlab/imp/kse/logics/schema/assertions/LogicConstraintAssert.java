package edu.upc.fib.inlab.imp.kse.logics.schema.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.LogicConstraint;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.HomomorphismBasedEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.comparator.LogicEquivalenceAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintSpec;
import edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.LogicConstraintWithIDSpec;
import org.assertj.core.api.Assertions;

import java.util.Optional;

public class LogicConstraintAssert extends NormalClauseAssert<LogicConstraint> {
    public LogicConstraintAssert(LogicConstraint logicConstraint) {
        super(logicConstraint, LogicConstraintAssert.class);
    }

    public static LogicConstraintAssert assertThat(LogicConstraint actual) {
        return new LogicConstraintAssert(actual);
    }

    @SuppressWarnings("UnusedReturnValue")
    public LogicConstraintAssert correspondsSpecWithId(LogicConstraintWithIDSpec spec) {
        Assertions.assertThat(actual.getID().id()).isEqualTo(spec.getId());
        return correspondsSpec(spec);
    }

    public LogicConstraintAssert correspondsSpec(LogicConstraintSpec spec) {
        Assertions.assertThat(actual.getBody()).hasSameSizeAs(spec.getBody());
        for (int i = 0; i < actual.getBody().size(); ++i) {
            Literal actualLit = actual.getBody().get(i);
            LiteralSpec litSpec = spec.getBody().get(i);
            LiteralAssert.assertThat(actualLit).correspondsSpec(litSpec);
        }
        return this;
    }

    public LogicConstraintAssert hasID(String id) {
        Assertions.assertThat(actual.getID().id()).isEqualTo(id);
        return this;
    }

    /**
     * Checks whether the actual constraint is the same as the expected logic constraint up-to renaming
     * variables, and derived predicate names.
     *
     * @param expected not-null
     * @return this assertion
     * @see LogicEquivalenceAnalyzer
     */
    @SuppressWarnings("unused")
    public LogicConstraintAssert isLogicallyEquivalent(LogicConstraint expected) {
        LogicEquivalenceAnalyzer logicEquivalenceAnalyzer = new HomomorphismBasedEquivalenceAnalyzer();

        Optional<Boolean> equivalenceResult = logicEquivalenceAnalyzer.areEquivalent(actual, expected);
        if (equivalenceResult.isPresent()) {
            Assertions.assertThat(equivalenceResult)
                    .overridingErrorMessage("Actual constraint: " + actual.toString() + "\n" +
                            "   is not equivalent to \n" +
                            "Expected constraint: " + expected.toString()
                    )
                    .contains(true);
        } else {
            Assertions.fail("Current logicEquivalenceAnalyzer: " + logicEquivalenceAnalyzer.getClass().getName() + "\n" +
                    " could not determine if actual constraint: " + actual.toString() + "\n" +
                    "   is equivalent to\n" +
                    "Expected constraint: " + expected.toString() + "\n");
        }
        return this;
    }

}
