package edu.upc.imp.logics.services.creation.spec;

import java.util.List;
import java.util.Objects;

public class LogicConstraintWithIDSpec extends LogicConstraintSpec {

    private final String id;

    public LogicConstraintWithIDSpec(String id, BodySpec body) {
        super(body);
        if (Objects.isNull(id)) throw new IllegalArgumentException("Id cannot be null");
        this.id = id;
    }

    public LogicConstraintWithIDSpec(String id, List<LiteralSpec> bodyLiterals) {
        this(id, new BodySpec(bodyLiterals));
    }

    public String getId() {
        return id;
    }
}
