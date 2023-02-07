package edu.upc.imp.logicschema;

import edu.upc.imp.utils.AtomMother;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class AtomTest {

    @Test
    public void should_ReturnEquivalentAtom_WhenApplyingEmptySubstitution(){
        Atom atomP = AtomMother.buildAtom("P", List.of("x", "y"));
        Map<String, String> substitution = new HashMap<>();
        Atom substitutedAtom = atomP.getSubstitutedAtom(substitution);
        assertThat(atomP).isEqualTo(substitutedAtom);
    }

    @Test
    public void should_ReturnEquivalentAtom_WhenApplyingSubstitution_ThatDoesChangeAnyVariable(){
        Atom atomP = AtomMother.buildAtom("P", List.of("x", "y"));
        Map<String, String> substitution = Map.of("a","b");
        Atom substitutedAtom = atomP.getSubstitutedAtom(substitution);
        assertThat(atomP).isEqualTo(substitutedAtom);
    }

    @Test
    public void should_ReturnAtom_WithChangedVariablse_WhenApplyingSubstitution_ThatChangesVariables(){
        Atom atomP = AtomMother.buildAtom("P", List.of("x", "y"));
        Map<String, String> substitution = Map.of("x","y", "y", "1");
        Atom substitutedAtom = atomP.getSubstitutedAtom(substitution);
        Atom expectedAtom = AtomMother.buildAtom("P", List.of("y", "1"));
        assertThat(substitutedAtom).isEqualTo(expectedAtom);
    }



}
