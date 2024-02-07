package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.AtomMother;
import edu.upc.fib.inlab.imp.kse.logics.schema.mothers.LiteralMother;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class TGDTest {

    private final List<Literal> defaultBody = List.of(LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")));

    @Nested
    class CreationTests {

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


    }

    @Nested
    class existentialAndUniversalVariablesTest {

        @Test
        void getUniversalVariablesFromDefaultBody() {
            Atom atom = AtomMother.createAtom("P");
            Set<Variable> universalVars = new TGD(defaultBody, createMutableAtomList(atom)).getUniversalVariables();
            assertThat(universalVars).isNotEmpty()
                    .contains(new Variable("x"));
        }

        @Test
        void getUniversalVariablesFromBodyWithSeveralLiterals() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteralWithVariableNames("P", List.of("x")),
                    LiteralMother.createOrdinaryLiteralWithVariableNames("Q", List.of("x")),
                    LiteralMother.createOrdinaryLiteralWithVariableNames("T", List.of("y"))
            );
            Atom atom = AtomMother.createAtom("P");
            Set<Variable> universalVars = new TGD(body, createMutableAtomList(atom)).getUniversalVariables();
            assertThat(universalVars).isNotEmpty()
                    .hasSize(2)
                    .contains(new Variable("x"))
                    .contains(new Variable("y"));
        }

        @Test
        void getUniversalVariablesFromBodyWithOnlyConstants() {
            List<Literal> body = List.of(
                    LiteralMother.createOrdinaryLiteral("P", List.of(new Constant("1"))),
                    LiteralMother.createOrdinaryLiteral("Q", List.of(new Constant("2"))),
                    LiteralMother.createOrdinaryLiteral("T", List.of(new Constant("3")))
            );
            Atom atom = AtomMother.createAtom("P");
            Set<Variable> universalVars = new TGD(body, createMutableAtomList(atom)).getUniversalVariables();
            assertThat(universalVars).isEmpty();
        }

        @Test
        void getExistentialVariablesFromTrivialHead() {
            Atom atom = AtomMother.createAtom("P");
            Set<Variable> existentialVars = new TGD(defaultBody, createMutableAtomList(atom)).getExistentialVariables();
            assertThat(existentialVars).isEmpty();
        }

        @Test
        void getExistentialVariablesFromHeadWihtExistentialAndUniversalVariables() {
            Atom atom = AtomMother.createAtom("P", "x" , "y");
            Set<Variable> existentialVars = new TGD(defaultBody, createMutableAtomList(atom)).getExistentialVariables();
            assertThat(existentialVars).isNotEmpty()
                    .contains(new Variable("y"))
                    .doesNotContain(new Variable("x"));
        }

        @Test
        void getExistentialVariablesFromHeadWihtExistentialVariableAndConstant() {
            Atom atom = AtomMother.createAtom("P", "y" , "4");
            Set<Variable> existentialVars = new TGD(defaultBody, createMutableAtomList(atom)).getExistentialVariables();
            assertThat(existentialVars).isNotEmpty()
                    .contains(new Variable("y"))
                    .doesNotContain(new Variable("4"));
        }

    }

    private static List<Atom> createMutableAtomList(Atom... atoms) {
        return new LinkedList<>(List.of(atoms));
    }

}