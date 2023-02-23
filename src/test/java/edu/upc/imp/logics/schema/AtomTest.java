package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.exceptions.ArityMismatch;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AtomTest {

    @Test
    public void should_ThrowException_WhenCreatingAtomWithNullPredicate() {
        assertThatThrownBy(() -> new Atom(null, List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_ThrowException_WhenCreatingAtomWithNullListOfTerms() {
        assertThatThrownBy(() -> new Atom(new MutablePredicate("P", 0), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void should_MakeTermsListImmutable_WhenCreatingTheAtom() {
        List<Term> terms = new LinkedList<>();
        terms.add(new Variable("x"));
        Atom atom = new Atom(new MutablePredicate("P", 1), terms);
        assertThat(atom.getTerms()).isUnmodifiable();
    }

    private static Stream<Arguments> provideWrongAritiesAndLists() {
        return Stream.of(
                Arguments.of(0, List.of(new Constant("1"))),
                Arguments.of(1, List.of()),
                Arguments.of(2, List.of(new Constant("1"))),
                Arguments.of(1, List.of(new Constant("1"), new Constant("2")))
        );
    }

    @ParameterizedTest
    @MethodSource("provideWrongAritiesAndLists")
    public void should_ThrowException_WhenCreatingAtomWithWrongArity(int arity, List<Term> terms) {
        assertThatThrownBy(() -> new Atom(new MutablePredicate("P", arity), terms))
                .isInstanceOf(ArityMismatch.class);
    }
}
