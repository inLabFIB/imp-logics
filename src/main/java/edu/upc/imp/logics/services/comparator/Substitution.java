package edu.upc.imp.logics.services.comparator;

import edu.upc.imp.logics.schema.Term;
import edu.upc.imp.logics.schema.Variable;

import java.util.*;

/**
 * This class represents a substitution of variables to terms.
 * I.e., it is a mapping from variables to terms, where each variable can be mapped, at most, to one term.
 */
public class Substitution {
    private Map<Variable, Term> termsMap = new HashMap<>();

    /**
     * Creates empty substitution
     */
    public Substitution() {

    }

    /**
     * Constructor by copy
     *
     * @param toCopy not null
     */
    public Substitution(Substitution toCopy) {
        if (Objects.isNull(toCopy)) throw new IllegalArgumentException("toCopy cannot be null");
        this.termsMap = new HashMap<>(toCopy.termsMap);
    }


    /**
     * Creates a Substitution that replaces each i-th term from domainTerms to unify with the i-th rangeTerm
     * If such substitution does not exist, it throws a SubstitutionException.
     * Such substitution might not exist, for instance, if the domainTerms contains a constant in the i-th position,
     * and the rangeTerms has a different term (different constant, or variable), in the i-th position.
     *
     * @param domainTerms not null
     * @param rangeTerms  not null
     */
    public Substitution(List<Term> domainTerms, List<Term> rangeTerms) {
        if (Objects.isNull(domainTerms)) throw new IllegalArgumentException("Domain terms cannot be null");
        if (Objects.isNull(rangeTerms)) throw new IllegalArgumentException("Range terms cannot be null");
        if (domainTerms.size() != rangeTerms.size()) {
            throw new SubstitutionException("Cannot create substitutions with different terms size");
        }

        for (int i = 0; i < domainTerms.size(); i++) {
            unifyTermsInSubstitution(domainTerms.get(i), rangeTerms.get(i));
        }
    }

    private void unifyTermsInSubstitution(Term domainTerm, Term rangeTerm) {
        if (domainTerm instanceof Variable domainVariable) {
            this.addMappingIfNotIncluded(domainVariable, rangeTerm);
        } else {
            if (!domainTerm.equals(rangeTerm)) {
                throw new SubstitutionException(
                        "Cannot map non-variable term " + domainTerm.getName() +
                                " to " + rangeTerm.getName());
            }
        }
    }

    /**
     * Creates a new Substitution making the union between this Substitution, and the otherSubstitution.
     * If both substitutions try to map the same variable to different terms, it throws an exception.
     *
     * @param otherSubstitution not null
     * @return a union of substitutions
     */
    public Substitution union(Substitution otherSubstitution) {
        if (Objects.isNull(otherSubstitution)) throw new IllegalArgumentException("otherSubstitution cannot be null");

        Substitution result = new Substitution(this);
        for (Map.Entry<Variable, Term> otherMapping : otherSubstitution.termsMap.entrySet()) {
            result.addMappingIfNotIncluded(otherMapping.getKey(), otherMapping.getValue());
        }
        return result;
    }

    /**
     * Include in this substitution a new mapping from the domainVariable to the rangeTerm
     * If such mapping already exist, it throws a SubstitutionException if the rangeTerm is different compared to the current
     * image of the domainVariable
     *
     * @param domainVariable not null
     * @param rangeTerm      not null
     */
    public void addMappingIfNotIncluded(Variable domainVariable, Term rangeTerm) {
        if (Objects.isNull(domainVariable)) throw new IllegalArgumentException("domainVariable cannot be null");
        if (Objects.isNull(rangeTerm)) throw new IllegalArgumentException("rangeTerm cannot be null");

        Term currentTermImage = termsMap.get(domainVariable);
        if (Objects.isNull(currentTermImage)) {
            termsMap.put(domainVariable, rangeTerm);
        } else if (!currentTermImage.equals(rangeTerm)) {
            throw new SubstitutionException(
                    "Substitution already maps " + domainVariable.getName() +
                            " to " + currentTermImage.getName() +
                            " which is not equal to " + rangeTerm.getName());
        }
    }

    /**
     * @param variable not null
     * @return the image of the variable
     */
    public Optional<Term> getTerm(Variable variable) {
        if (Objects.isNull(variable)) throw new IllegalArgumentException("Variable cannot be null");

        return Optional.ofNullable(termsMap.get(variable));
    }

    public int getSize() {
        return this.termsMap.size();
    }

    public boolean isEmpty() {
        return this.termsMap.isEmpty();
    }
}
