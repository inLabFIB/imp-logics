package edu.upc.imp.logics.schema;

/**
 * Implementation of a logic Term. A Term is either a Variable or a Constant.
 * E.g.: variable "x", constant "1"
 * The distinction between Variables and Users is done through the subclass
 * This class is a ValueObject. Hence, the same Term can be safely reused
 * among several Atoms, or Built-in literals.
 */
public abstract class Term {
    private final String name;

    public Term(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
