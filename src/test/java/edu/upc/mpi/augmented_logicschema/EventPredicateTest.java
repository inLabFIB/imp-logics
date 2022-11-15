package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.augmented_logicschema.EventPredicate.EventType;
import edu.upc.mpi.logicschema.Predicate;
import edu.upc.mpi.logicschema.PredicateImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 */
public class EventPredicateTest {
    
    public EventPredicateTest() {
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
        assertThat(result).isEqualTo(expResult);
        
        instance = new EventPredicate(p,EventType.DELETE);
        expResult = "del_P";
        result = instance.getName();
        assertThat(result).isEqualTo(expResult);
    }
}
