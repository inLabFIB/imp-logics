package edu.upc.imp.logics.schema;

import edu.upc.imp.logics.schema.operations.Substitution;

/**
 * Implementation of a logic constant. E.g.: `Socrates`, 4, etc.
 */
public class Constant extends Term {
    public Constant(String name) {
        super(name);
    }

    @Override
    public Term applySubstitution(Substitution substitution) {
        return this;
    }

    @Override
    public String toString() {
        return "Const{'" + this.getName() + "'}";
    }

}
