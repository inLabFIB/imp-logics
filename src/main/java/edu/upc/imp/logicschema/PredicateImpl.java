
package edu.upc.imp.logicschema;

/**
 * Simple implementation of a logic predicate abstract class
 */
public class PredicateImpl extends Predicate {
    private final String name;
    private final int arity;

    public PredicateImpl(String name, int arity) {
        assert arity >= 0 : "Arity cannot be negative";
        this.name = name;
        this.arity = arity;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public void copyToLogicSchema(LogicSchema logicSchema) {
        logicSchema.addPredicate(new PredicateImpl(this.name, this.arity));
    }
}
