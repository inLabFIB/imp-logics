package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.SchemaTransformation;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SchemaTransformationAssert extends AbstractAssert<SchemaTransformationAssert, SchemaTransformation> {

    public SchemaTransformationAssert(SchemaTransformation schemaTransformation) {
        super(schemaTransformation, SchemaTransformationAssert.class);
    }

    public static SchemaTransformationAssert assertThat(SchemaTransformation actual) {
        return new SchemaTransformationAssert(actual);
    }

    public SchemaTransformationAssert constraintIDComesFrom(String finalID, String originalID) {
        Assertions.assertThat(actual.getOriginalConstraintID(new ConstraintID(finalID)))
                .isNotNull()
                .satisfies(actualOriginalID -> Assertions.assertThat(actualOriginalID.id()).isEqualTo(originalID));
        return this;
    }
}
