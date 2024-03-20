package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class ConjunctiveQuerySpecTest {

    @Test
    void should_throwException_when_termSpecListIsNull() {
        List<TermSpec> termSpecList = null;
        BodySpec body = new BodySpec(List.of(new OrdinaryLiteralSpec("a", List.of(new ConstantSpec("a")), true)));
        assertThatThrownBy(() -> new ConjunctiveQuerySpec(termSpecList, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_throwException_when_bodyIsNull() {
        List<TermSpec> termSpecList = List.of(new ConstantSpec("a"));
        BodySpec body = null;
        assertThatThrownBy(() -> new ConjunctiveQuerySpec(termSpecList, body))
                .isInstanceOf(IllegalArgumentException.class);
    }

}