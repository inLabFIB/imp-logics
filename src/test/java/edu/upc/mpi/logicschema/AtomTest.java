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
public class AtomTest {
    private Atom johnSnowAtom;  //Knows("John Snow", x)
    private Atom samAtom;       //Kills("Sam", 1, x)
    private Atom lannisterAtom; //Loves("Lannister", x)
    private Atom heroAtom;      //Hero("John Snow")
    
    private Predicate knows;    //base
    private Predicate kills;    //base
    private Predicate loves;    //base
    private Predicate hero;     //Hero(x) :- Kills(x, y, z), y > 0
                                //Hero(x) :- Knows(x, y)
    
    //TODO: Still not finished!!
    
    public AtomTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        knows = new PredicateImpl("Knows",2);
        List<Term> terms = new LinkedList();
        terms.add(new Term("\"John Snow\""));
        terms.add(new Term("x"));
        johnSnowAtom = new Atom(knows, terms);
        
        kills = new PredicateImpl("Kills",3);
        terms = new LinkedList();
        terms.add(new Term("\"Sam\""));
        terms.add(new Term("1"));
        terms.add(new Term("x"));
        samAtom = new Atom(kills, terms);
        
        loves = new PredicateImpl("Loves",2);
        terms = new LinkedList();
        terms.add(new Term("\"Lannister\""));
        terms.add(new Term("\"Lannister\""));
        lannisterAtom = new Atom(loves, terms);
        
        hero = new PredicateImpl("Hero",1);
        terms = new LinkedList();
        terms.add(new Term("x"));
        Atom headAtom = new Atom(hero, terms);
        LinkedList<Literal> literals = new LinkedList();
        LinkedList<Term> killTerms = new LinkedList();
        killTerms.add(new Term("x"));
        killTerms.add(new Term("y"));
        killTerms.add(new Term("z"));
        literals.add(new OrdinaryLiteral(new Atom(kills, killTerms)));
        literals.add(new BuiltInLiteral(new Term("y"),new Term(0),">"));
        DerivationRule derivationRuleForKills = new DerivationRule(headAtom, literals);
        
        literals = new LinkedList();
        LinkedList<Term> knowsTerms = new LinkedList();
        knowsTerms.add(new Term("x"));
        knowsTerms.add(new Term("y"));
        literals.add(new OrdinaryLiteral(new Atom(knows, knowsTerms)));
        DerivationRule derivationRuleForKnows = new DerivationRule(new Atom(headAtom), literals);
        
        terms = new LinkedList();
        terms.add(new Term("\"John Snow\""));
        heroAtom = new Atom(hero, terms);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTerms method, of class Atom.
     */
    @Test
    public void testGetTerms() {
        System.out.println("getTerms");

        List<Term> expResult = new LinkedList();
        expResult.add(new Term("\"John Snow\""));
        expResult.add(new Term("x"));
        List<Term> result = johnSnowAtom.getTerms();
        assertEquals(expResult, result);
        
        //Checking that modifying the returned terms list actually modifies the atom
        johnSnowAtom.getTerms().get(1).setName("\"Nothing\"");
        
        expResult = new LinkedList();
        expResult.add(new Term("\"John Snow\""));
        expResult.add(new Term("\"Nothing\""));
        result = johnSnowAtom.getTerms();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTermsCopied method, of class Atom.
     */
    @Test
    public void testGetTermsCopied() {
        System.out.println("getTermsCopied");
        
        List<Term> expResult = new LinkedList();
        expResult.add(new Term("\"John Snow\""));
        expResult.add(new Term("x"));
        List<Term> result = johnSnowAtom.getTerms();
        assertEquals(expResult, result);
        
        //Checking that modifying the returned terms list does not modify the atom
        johnSnowAtom.getTermsCopied().get(1).setName("\"Nothing\"");
        
        expResult = new LinkedList();
        expResult.add(new Term("\"John Snow\""));
        expResult.add(new Term("x"));
        result = johnSnowAtom.getTerms();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTermsNamesList method, of class Atom.
     */
    @Test
    public void testGetTermsNamesList() {
        System.out.println("getTermsNamesList");
        
        List<String> expResult = new LinkedList();
        expResult.add("\"John Snow\"");
        expResult.add("x");
        List<String> result = johnSnowAtom.getTermsNamesList();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTerm method, of class Atom.
     */
    @Test
    public void testGetTerm() {
        System.out.println("getTerm");
        
        Term expResult = new Term("\"John Snow\"");
        Term result = johnSnowAtom.getTerm(0);
        assertEquals(expResult, result);
        
        expResult = new Term("x");
        result = johnSnowAtom.getTerm(1);
        assertEquals(expResult, result);
    }

    /**
     * Test of getVariablesNames method, of class Atom.
     */
    @Test
    public void testGetVariablesNames() {
        System.out.println("getVariablesNames");
        
        Set<String> result = samAtom.getVariablesNames();
        Set<String> expResult = new HashSet();
        expResult.add("x");
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getIndexesOfTerm method, of class Atom.
     */
    @Test
    public void testGetIndexesOfTerm() {
        System.out.println("getIndexesOfTerm");
        
        Term term = new Term("\"Lannister\"");
        List<Integer> expResult = new LinkedList();
        expResult.add(0);
        expResult.add(1);
        List<Integer> result = lannisterAtom.getIndexesOfTerm(term);
        assertEquals(expResult, result);
        
        term = new Term("\"Sam\"");
        expResult = new LinkedList();
        expResult.add(0);
        result = samAtom.getIndexesOfTerm(term);
        assertEquals(expResult, result);
        
        term = new Term("A term not appearing anywhere");
        expResult = new LinkedList();
        result = samAtom.getIndexesOfTerm(term);
        assertEquals(expResult, result);
    }

    /**
     * Test of isBase method, of class Atom.
     */
    @Test
    public void testIsBase() {
        System.out.println("isBase");

        boolean expResult = true;
        boolean result = samAtom.isBase();
        assertEquals(expResult, result);
        
        assertEquals(heroAtom.isBase(), false);
        
        
    }

    /**
     * Test of getPredicateName method, of class Atom.
     */
    @Test
    public void testGetPredicateName() {
        System.out.println("getPredicateName");
        
        Atom instance = samAtom;
        String expResult = "Kills";
        String result = instance.getPredicateName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPredicateArity method, of class Atom.
     */
    @Test
    public void testGetPredicateArity() {
        System.out.println("getPredicateArity");
        
        Atom instance = samAtom;
        int expResult = 3;
        int result = instance.getPredicateArity();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDefinitionRules method, of class Atom.
     */
    @Test
    public void testGetDefinitionRulesWhenCalled() {
        System.out.println("getDefinitionRulesWhenCalled");
        
        Atom instance = samAtom;
        List<List<Literal>> expResult = new LinkedList();
        List<List<Literal>> result = instance.getDefinitionRulesWhenCalled(new HashSet());
        assertEquals(expResult, result);
        
        //Checking for derivation Rules
        instance = heroAtom;
        expResult = new LinkedList();
        
        LinkedList<Literal> literals = new LinkedList();
        LinkedList<Term> killTerms = new LinkedList();
        killTerms.add(new Term("\"John Snow\""));
        killTerms.add(new Term("y"));
        killTerms.add(new Term("z"));
        literals.add(new OrdinaryLiteral(new Atom(kills, killTerms)));
        literals.add(new BuiltInLiteral(new Term("y"),new Term(0),">"));
        expResult.add(literals);
        
        literals = new LinkedList();
        LinkedList<Term> knowsTerms = new LinkedList();
        knowsTerms.add(new Term("\"John Snow\""));
        knowsTerms.add(new Term("y"));
        literals.add(new OrdinaryLiteral(new Atom(knows, knowsTerms)));
        expResult.add(literals);
        
        result = instance.getDefinitionRulesWhenCalled(new HashSet());
        assertEquals(expResult, result);
    }

//    /**
//     * Test of getSubstitutedAtom method, of class Atom.
//     */
//    @Test
//    public void testGetSubstitutedAtom() {
//        System.out.println("getSubstitutedAtom");
//        Map<String, Term> substitution = null;
//        Atom instance = null;
//        Atom expResult = null;
//   //     Atom result = instance.getSubstitutedAtom(substitution);
//   //     assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getUnification method, of class Atom.
//     */
//    @Test
//    public void testGetUnification() {
//        System.out.println("getUnification");
//        Atom target = null;
//        Atom instance = null;
//        Map<String, String> expResult = null;
//        Map<String, String> result = instance.getUnification(target);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getVariableToVariableUnification method, of class Atom.
//     */
//    @Test
//    public void testGetVariableToVariableUnification() {
//        System.out.println("getVariableToVariableUnification");
//        Atom target = null;
//        Map<String, String> currentSubstitution = null;
//        Atom instance = null;
//        Map<String, String> expResult = null;
//        Map<String, String> result = instance.getVariableToVariableUnification(target, currentSubstitution);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getAtomWithVariableSuffix method, of class Atom.
//     */
//    @Test
//    public void testGetAtomWithVariableSuffix() {
//        System.out.println("getAtomWithVariableSuffix");
//        String suffix = "";
//        Atom instance = null;
//        Atom expResult = null;
//        Atom result = instance.getAtomWithVariableSuffix(suffix);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getNumberOfDefinitionRules method, of class Atom.
//     */
//    @Test
//    public void testGetNumberOfDefinitionRules() {
//        System.out.println("getNumberOfDefinitionRules");
//        Atom instance = null;
//        int expResult = 0;
//        int result = instance.getNumberOfDefinitionRules();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
