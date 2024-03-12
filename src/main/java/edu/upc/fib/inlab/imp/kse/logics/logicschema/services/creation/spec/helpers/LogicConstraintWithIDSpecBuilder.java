package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LogicConstraintWithIDSpec;

/**
 * Builder to facilitate the creation of LogicConstraintWithIDs.
 */
public class LogicConstraintWithIDSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintWithIDSpecBuilder> {
    private String id;

    /**
     * Creates a new LogicConstraintWithIDSpecBuilder using a DefaultStringToTermSpecFactory to distinguish
     * the kind of Term to instantiate for the given term names.
     */
    public LogicConstraintWithIDSpecBuilder() {
        this(new AllVariableTermTypeCriteria());
    }

    public LogicConstraintWithIDSpecBuilder(TermTypeCriteria termTypeCriteria) {
        super(termTypeCriteria);
    }

    public LogicConstraintWithIDSpecBuilder addConstraintId(String id) {
        this.id = id;
        return this;
    }

    public LogicConstraintWithIDSpec build() {
        return new LogicConstraintWithIDSpec(id, bodySpec);
    }
}
