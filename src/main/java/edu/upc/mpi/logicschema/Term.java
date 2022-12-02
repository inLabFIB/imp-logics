package edu.upc.mpi.logicschema;

import java.util.*;


/**
 * Implementation of a logic Term. A Term is either a variable or a constant.
 * We assume that constants are:
 *  - reals (e.g. -1.0, 0, 2, 2.5, ...)
 *  - strings surrounded by " " (e.g. "John", "Mary").
 * Anything else is a variable (e.g. x, y, John, Mary, etc)
 */
public class Term {
    private String name;
    
    
    /**
     * @return true iff name does not contain any invalid character (',','(', ...)
     */
    private boolean isValidName(String name){
        List<Character> forbiddenSymbols = new LinkedList<>();
        forbiddenSymbols.add(',');
        forbiddenSymbols.add('(');
        forbiddenSymbols.add(')');
        forbiddenSymbols.add('%');
        
        for(char c: name.toCharArray()){
            if(forbiddenSymbols.contains(c)) return false;
        }
        
        return true;
    }
    

    /**
     * Constructs a new Term term with the given name.
     * The term will be considered a constant if the given name starts and ends with the
     * symbol ", or if it can be cast to a Real Number.
     * 
     * @param name != null && name != ""
     */
    public Term(String name) {
        assert name != null:"The name of a term cannot be null";
        assert !name.equals(""):"The name of a term cannot be empty";
        assert isValidName(name):"Given term name is not valid: "+name;
        this.name = name;
    }

    /**
     * Constructs a new constant term with the given value
     */
    public Term(int value) {
        name = Integer.toString(value);
    }

    /**
     * Constructs a copy of the term
     */
    public Term copy(){
        Term copy = this.copySpecific();
        copy.name = this.name;
        return copy;
    }
    
    /**
     * Hook method
     */
    protected Term copySpecific(){
        return new Term(this.name);
    }

    public String getName() {
        return name;
    }
    
    /**
     * Renames the term with the given name. You should ensure that this term
     * is a variable before calling this method.
     * 
     * @param name != null && name != ""
     */
    public void setName(String name) {
        assert name != null:"The name of a term cannot be null";
        assert !name.equals(""):"The name of a term cannot be empty";
        assert this.nameIsModifiable():"This term cannot change its name";
        assert this.isValidName(name):"Given term name is not valid: "+name;
        this.name = name;
    }

    public boolean isConstant() {
        if(name.startsWith("\"") && name.endsWith("\"") ||
           name.startsWith("'") && name.endsWith("'")){
            return true;
        }
        else {
            try{
                Double.parseDouble(name);
                return true;
            } catch(NumberFormatException exc){
                return false;
            }
        }
    }

    public boolean nameIsModifiable(){
        return !this.isConstant();
    }

    public boolean isVariable() {
        return !isConstant();
    }

    /**
     * Returns a copy of this term after applying the given substitution, if
     * substitution is not null. Otherwise, it just returns a copy of this.
     */
    public Term getSubstitutedTerm(Map<String, String> substitution) {
        if (substitution != null && this.isVariable() && substitution.containsKey(this.getName())) {
            return new Term(substitution.get(this.getName()));
        }else return this.copy();
    }

    /**
     * Adds the given suffix to the term name if it is a variable.
     * You should ensure that this term is a variable before calling this method.
     */
    public void setSuffix(String suffix) {
        String newName = this.getName()+suffix;
        this.setName(this.getName()+suffix);
    }

    /**
     * @param thatVariable != null
     * @param substitution != null
     * @return a new substitution containing the given substitution, but possibly adding
     * a replacement from this variable to thatVariable. If thatVariable is not a variable
     * (whereas this is) or such substitution does not exists, it returns null.
     * If this and thatVariable are constants with the same value, it returns
     * a copy of the given substitution.
     */
    protected Map<String, String> getVariableToVariableUnification(Term thatVariable, Map<String, String> substitution) {
        assert thatVariable != null : "thatVariable cannot be null";
        assert substitution != null : "substitution cannot be null";

        Map<String, String> result = new HashMap<>(substitution);
        if (result.containsKey(this.getName())) {
            assert this.isVariable() : "The substitution should not map the constant term: " + this.getName();
            String thisSubstituted = substitution.get(this.getName());
            if (thatVariable.getName().equals(thisSubstituted)) return result;
            else return null;
        } else {
            if (this.isConstant()) {
                return this.getName().equals(thatVariable.getName()) ? result : null;
            } else {
                if (thatVariable.isVariable()) {
                    result.put(this.getName(), thatVariable.getName());
                    return result;
                } else return null;
            }
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public boolean equals(Object o){
        if(o instanceof Term){
            Term to = (Term)o;
            return this.name.equals(to.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);       
        return hash;
    }
    
    public static List<Term> getTermList(String[] terms){
        List<Term> result = new LinkedList<>();
        for(String term: terms){
            result.add(new Term(term));
        }
        return result;
    }

}
