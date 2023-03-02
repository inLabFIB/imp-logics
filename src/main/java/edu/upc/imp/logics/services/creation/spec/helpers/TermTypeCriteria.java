package edu.upc.imp.logics.services.creation.spec.helpers;

public interface TermTypeCriteria {
    boolean isVariable(String rangeTermName);

    boolean isConstant(String rangeTermName);
}
