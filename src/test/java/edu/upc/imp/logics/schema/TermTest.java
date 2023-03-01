package edu.upc.imp.logics.schema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TermTest {

    @ParameterizedTest
    @NullAndEmptySource
    void should_throwException_whenNameIsNullOrEmpty(String nullOrEmpty) {
        assertThatThrownBy(() -> new Term(nullOrEmpty) {
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_createTerm_whenNameIsNotNullNorEmpty() {
        assertThatCode(() -> new Term("x") {
        }).doesNotThrowAnyException();
    }
}