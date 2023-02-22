package edu.upc.imp.logics.schema.visitor;

public interface Visitable {

    <T, R> T accept(Visitor<T, R> visitor, R context);
}
