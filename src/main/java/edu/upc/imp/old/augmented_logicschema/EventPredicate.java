package edu.upc.imp.old.augmented_logicschema;

import edu.upc.imp.old.logicschema.LogicSchema;
import edu.upc.imp.old.logicschema.Predicate;


/**
 * Implementation of an augmented predicate. An augmented predicate is a predicate
 * representing the application of an insertion/deletion in the population of
 * some predicate.
 * E.g. given the predicate "P", "ins_P" is an augmented predicate representing
 * an insertion in the predicate P.
 */
public class EventPredicate extends Predicate {
    private final EventType eventType;
    private final Predicate containedPredicate;

    public enum EventType {INSERT, DELETE}

    public static String getPredicateNameFor(EventType eventType, Predicate predicate) {
        String prefix = "";
        switch (eventType) {
            case INSERT:
                prefix = "ins_";
                break;
            case DELETE:
                prefix = "del_";
                break;
            default:
                assert false : "Configure the prefix for the EventType " + eventType;
        }

        return prefix + predicate.getName();
    }


    /**
     * Create a new EventPredicate with the given eventType and predicate
     */
    public EventPredicate(Predicate predicate, EventType eventType) {
        this.eventType = eventType;
        this.containedPredicate = predicate;
    }


    @Override
    public String getName() {
        return getPredicateNameFor(this.eventType, this.containedPredicate);
    }

    @Override
    public int getArity() {
        return containedPredicate.getArity();
    }

    public Predicate getPredicate() {
        return this.containedPredicate;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean getIsInsertion() {
        return this.eventType.equals(EventType.INSERT);
    }


    @Override
    public void copyToLogicSchema(LogicSchema logicSchema) {
        this.containedPredicate.copyToLogicSchema(logicSchema);
        Predicate newContainedPredicate = logicSchema.getPredicate(this.containedPredicate.getName());
        logicSchema.addPredicate(new EventPredicate(newContainedPredicate, this.eventType));
    }

}
