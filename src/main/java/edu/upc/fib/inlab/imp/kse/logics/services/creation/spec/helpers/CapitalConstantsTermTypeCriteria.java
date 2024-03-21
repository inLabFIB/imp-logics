package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

public class CapitalConstantsTermTypeCriteria extends TermTypeCriteria {

    @Override
    public boolean isConstantHook(String rangeTermName) {
        if (rangeTermName.isEmpty()) return false;
        return Character.isUpperCase(rangeTermName.charAt(0));
    }
}
