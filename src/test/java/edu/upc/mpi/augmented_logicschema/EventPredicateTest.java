/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.augmented_logicschema.EventPredicate;
import edu.upc.mpi.augmented_logicschema.EventPredicate.EventType;
import edu.upc.mpi.logicschema.Predicate;
import edu.upc.mpi.logicschema.PredicateImpl;
import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Xavier
 */
public class EventPredicateTest {
    
    public EventPredicateTest() {
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
     * Test of getName method, of class EventPredicate.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        
        Predicate p  = new PredicateImpl("P",1);
        
        EventPredicate instance = new EventPredicate(p,EventType.INSERT);
        String expResult = "ins_P";
        String result = instance.getName();
        assertEquals(expResult, result);
        
        instance = new EventPredicate(p,EventType.DELETE);
        expResult = "del_P";
        result = instance.getName();
        assertEquals(expResult, result);
    }
}
