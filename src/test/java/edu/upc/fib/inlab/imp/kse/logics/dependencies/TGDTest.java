package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TGDTest {

    @Nested
    class CreationTests {

        private final List<Literal> defaultBody = List.of(LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")));

        @Test
        void should_throwException_whenCreatingATGD_withNullHead() {
            assertThatThrownBy(() -> new TGD(defaultBody, null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenCreatingATGD_withEmptyHead() {
            List<Atom> head = List.of();
            assertThatThrownBy(() -> new TGD(defaultBody, head)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_makeHeadImmutable_whenCreatingATGD_withMutableListInput() {
            Atom atom = AtomMother.createAtom("P", "x");
            List<Atom> head = createMutableAtomList(atom);
            TGD dependency = new TGD(defaultBody, head);

            assertThat(dependency.getHead()).isUnmodifiable();
        }

        private static List<Atom> createMutableAtomList(Atom... atoms) {
            return new LinkedList<>(List.of(atoms));
        }
    }

}