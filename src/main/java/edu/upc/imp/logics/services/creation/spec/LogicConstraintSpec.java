package edu.upc.imp.logics.services.creation.spec;

import java.util.List;

public class LogicConstraintSpec extends NormalClauseSpec implements LogicElementSpec {
    private final String id;

    public LogicConstraintSpec(String id, BodySpec body) {
        super(body);
        this.id = id;
    }

    public LogicConstraintSpec(String id, List<LiteralSpec> bodyLiterals) {
        this(id, new BodySpec(bodyLiterals));
    }

    public String getId() {
        return id;
    }
}
