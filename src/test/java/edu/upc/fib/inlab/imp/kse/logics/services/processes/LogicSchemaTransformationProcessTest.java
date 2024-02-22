package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LogicSchemaTransformationProcessTest {

    @Test
    void should_throwException_whenExecuteWithNullSchema() {
        DummyProcess dummyProcess = new DummyProcess();
        LogicSchema logicSchema = null;
        assertThatThrownBy(() -> dummyProcess.execute(logicSchema))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static class DummyProcess extends LogicSchemaTransformationProcess {
        @Override
        public SchemaTransformation executeTransformation(LogicSchema logicSchema) {
            return null;
        }
    }
}