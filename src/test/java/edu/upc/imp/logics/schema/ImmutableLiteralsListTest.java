package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.assertions.ImmutableLiteralsListAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ImmutableLiteralsListTest {

    @Nested
    class CreateImmutableLiteralsList {
        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithElementNull() {
            List<Literal> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableLiteralsList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_throwException_whenTryCreateImmutableLiteralsListWithNull() {
            assertThatThrownBy(() -> new ImmutableLiteralsList((List<Literal>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        public void should_createImmutableLiteralsEmptyList() {
            assertThat(new ImmutableLiteralsList()).isEmpty();
        }

        @Test
        public void should_createEmptyLiteralList_whenCreatingImmutableLiteralList_withEmptyList() {
            ImmutableLiteralsList actualTermList = new ImmutableLiteralsList(List.of());
            ImmutableLiteralsListAssert.assertThat(actualTermList).isEmpty();
        }
    }


}