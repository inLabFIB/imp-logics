package edu.upc.mpi.logicschema;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of a logic predicate.
 *
 */
public abstract class Predicate{
    private final List<DerivationRule> definitionRules = new LinkedList();

    public abstract String getName();
    public abstract int getArity();
    
    /**
     * @return true iff this predicate is base (not derived)
     */
    public boolean isBase(){
        return this.definitionRules.isEmpty();
    }
    
    /**
     * Add a definition rule for this predicate.
     * 
     * @param definitionRule a rule whose head's predicate is this
     */
    protected void addDerivationRule(DerivationRule definitionRule){
        assert definitionRule!=null:"definitionRule cannot be null";
        assert definitionRule.getPredicateName().equals(this.getName());
        
        this.definitionRules.add(definitionRule);
    }

    /**
     * @return a copied list of the actual definition rules for the predicate. It returns an empty
     * list if the predicate is base
     */
    public List<DerivationRule> getDefinitionRules(){
        assert definitionRules != null:"";
        
        return new LinkedList(definitionRules);
    }
    
    /**
     *
     * @return the number of definition rules
     */
    public int getNumberOfDefinitionRules() {
        return this.getDefinitionRules().size();
    }
    
    /**
     * @return all the predicates used in the definition rules of this
     */
    public Set<Predicate> getAllPredicatesClosureInDefinitionRules(){
        Set<Predicate> result = new HashSet();
        computeAllPredicatesInDefinitionRules(result);
        return result;
    }

    /**
     * Computes the all predicates used in this definition rules but not already included in predicates
     * @param predicates
     */
    private void computeAllPredicatesInDefinitionRules(Set<Predicate> predicates){
        for(DerivationRule rule: this.getDefinitionRules()){
            for(Predicate p: rule.getBodyPredicates()){
                if(predicates.add(p)) {
                    p.computeAllPredicatesInDefinitionRules(predicates);
                }
            }
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Predicate){
            Predicate p = (Predicate) o;
            return this.getName().equals(p.getName());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        //hash = 59 * hash + Objects.hashCode(this.definitionRules);//cannot use this for computing the hash because it would create an infinite loop
        hash = 59 * hash + Objects.hashCode(this.getName());
        return hash;
    }


    @Override
    public String toString(){
        String result = this.getName()+"(";
        for(int i = 0; i < this.getArity(); ++i){
            if(i>0)result+=",";
            result+="X"+i;
        }
        return result+=")";
    }

    public void deleteDerivationRule(DerivationRule dr) {
        this.definitionRules.remove(dr);
    }
}
