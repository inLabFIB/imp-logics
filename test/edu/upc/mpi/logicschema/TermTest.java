/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.mpi.logicschema;

import edu.upc.mpi.logicschema.Term;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Xavier
 */
public class TermTest {
    
    public TermTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
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
        assertEquals(expResult, result);
        
        instance = new Term("John");
        expResult = "John";
        result = instance.getName();
        assertEquals(expResult, result);

        instance = new Term("1");
        expResult = "1";
        result = instance.getName();
        assertEquals(expResult, result);
        
        instance = new Term(1);
        expResult = "1";
        result = instance.getName();
        assertEquals(expResult, result);
        
        instance = new Term(-1);
        expResult = "-1";
        result = instance.getName();
        assertEquals(expResult, result);
        
        instance = new Term("-1.5");
        expResult = "-1.5";
        result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setName method, of class Term.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        Term instance;
        String expResult, result;
        
        instance = new Term("John");
        try {
            instance.setName("John Snow");
            expResult = "John Snow";
            result = instance.getName();
            assertEquals(expResult, result);
        } catch (AssertionError ex) {
            fail(instance+ "should have been correctly renamed");
        }
        
        instance = new Term("\"John Snow\"");
        try {
            instance.setName("John Snow");
            fail(instance+ "should have not been renamed");
        } catch (AssertionError ex) {
            //Ok
        }
    }

    /**
     * Test of isConstant method, of class Term.
     */
    @Test
    public void testIsConstant() {
        System.out.println("isConstant");

        Term instance;
        boolean expResult, result;
        
        instance = new Term("\"John\"");
        expResult = true;
        result = instance.isConstant();
        assertEquals(expResult, result);
        
        instance = new Term("John");
        expResult = false;
        result = instance.isConstant();
        assertEquals(expResult, result);

        instance = new Term("1");
        expResult = true;
        result = instance.isConstant();
        assertEquals(expResult, result);
        
        instance = new Term(1);
        expResult = true;
        result = instance.isConstant();
        assertEquals(expResult, result);
        
        instance = new Term(-1);
        expResult = true;
        result = instance.isConstant();
        assertEquals(expResult, result);
        
        instance = new Term("-1.5");
        expResult = true;
        result = instance.isConstant();
        assertEquals(expResult, result);
    }

    /**
     * Test of isVariable method, of class Term.
     */
    @Test
    public void testIsVariable() {
        System.out.println("isVariable");
        
        Term instance;
        boolean expResult, result;
        
        instance = new Term("\"John\"");
        expResult = false;
        result = instance.isVariable();
        assertEquals(expResult, result);
        
        instance = new Term("John");
        expResult = true;
        result = instance.isVariable();
        assertEquals(expResult, result);

        instance = new Term("1");
        expResult = false;
        result = instance.isVariable();
        assertEquals(expResult, result);
        
        instance = new Term(1);
        expResult = false;
        result = instance.isVariable();
        assertEquals(expResult, result);
        
        instance = new Term(-1);
        expResult = false;
        result = instance.isVariable();
        assertEquals(expResult, result);
        
        instance = new Term("-1.5");
        expResult = false;
        result = instance.isVariable();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of setSuffix method, of class Term.
     */
    @Test
    public void testSetSuffix() {
        System.out.println("setSuffix");
        
        String suffix = "_0";
        Term instance = new Term("x");
        try {
            instance.setSuffix(suffix);
            String expResult = "x_0";
            String result = instance.getName();
            assertEquals(expResult, result);
        } catch (AssertionError ex) {
            fail("Term should have been correctly renamed");
        }

        instance = new Term("1");
        try {
            instance.setSuffix(suffix);
            fail("Term should have not been renamed");
        } catch (AssertionError ex) {
            //Ok
        }
    }

    /**
     * Test of getSubstitutedTerm method, of class Term.
     */
    @Test
    public void testGetSubstitutedTerm() {
        System.out.println("getSubstitutedTerm");
        
        Term instance;
        Term expResult, result;
        Map<String, String> substitution;
        
        substitution = new HashMap();
        substitution.put("a", "b");
        instance = new Term("a");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("b");
        assertEquals(expResult, result);

        substitution = new HashMap();
        substitution.put("a", "b");
        instance = new Term("b");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("b");
        assertEquals(expResult, result);
        
        substitution = new HashMap();
        substitution.put("0", "1");
        instance = new Term("0");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("0");
        assertEquals(expResult, result);
        
        substitution = new HashMap();
        substitution.put("a", "b");
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("c");
        assertEquals(expResult, result);
        
        substitution = new HashMap();
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(substitution);
        result = new Term("c");
        assertEquals(expResult, result);
        
        instance = new Term("c");
        expResult = instance.getSubstitutedTerm(null);
        result = new Term("c");
        assertEquals(expResult, result);
    }

    /**
     * Test of getVariableToVariableUnification method, of class Term.
     */
    @Test
    public void testGetVariableToVariableUnification() {
        System.out.println("getVariableToVariableUnification");
        
        Term thatVariable, instance;
        Map<String, String> substitution, expResult, result;
        
        thatVariable = new Term("b");
        substitution = new HashMap();
        instance = new Term("a");
        expResult = new HashMap();
        expResult.put("a","b");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);

        thatVariable = new Term("b");
        substitution = new HashMap();
        substitution.put("a","b");
        instance = new Term("a");
        expResult = new HashMap();
        expResult.put("a","b");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);
        
        thatVariable = new Term("b");
        substitution = new HashMap();
        substitution.put("a","c");
        instance = new Term("a");
        expResult = null;
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);
        
        thatVariable = new Term(1);
        substitution = new HashMap();
        instance = new Term("a");
        expResult = null;
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);
        
        thatVariable = new Term(1);
        substitution = new HashMap();
        instance = new Term(1);
        expResult = new HashMap();
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);
        
        thatVariable = new Term(1);
        substitution = new HashMap();
        instance = new Term("a");
        substitution.put("a", "1");
        expResult = new HashMap();
        expResult.put("a","1");
        result = instance.getVariableToVariableUnification(thatVariable, substitution);
        assertEquals(expResult, result);
    }

    


}
