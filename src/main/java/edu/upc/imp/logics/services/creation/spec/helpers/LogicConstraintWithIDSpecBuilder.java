package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;

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
        this(new DefaultStringToTermSpecFactory());
    }

    public LogicConstraintWithIDSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithIDSpecBuilder addConstraintId(String id) {
        this.id = id;
        return this;
    }

    public LogicConstraintWithIDSpec build() {
        return new LogicConstraintWithIDSpec(id, bodySpec);
    }
}
