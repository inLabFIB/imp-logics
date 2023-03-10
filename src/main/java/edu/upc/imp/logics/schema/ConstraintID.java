package edu.upc.imp.logics.schema;

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
}

