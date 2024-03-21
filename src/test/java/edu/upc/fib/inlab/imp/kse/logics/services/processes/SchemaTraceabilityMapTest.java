package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.services.processes.assertions.SchemaTraceabilityMapAssert;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.exceptions.MapsDoNotJoin;
import edu.upc.fib.inlab.imp.kse.logics.services.processes.mothers.SchemaTraceabilityMapMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SchemaTraceabilityMapTest {

    @Nested
    class JoinMapTests {
        @Nested
        class InputValidation {
            @Test
            public void should_throwIllegalArgumentException_whenCurrentMapIsNull() {
                SchemaTraceabilityMap emptyMap = SchemaTraceabilityMapMother
                        .createEmptyMap();
                assertThatThrownBy(() -> emptyMap.joinMap(null))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Test
        public void should_returnNewMap_whenJoiningMaps() {
            SchemaTraceabilityMap newMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1_1", "1_1"));
            SchemaTraceabilityMap oldMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1", "1"));

            SchemaTraceabilityMap joinMap = oldMap.joinMap(newMap);

            SchemaTraceabilityMapAssert.assertThat(joinMap)
                    .isNotSameAs(newMap)
                    .isNotSameAs(oldMap);
        }

        @Test
        public void should_returnOriginalOfOriginalConstraint_whenJoiningMaps() {
            SchemaTraceabilityMap newMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1_1", "1_1"));
            SchemaTraceabilityMap oldMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1", "1"));

            SchemaTraceabilityMap joinMap = oldMap.joinMap(newMap);

            SchemaTraceabilityMapAssert.assertThat(joinMap)
                    .size(1)
                    .constraintIDComesFrom("1_1_1", "1");
        }

        @Test
        public void should_throwException_whenNewMapDoNotJoinOldMap() {
            SchemaTraceabilityMap newMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1_1", "1_1", "2_1_1", "2_1"));
            SchemaTraceabilityMap oldMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1", "1"));

            assertThatThrownBy(() -> oldMap.joinMap(newMap))
                    .isInstanceOf(MapsDoNotJoin.class);
        }

        @Test
        public void should_notThrowException_whenOldMapDoNotJoinNewMap() {
            SchemaTraceabilityMap newMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1_1", "1_1"));
            SchemaTraceabilityMap oldMap = SchemaTraceabilityMapMother
                    .create(Map.of("1_1", "1", "2_1_1", "2_1"));

            assertDoesNotThrow(() -> oldMap.joinMap(newMap));
        }

    }


}