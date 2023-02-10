package edu.upc.imp.old.logicschema;

import java.util.*;

/**
 * Implementation of the logic BuiltInLiteral.
 */
public class BuiltInLiteral extends Literal {

    private final Term leftTerm;
    private final Term rightTerm;
    private final String operation;

    /**
     * Constructs a new BuiltInLiteral with the given terms and operation.
     *
     * @param operation an string like ">=",">", ...
     */
    public BuiltInLiteral(Term left, Term right, String operation) {
        this.leftTerm = left;
        this.rightTerm = right;
        this.operation = operation;
    }

    /**
     * Constructs a new BuiltInLiteral by copying the terms and operation
     * of the given builtInLiteral
     */
    public BuiltInLiteral(BuiltInLiteral builtInLiteral) {
        this.leftTerm = builtInLiteral.leftTerm.copy();
        this.rightTerm = builtInLiteral.rightTerm.copy();
        this.operation = builtInLiteral.operation;
    }

    /**
     * @return the actual left term
     */
    public Term getLeftTerm() {
        return leftTerm;
    }

    /**
     * @return the actual right term
     */
    public Term getRightTerm() {
        return rightTerm;
    }

    /**
     * @return the operation, which is a string like ">=","<>","=",...
     */
    public String getOperator() {
        return operation;
    }

    @Override
    public Set<String> getVariablesNames() {
        Set<String> result = new HashSet<>();
        if (this.leftTerm.isVariable()) {
            result.add(leftTerm.getName());
        }
        if (this.rightTerm.isVariable()) {
            result.add(rightTerm.getName());
        }
        return result;
    }

    @Override
    public Literal getLiteralAfterSubstitution(Map<String, String> substitution) {
        Term resultLeft = this.leftTerm.getSubstitutedTerm(substitution);
        Term resultRight = this.rightTerm.getSubstitutedTerm(substitution);
        return new BuiltInLiteral(resultLeft, resultRight, this.operation);
    }

    @Override
    public BuiltInLiteral copy() {
        return new BuiltInLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BuiltInLiteral) {
            BuiltInLiteral bil = (BuiltInLiteral) o;
            // Case a < b is equal to a < b
            if (this.getLeftTerm().equals(bil.getLeftTerm())
                    && this.getRightTerm().equals(bil.getRightTerm())
                    && this.getOperator().equals(bil.getOperator())) {
                return true;
            }
            //Case a < b is equal to b > a
            else if (this.getRightTerm().equals(bil.getLeftTerm())
                    && this.getLeftTerm().equals(bil.getRightTerm())
                    && this.getMirroredOperator().equals(bil.getOperator()))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.leftTerm != null ? this.leftTerm.hashCode() : 0);
        hash = 71 * hash + (this.rightTerm != null ? this.rightTerm.hashCode() : 0);
        hash = 71 * hash + (this.operation != null ? this.operation.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getLeftTerm().getName() + getOperator() + getRightTerm().getName();
    }

    @Override
    protected Map<String, String> getVariableToVariableSubstitutionSpecific(Map<String, String> substitution, Literal thatLiteral) {
        if(thatLiteral instanceof BuiltInLiteral){
            BuiltInLiteral builtInLiteral = (BuiltInLiteral) thatLiteral;
            if(builtInLiteral.getOperator().equals(this.getOperator())){
                Map<String, String> newSubstitution = this.leftTerm.getVariableToVariableUnification(builtInLiteral.leftTerm, substitution);
                if(newSubstitution != null){
                    return this.rightTerm.getVariableToVariableUnification(builtInLiteral.rightTerm, newSubstitution);
                }
                //If the operation is "=" or "<>", try to reverse the terms
                if(builtInLiteral.getOperator().equals("=") || builtInLiteral.getOperator().equals("<>")){
                    newSubstitution = this.leftTerm.getVariableToVariableUnification(builtInLiteral.rightTerm, substitution);
                    if(newSubstitution != null){
                        return this.rightTerm.getVariableToVariableUnification(builtInLiteral.leftTerm, newSubstitution);
                    }
                }
            }
        }
        return null;
    }

    public boolean isLeftTermVariable() {
        return this.leftTerm.isVariable();
    }

    public boolean isRightTermVariable() {
        return this.rightTerm.isVariable();
    }

    public String getLeftTermName() {
        return this.leftTerm.getName();
    }

    public String getRightTermName() {
        return this.rightTerm.getName();
    }

    /**
     * 
     * @return an string representing the operation after mirrorring it. For example
     * if this operation is "<" this funciton returns ">".
     */
    private String getMirroredOperator() {
        Map<String, String> mirrorringMap = new HashMap<>();
        mirrorringMap.put("<", ">");
        mirrorringMap.put("<=", ">=");
        mirrorringMap.put("=", "=");
        mirrorringMap.put(">=", "<=");
        mirrorringMap.put(">", "<");
        mirrorringMap.put("<>", "<>");
        assert mirrorringMap.containsKey(this.operation):"Unknown operation " + this.operation+ " for mirrorring";
        return mirrorringMap.get(this.operation);
    }

    @Override
    public Literal getLiteralWithVariableSuffix(String suffix) {
        BuiltInLiteral result = new BuiltInLiteral(this);
        if(result.isLeftTermVariable())result.leftTerm.setSuffix(suffix);
        if(result.isRightTermVariable())result.rightTerm.setSuffix(suffix);
        return result;
    }

    @Override
    public List<String> getTermNamesList() {
        List<String> result = new LinkedList<>();
        result.add(this.leftTerm.getName());
        result.add(this.rightTerm.getName());
        return result;
    }
}
