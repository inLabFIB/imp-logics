package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

public abstract class TermTypeCriteria {

    private final BasicConstantTypeCriteria basicConstantTypeCriteria = new BasicConstantTypeCriteria();

    public boolean isVariable(String rangeTermName) {
        return !isConstant(rangeTermName);
    }

    public boolean isConstant(String rangeTermName) {
        if (basicConstantTypeCriteria.isBasicConstant(rangeTermName)) return true;
        else return isConstantHook(rangeTermName);
    }

    protected abstract boolean isConstantHook(String rangeTermName);

}
