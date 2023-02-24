package edu.upc.imp.logics.services.creation.spec.helpers;

import edu.upc.imp.logics.services.creation.spec.LogicConstraintWithoutIDSpec;

public class LogicConstraintWithoutIDSpecBuilder extends NormalClauseSpecBuilder<LogicConstraintWithoutIDSpecBuilder> {

    /**
     * Creates a new LogicConstraintWithoutIDSpecBuilder using a DefaultStringToTermSpecFactory to distinguish
     * the kind of Term to instantiate for the given term names.
     */
    public LogicConstraintWithoutIDSpecBuilder() {
        this(new DefaultStringToTermSpecFactory());
    }

    public LogicConstraintWithoutIDSpecBuilder(StringToTermSpecFactory stringToTermSpecFactory) {
        super(stringToTermSpecFactory);
    }

    public LogicConstraintWithoutIDSpec build() {
        return new LogicConstraintWithoutIDSpec(bodySpec);
    }
}
