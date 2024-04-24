package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.assertions.ImmutableAtomListAssert;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.ImmutableAtomListMother;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImmutableAtomListTest {

    @Test
    void should_ReturnPredicatePositions_ofGivenVariable() {
        ImmutableAtomList atomList = ImmutableAtomListMother.create(
                "P(x), Q(y,x), R(x,x)"
        );

        Set<PredicatePosition> occupiedPositions = atomList.getPredicatePositionsWithVar(new Variable("x"));

        assertThat(occupiedPositions).hasSize(4)
                .anyMatch(p -> p.getPredicateName().equals("P") && p.position() == 0)
                .anyMatch(p -> p.getPredicateName().equals("Q") && p.position() == 1)
                .anyMatch(p -> p.getPredicateName().equals("R") && p.position() == 0)
                .anyMatch(p -> p.getPredicateName().equals("R") && p.position() == 1);
    }

    @Nested
    class CreationTests {
        @Test
        void should_throwException_whenCreatingImmutableAtomList_withNullList() {
            assertThatThrownBy(() -> new ImmutableAtomList((List<Atom>) null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_throwException_whenTryingToCreateImmutableAtomList_withNullElement() {
            List<Atom> listWithNull = new LinkedList<>();
            listWithNull.add(null);
            assertThatThrownBy(() -> new ImmutableAtomList(listWithNull))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_createEmptyTermList_whenCreatingImmutableTermList_withEmptyList() {
            ImmutableAtomList actualAtomList = new ImmutableAtomList(List.of());
            ImmutableAtomListAssert.assertThat(actualAtomList).isEmpty();
        }

        @Test
        void should_containAllAtomsInSameOrder_whenCreatingAtomList() {
            Atom atom1 = AtomMother.createAtom("P", "x");
            Atom atom2 = AtomMother.createAtom("Q", "x");

            ImmutableAtomList actualAtomList = new ImmutableAtomList(atom1, atom2);
            ImmutableAtomListAssert.assertThat(actualAtomList)
                    .containsAtom(0, atom1)
                    .containsAtom(1, atom2);
        }
    }

    @Nested
    class EqualsTest {

        private static Stream<Arguments> equalAtoms() {
            return Stream.of(
                    Arguments.of("Atom list with one atom",
                                 List.of("P(x, y)")
                    ),
                    Arguments.of("Atom list with several atoms",
                                 List.of("P(x, y)", "Q(x, y)")
                    )
            );
        }

        private static Stream<Arguments> notEqualsAtoms() {
            return Stream.of(
                    Arguments.of("Atom list with different size",
                                 "P(x, y), Q(x, y)",
                                 "P(x, y)"
                    ),
                    Arguments.of("Atom list with different atoms",
                                 "P(x, y), Q(x, y)",
                                 "P(x, y), Q(x, z)"
                    ),
                    Arguments.of("Atom list with different order",
                                 "P(x, y), Q(x, y)",
                                 "Q(x, y), P(x, y)"
                    ),
                    Arguments.of("Atom list with different atom order",
                                 "P(x, y), Q(x, y)",
                                 "Q(x, y), P(x, z)"
                    )
            );
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("equalAtoms")
        void should_returnTrue_whenAtomsAreEquals(String description, List<String> atomStrings) {
            List<Atom> atoms = atomStrings.stream()
                    .map(atomString -> LiteralMother.createOrdinaryLiteral(atomString).getAtom())
                    .toList();
            ImmutableAtomList immutableAtomList1 = new ImmutableAtomList(atoms);
            ImmutableAtomList immutableAtomList2 = new ImmutableAtomList(atoms);

            assertThat(immutableAtomList1).isEqualTo(immutableAtomList2);
        }

        @ParameterizedTest(name = "{0}")
        @MethodSource("notEqualsAtoms")
        void should_returnFalse_whenAtomsAreNotEquals(String description, String atomsString1, String atomsString2) {
            ImmutableAtomList immutableAtomList1 = ImmutableAtomListMother.create(atomsString1);
            ImmutableAtomList immutableAtomList2 = ImmutableAtomListMother.create(atomsString2);

            assertThat(immutableAtomList1).isNotEqualTo(immutableAtomList2);
        }
    }
}