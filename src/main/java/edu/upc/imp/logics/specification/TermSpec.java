package edu.upc.imp.logics.specification;

public class TermSpec implements LogicElementSpec {
    private final String name;

    public TermSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
