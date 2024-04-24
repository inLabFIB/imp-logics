package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithoutIDSpec;

/**
 * Builder to facilitate the creation of LogicConstraintWithoutIDs.
 */
public class LogicConstraintWithoutIDSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintWithoutIDSpecBuilder> {

    /**
     * Creates a new LogicConstraintWithoutIDSpecBuilder using a DefaultStringToTermSpecFactory to distinguish the kind
     * of Term to instantiate for the given term names.
     */
    public LogicConstraintWithoutIDSpecBuilder() {
        this(new AllVariableTermTypeCriteria());
    }

    public LogicConstraintWithoutIDSpecBuilder(TermTypeCriteria termTypeCriteria) {
        super(termTypeCriteria);
    }

    public LogicConstraintWithoutIDSpec build() {
        return new LogicConstraintWithoutIDSpec(bodySpec);
    }
}
