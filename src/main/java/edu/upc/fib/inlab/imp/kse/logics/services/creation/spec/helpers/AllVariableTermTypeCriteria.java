package edu.upc.fib.inlab.imp.kse.logics.services.creation.spec.helpers;

/**
 * AllVariableTermTypeCriteria that interprets any term name as variable
 */
public class AllVariableTermTypeCriteria extends TermTypeCriteria {

    @Override
    protected boolean isConstantHook(String rangeTermName) {
        return false;
    }
}
