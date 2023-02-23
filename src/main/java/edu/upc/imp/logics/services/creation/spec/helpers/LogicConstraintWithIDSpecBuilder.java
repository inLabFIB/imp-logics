package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithIDSpec;

public class LogicConstraintWithIDSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintWithIDSpecBuilder> {
    private String id;

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
