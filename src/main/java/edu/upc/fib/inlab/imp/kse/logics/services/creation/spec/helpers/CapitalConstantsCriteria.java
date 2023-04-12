package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

public class CapitalConstantsCriteria implements TermTypeCriteria {

    @Override
    public boolean isVariable(String rangeTermName) {
        return !isConstant(rangeTermName);
    }

    @Override
    public boolean isConstant(String rangeTermName) {
        if (rangeTermName.isEmpty()) return false;
        return Character.isUpperCase(rangeTermName.charAt(0));
    }
}
