package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;

public class LogicConstraintWithoutIDSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintWithoutIDSpecBuilder> {

    public LogicConstraintWithoutIDSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithoutIDSpec build() {
        return new LogicConstraintWithoutIDSpec(bodySpec);
    }
}
