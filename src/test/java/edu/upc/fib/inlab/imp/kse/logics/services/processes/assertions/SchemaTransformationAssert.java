package edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions;

import edu.upc.fib.inlab.imp.kse.logics.schema.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.SchemaTransformation;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SchemaTransformationAssert extends AbstractAssert<SchemaTransformationAssert, SchemaTransformation> {

    public SchemaTransformationAssert(SchemaTransformation schemaTransformation) {
        super(schemaTransformation, SchemaTransformationAssert.class);
    }

    public static SchemaTransformationAssert assertThat(SchemaTransformation actual) {
        return new SchemaTransformationAssert(actual);
    }

    public SchemaTransformationAssert constraintIDComesFrom(String originalID, String finalID) {
        Assertions.assertThat(actual.getOriginalConstraintID(new ConstraintID(finalID)).id()).isEqualTo(originalID);
        return this;
    }
}
