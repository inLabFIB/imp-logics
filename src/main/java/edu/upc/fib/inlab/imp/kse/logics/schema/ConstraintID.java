package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.Objects;

public record ConstraintID(String id) {
    public ConstraintID {
        if (Objects.isNull(id)) {
            throw new IllegalArgumentException("ConstraintID cannot be null");
        }
    }

    @Override
    public String toString() {
        return id;
    }

    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

