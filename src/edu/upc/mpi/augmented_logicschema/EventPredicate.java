/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.logicschema.Predicate;


/**
 * Implementation of an augmented predicate. An augmented predicate is a predicate
 * representing the application of an insertion/deletion in the population of
 * some predicate.
 * E.g. given the predicate "P", "ins_P" is an augmented predicate representing
 * an insertion in the predicate P.
 * 
 * @author Xavier Oriol Hilari
 */
public class EventPredicate extends Predicate {
    private final EventType eventType;
    private final Predicate containedPredicate;

    public enum EventType{INSERT, DELETE};
    
    /**
     * Craete a new EventPredicate with the given eventType and and predicate
     *
     * @param predicate
     * @param eventType
     */
    public EventPredicate(Predicate predicate, EventType eventType){
        this.eventType = eventType;
        this.containedPredicate = predicate;
    }
    
    public static String getPredicateNameFor(EventType eventType, Predicate predicate) {
        String prefix = "";
        switch(eventType){
            case INSERT: prefix = "ins_"; break;
            case DELETE: prefix = "del_"; break;
            default:
                assert false :"Configure the prefix for the EventType " + eventType;
        }

        return prefix + predicate.getName();
    }
    
    public Predicate getPredicate(){
        return this.containedPredicate;
    }

    @Override
    public String getName(){
        return getPredicateNameFor(this.eventType, this.containedPredicate);
    }
    
    @Override
    public int getArity() {
        return containedPredicate.getArity();
    }

    public EventType getEventType(){
        return eventType;
    }
    
    public boolean getIsInsertion() {
        return this.eventType.equals(EventType.INSERT);
    }
    

}
