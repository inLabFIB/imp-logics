package edu.upc.imp.old.logicschema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the logic literal.
 *
 */
public abstract class Literal {
        public abstract Set<String> getVariablesNames();
        public abstract Literal getLiteralAfterSubstitution(Map<String, String> substitution);
        public abstract Literal copy();

        /**
         * @return true iff this, after applying substitution, can be unified with that, without changing
         * any variable for a constant, neither changing any of the current terms present in substitution
         */
        public boolean isVariableUnifiable(Literal thatLiteral, Map<String, String> substitution){
            return this.getVariableToVariableSubstitution(substitution, thatLiteral) != null;
        }


        /**
         * @return a new substitution S, containing the substitution given by parameter, such that
         * this unifies with thatLiteral, but without substituting any variable for a constant
         */
    public Map<String, String> getVariableToVariableSubstitution(Map<String, String> substitution, Literal thatLiteral) {
        Map<String, String> newSubstitution = new HashMap<>(substitution);
        return getVariableToVariableSubstitutionSpecific(newSubstitution, thatLiteral);
    }

    public abstract List<String> getTermNamesList();

    /**
     * @return a copy of this but adding the given suffix to each variable term
     */
    public abstract Literal getLiteralWithVariableSuffix(String suffix);

    protected abstract Map<String, String> getVariableToVariableSubstitutionSpecific(Map<String, String> substitution, Literal lit);


}




