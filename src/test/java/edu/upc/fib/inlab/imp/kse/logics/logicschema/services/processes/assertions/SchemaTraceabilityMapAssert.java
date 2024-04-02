package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.assertions;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ConstraintID;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.SchemaTraceabilityMap;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SchemaTraceabilityMapAssert extends AbstractAssert<SchemaTraceabilityMapAssert, SchemaTraceabilityMap> {
    public SchemaTraceabilityMapAssert(SchemaTraceabilityMap schemaTraceabilityMap) {
        super(schemaTraceabilityMap, SchemaTraceabilityMapAssert.class);
    }

    public static SchemaTraceabilityMapAssert assertThat(SchemaTraceabilityMap actual) {
        return new SchemaTraceabilityMapAssert(actual);
    }

    public SchemaTraceabilityMapAssert constraintIDComesFrom(String finalID, String originalID) {
        Assertions.assertThat(actual.getOriginalConstraintID(new ConstraintID(finalID)).id()).isEqualTo(originalID);
        return this;
    }

    public SchemaTraceabilityMapAssert size(int expectedSize) {
        Assertions.assertThat(actual.size()).isEqualTo(expectedSize);
        return this;
    }
}
