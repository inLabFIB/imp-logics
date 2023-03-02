package edu.upc.imp.logics.schema;

/**
 * Implementation of a logic constant. E.g.: `Socrates`, 4, etc.
 */
public class Constant extends Term {
    public Constant(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Const{'" + this.getName() + "'}";
    }

}
