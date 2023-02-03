package edu.upc.imp.logicschema;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 *
 */
public class TermTest {
    
    public TermTest() {
    }
    

    /**
     * Test of getName method, of class Term.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        
        Term instance;
        String expResult, result;
        
        instance = new Term("\"John\"");
        expResult = "\"John\"";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
        
        instance = new Term("John");
        expResult = "John";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);

        instance = new Term("1");
        expResult = "1";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
        
        instance = new Term(1);
        expResult = "1";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
        
        instance = new Term(-1);
        expResult = "-1";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
        
        instance = new Term("-1.5");
        expResult = "-1.5";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of setName method, of class Term.
     */
    @Test
    public void testSetName() {
        Term instance = new Term("John");
        instance.setName("John Snow");
        String expResult = "John Snow";
        String result = instance.getName();
        assertThat(result).isEqualTo(expResult);

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> {
                    Term term = new Term("\"John Snow\"");
                    term.setName("John Snow");
                });
    }

    /**
     * Test of isConstant method, of class Term.
     */
    @Test
    public void testIsConstant() {

        Term instance;
        boolean result;
        
        instance = new Term("\"John\"");
        result = instance.isConstant();
        assertThat(result).isTrue();
        
        instance = new Term("John");
        result = instance.isConstant();
        assertThat(result).isFalse();

        instance = new Term("1");
        result = instance.isConstant();
        assertThat(result).isTrue();
        
        instance = new Term(1);
        result = instance.isConstant();
        assertThat(result).isTrue();
        
        instance = new Term(-1);
        result = instance.isConstant();
        assertThat(result).isTrue();
        
        instance = new Term("-1.5");
        result = instance.isConstant();
        assertThat(result).isTrue();
    }

    /**
     * Test of isVariable method, of class Term.
     */
    @Test
    public void testIsVariable() {
        System.out.println("isVariable");
        
        Term instance;
        boolean result;
        
        instance = new Term("\"John\"");
        result = instance.isVariable();
        assertThat(result).isFalse();
        
        instance = new Term("John");
        result = instance.isVariable();
        assertThat(result).isTrue();

        instance = new Term("1");
        result = instance.isVariable();
        assertThat(result).isFalse();
        
        instance = new Term(1);
        result = instance.isVariable();
        assertThat(result).isFalse();
        
        instance = new Term(-1);
        result = instance.isVariable();
        assertThat(result).isFalse();
        
        instance = new Term("-1.5");
        result = instance.isVariable();
        assertThat(result).isFalse();
    }
    
    /**
     * Test of setSuffix method, of class Term.
     */
    @Test
    public void testSetSuffix() {

        String suffix = "_0";
        Term instance = new Term("x");
        instance.setSuffix(suffix);
        String expResult = "x_0";
        String result = instance.getName();
        assertThat(result).isEqualTo(expResult);

        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> {
                    Term term = new Term("1");
                    term.setSuffix("_0");
                });
    }

    /**
     * Test of getSubstitutedTerm method, of class Term.
     */
    @Test
    public void testGetSubstitutedTerm() {

        Term instance;
        Term expResult, result;
        Map<String, String> substitution;
        
        substitution = new HashMap<>();
        substitution.put("a", "b");
        instance = new Term("a");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("b");
        assertThat(result).isEqualTo(expResult);

        substitution = new HashMap<>();
        substitution.put("a", "b");
        instance = new Term("b");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("b");
        assertThat(result).isEqualTo(expResult);
        
        substitution = new HashMap<>();
        substitution.put("0", "1");
        instance = new Term("0");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("0");
        assertThat(result).isEqualTo(expResult);
        
        substitution = new HashMap<>();
        substitution.put("a", "b");
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("c");
        assertThat(result).isEqualTo(expResult);
        
        substitution = new HashMap<>();
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("c");
        assertThat(result).isEqualTo(expResult);
        
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(null);
        result = new Term("c");
        assertThat(result).isEqualTo(expResult);
    }

    /**
     * Test of getVariableToVariableUnification method, of class Term.
     */
    @Test
    public void testGetVariableToVariableUnification() {

        Term thatVariable, instance;
        Map<String, String> substitution, expResult, result;
        
        thatVariable = new Term("b");
        substitution = new HashMap<>();
        instance = new Term("a");
        expResult = new HashMap<>();
        expResult.put("a","b");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isEqualTo(expResult);

        thatVariable = new Term("b");
        substitution = new HashMap<>();
        substitution.put("a","b");
        instance = new Term("a");
        expResult = new HashMap<>();
        expResult.put("a","b");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isEqualTo(expResult);
        
        thatVariable = new Term("b");
        substitution = new HashMap<>();
        substitution.put("a","c");
        instance = new Term("a");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertThat(result).isNull();
        
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
