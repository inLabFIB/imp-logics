package edu.upc.imp.logics.services.creation.spec;

public class TermSpec implements LogicElementSpec {
    private final String name;

    public TermSpec(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
