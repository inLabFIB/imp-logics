package edu.upc.imp.logicschema;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class TermTest {
    
    @Test
    public void should_CreateConstantTerm_When_CreateTermWithInteger(){
        Term term = new Term(1);
        assertThat(term.isConstant()).isTrue();
        assertThat(term.getName()).isEqualTo("1");
    }

    @Test
    public void should_CreateNewTermWithSameName_When_CopyingTerm(){
        Term original = new Term("x");
        Term copy = original.copy();
        assertThat(copy.getName()).isEqualTo(original.getName());
        assertThat(copy).isNotSameAs(original);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "1.0", "1.00", "\"x\"", "\"1\""})
    public void should_AddSufixToName_When_InvokingSetSuffix(String nameOfConstant){
        Term term = new Term(nameOfConstant);
        assertThat(term.isConstant()).isTrue();
    }

    private static Stream<Arguments> provideConstantNames() {
        return Stream.of(
                Arguments.of("1"),
                Arguments.of("1.0"),
                Arguments.of("1.00"),
                Arguments.of("\"x\""),
                Arguments.of("\"1\"")
        );
    }

    private static Stream<Arguments> provideVariableNames() {
        return Stream.of(
                Arguments.of("x"),
                Arguments.of("1x"),
                Arguments.of("x1"),
                Arguments.of("x'")
        );
    }

    @ParameterizedTest
    @MethodSource("provideConstantNames")
    public void should_IdentifyConstant_When_TermIsConstant(String nameOfConstant){
        Term term = new Term(nameOfConstant);
        assertThat(term.isConstant()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideVariableNames")
    public void should_NotIdentifyConstant_When_TermIsVariable(String nameOfVariable){
        Term term = new Term(nameOfVariable);
        assertThat(term.isConstant()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideVariableNames")
    public void should_IdentifyVariable_When_TermIsVariable(String nameOfVariable){
        Term term = new Term(nameOfVariable);
        assertThat(term.isVariable()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideConstantNames")
    public void should_NotIdentifyVariable_When_TermIsConstant(String nameOfConstant){
        Term term = new Term(nameOfConstant);
        assertThat(term.isVariable()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "1"})
    public void should_ReturnVariableWithSameName_When_SubstitutionDoesNotApplyToVariable(String termName){
        Term term = new Term(termName);
        Map<String, String> substitution = Map.of("x1", "y1");
        Term substitutedTerm = term.getSubstitutedTerm(substitution);
        assertThat(substitutedTerm.getName()).isEqualTo(termName);
    }

    @Test
    public void should_ReturnNewVariableWithCorrespondingName_When_SubstitutionApplies(){
        Term term = new Term("a");
        Map<String, String> substitution = Map.of("x", "y", "a", "b");
        Term substitutedTerm = term.getSubstitutedTerm(substitution);
        assertThat(substitutedTerm.getName()).isEqualTo("b");
    }



    @Test
    public void should_ReturnSubstitution_When_UnifyingTwoVariables_WithEmptySubstitution(){
        Term thisVariable = new Term("a");
        Term thatVariable = new Term("b");
        Map<String, String> substitution = Map.of();

        Map<String, String> result = thisVariable.getVariableToVariableUnification(thatVariable, substitution);

        Map<String, String> expResult = Map.of("a", "b");
        assertThat(result).isEqualTo(expResult);
    }

    @Test
    public void should_ReturnSameSubstitution_When_UnifyingTwoVariables_WithAlreadyUnifyingSubstitution(){
        Term thisVariable = new Term("a");
        Term thatVariable = new Term("b");
        Map<String, String> substitution = Map.of("a","b");

        Map<String, String> result = thisVariable.getVariableToVariableUnification(thatVariable, substitution);

        Map<String, String> expResult = Map.of("a", "b");
        assertThat(result).isEqualTo(expResult);
    }

    @Test
    public void should_ReturnNull_When_UnifyingTwoVariables_WithWrongSubstitution(){
        Term thisVariable = new Term("a");
        Term thatVariable = new Term("b");
        Map<String, String> substitution = Map.of("a","c");

        Map<String, String> result = thisVariable.getVariableToVariableUnification(thatVariable, substitution);

        assertThat(result).isNull();
    }


    /**
     * Test of getVariableToVariableUnification method, of class Term.
     */
    @Test
    public void otherCases_getVariableToVariableUnification() {
        Term thatVariable, instance;
        Map<String, String> substitution, expResult, result;
        
        thatVariable = new Term(1);
        substitution = new HashMap<>();
        instance = new Term("a");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isNull();
        
        thatVariable = new Term(1);
        substitution = new HashMap<>();
        instance = new Term(1);
        expResult = new HashMap<>();
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isEqualTo(expResult);
        
        thatVariable = new Term(1);
        substitution = new HashMap<>();
        instance = new Term("a");
        substitution.put("a", "1");
        expResult = new HashMap<>();
        expResult.put("a","1");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isEqualTo(expResult);
    }


}
