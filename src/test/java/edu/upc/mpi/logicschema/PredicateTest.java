/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.mpi.logicschema;

import edu.upc.mpi.logicschema.*;
import org.junit.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Xavier
 */
public class PredicateTest {
    
    public PredicateTest() {
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
     * Test of getName method, of class Predicate.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        
        Predicate instance = new PredicateImpl("P",2);
        String expResult = "P";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getArity method, of class Predicate.
     */
    @Test
    public void testGetArity() {
        System.out.println("getArity");
        
        Predicate instance = new PredicateImpl("P",2);
        int expResult = 2;
        int result = instance.getArity();
        assertEquals(expResult, result);
    }

    /**
     * Test of isBase method, of class Predicate.
     */
    @Test
    public void testIsBase() {
        System.out.println("isBase");
        
        Predicate instance = new PredicateImpl("P",2);
        boolean expResult = true;
        boolean result = instance.isBase();
        assertEquals(expResult, result);

        List<Term> headTerms = new LinkedList();
        headTerms.add(new Term("x"));
        headTerms.add(new Term("y"));
        Atom head = new Atom(instance, headTerms);
        
        Predicate q = new PredicateImpl("Q",2);
        List<Term> qTerms = new LinkedList();
        qTerms.add(new Term("x"));
        qTerms.add(new Term("y"));
        Atom qAtom = new Atom(q, qTerms);
        List<Literal> literals = new LinkedList();
        literals.add(new OrdinaryLiteral(qAtom));
        DerivationRule derivationRule = new DerivationRule(head, literals);
        
        expResult = false;
        result = instance.isBase();
        assertEquals(expResult, result);
    }

    /**
     * Test of addDerivationRule method, of class Predicate.
     */
    @Test
    public void testAddDerivationRule() {
        //This is tested with test get definition rules
    }

    /**
     * Test of getDefinitionRules method, of class Predicate.
     */
    @Test
    public void testGetDefinitionRules() {
        System.out.println("getDefinitionRules");
        
        Predicate instance = new PredicateImpl("P",2);
        List<DerivationRule> expResult = new LinkedList();
        List<DerivationRule> result = instance.getDefinitionRules();
        assertEquals(expResult, result);

        List<Term> headTerms = new LinkedList();
        headTerms.add(new Term("x"));
        headTerms.add(new Term("y"));
        Atom head = new Atom(instance, headTerms);
        
        Predicate q = new PredicateImpl("Q",2);
        List<Term> qTerms = new LinkedList();
        qTerms.add(new Term("x"));
        qTerms.add(new Term("y"));
        Atom qAtom = new Atom(q, qTerms);
        List<Literal> literals = new LinkedList();
        literals.add(new OrdinaryLiteral(qAtom));
        DerivationRule derivationRule = new DerivationRule(head, literals);
        
        expResult = new LinkedList();
        expResult.add(derivationRule);
        result = instance.getDefinitionRules();
        assertEquals(expResult, result);
        
        Predicate r = new PredicateImpl("R",2);
        List<Term> rTerms = new LinkedList();
        rTerms.add(new Term("x"));
        rTerms.add(new Term("y"));
        Atom rAtom = new Atom(r, rTerms);
        literals = new LinkedList();
        literals.add(new OrdinaryLiteral(rAtom));
        derivationRule = new DerivationRule(head, literals);
        
        expResult.add(derivationRule);
        result = instance.getDefinitionRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNumberOfDefinitionRules method, of class Predicate.
     */
    @Test
    public void testGetNumberOfDefinitionRules() {
        System.out.println("getNumberOfDefinitionRules");

        Predicate instance = new PredicateImpl("P",2);
        int expResult = 0;
        int result = instance.getNumberOfDefinitionRules();
        assertEquals(expResult, result);

        List<Term> headTerms = new LinkedList();
        headTerms.add(new Term("x"));
        headTerms.add(new Term("y"));
        Atom head = new Atom(instance, headTerms);
        
        Predicate q = new PredicateImpl("Q",2);
        List<Term> qTerms = new LinkedList();
        qTerms.add(new Term("x"));
        qTerms.add(new Term("y"));
        Atom qAtom = new Atom(q, qTerms);
        List<Literal> literals = new LinkedList();
        literals.add(new OrdinaryLiteral(qAtom));
        DerivationRule derivationRule = new DerivationRule(head, literals);
        
        expResult = 1;
        result = instance.getNumberOfDefinitionRules();
        assertEquals(expResult, result);
        
        Predicate r = new PredicateImpl("R",2);
        List<Term> rTerms = new LinkedList();
        rTerms.add(new Term("x"));
        rTerms.add(new Term("y"));
        Atom rAtom = new Atom(r, rTerms);
        literals = new LinkedList();
        literals.add(new OrdinaryLiteral(rAtom));
        derivationRule = new DerivationRule(head, literals);
        
        expResult = 2;
        result = instance.getNumberOfDefinitionRules();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllPredicatesClosureInDefinitionRules method, of class Predicate.
     */
    @Test
    public void testGetAllPredicatesClosureInDefinitionRules() {
        System.out.println("getAllPredicatesClosureInDefinitionRules");
        
        Predicate instance = new PredicateImpl("P",2);
        Set<Predicate> expResult = new HashSet();
        Set<Predicate> result = instance.getAllPredicatesClosureInDefinitionRules();
        assertEquals(expResult, result);

        List<Term> headTerms = new LinkedList();
        headTerms.add(new Term("x"));
        headTerms.add(new Term("y"));
        Atom head = new Atom(instance, headTerms);
        
        Predicate q = new PredicateImpl("Q",2);
        List<Term> qTerms = new LinkedList();
        qTerms.add(new Term("x"));
        qTerms.add(new Term("y"));
        Atom qAtom = new Atom(q, qTerms);
        List<Literal> literals = new LinkedList();
        literals.add(new OrdinaryLiteral(qAtom));
        DerivationRule derivationRule = new DerivationRule(head, literals);
        
        expResult = new HashSet();
        expResult.add(q);
        result = instance.getAllPredicatesClosureInDefinitionRules();
        assertEquals(expResult, result);
        
        Predicate r = new PredicateImpl("R",2);
        List<Term> rTerms = new LinkedList();
        rTerms.add(new Term("x"));
        rTerms.add(new Term("y"));
        Atom rAtom = new Atom(r, rTerms);
        literals = new LinkedList();
        literals.add(new OrdinaryLiteral(rAtom));
        derivationRule = new DerivationRule(head, literals);
        
        expResult.add(r);
        result = instance.getAllPredicatesClosureInDefinitionRules();
        assertEquals(expResult, result);
        
        Predicate s = new PredicateImpl("S",2);
        List<Term> sTerms = new LinkedList();
        sTerms.add(new Term("x"));
        sTerms.add(new Term("y"));
        Atom sAtom = new Atom(s, sTerms);
        literals = new LinkedList();
        literals.add(new OrdinaryLiteral(sAtom));
        head = new Atom(rAtom);
        derivationRule = new DerivationRule(head, literals);
        
        expResult.add(s);
        result = instance.getAllPredicatesClosureInDefinitionRules();
        assertEquals(expResult, result);
    }
}
