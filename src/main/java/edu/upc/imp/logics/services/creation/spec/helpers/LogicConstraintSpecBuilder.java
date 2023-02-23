package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintSpec;

public class LogicConstraintSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintSpecBuilder> {
    private String id;

    public LogicConstraintSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintSpecBuilder addConstraintId(String id) {
        this.id = id;
        return this;
    }

    public LogicConstraintSpec build() {
        return new LogicConstraintSpec(id, bodySpec);
    }
}
