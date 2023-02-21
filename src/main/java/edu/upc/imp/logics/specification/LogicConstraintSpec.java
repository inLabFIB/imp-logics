package edu.upc.imp.logics.specification;

import java.util.List;

public class LogicConstraintSpec extends NormalClauseSpec {
    private final String id;

    public LogicConstraintSpec(String id, List<LiteralSpec> body) {
        super(body);
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
