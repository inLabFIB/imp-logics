package edu.upc.imp.logics.schema;

/**
 * Implementation of a logic variable. E.g.: x, y, z, ...
 */
public class Variable extends Term {
    public Variable(String name) {
        super(name);
    }

    @Override
    public String toString() {
        return "Var{'" + this.getName() + "'}";
    }
}
