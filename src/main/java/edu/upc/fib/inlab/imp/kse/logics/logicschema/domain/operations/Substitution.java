package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.exceptions.SubstitutionException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a substitution of variables to terms. I.e., it is a mapping from variables to terms, where
 * each variable can be mapped, at most, to one term.
 */
public class Substitution {
    private Map<Variable, Term> termsMap = new HashMap<>();

    /**
     * Constructs an empty {@code Substitution}.
     */
    public Substitution() {
    }

    /**
     * Constructs a new {@code Substitution} with the same mappings as the specified {@code Substitution}.
     *
     * @param toCopy    the substitution whose mappings are to be placed in this substitution.
     * @throws IllegalArgumentException if toCopy parameter is {@code null}.
     */
    public Substitution(Substitution toCopy) {
        if (Objects.isNull(toCopy)) throw new IllegalArgumentException("Input substitution cannot be null");
        this.termsMap = new HashMap<>(toCopy.termsMap);
    }

    /**
     * Constructs a new {@code Substitution} that maps each i-th term from domainTerms with the i-th rangeTerm.
     * <p>
     * If such {@code Substitution} does not exist, it throws a {@code SubstitutionException}. This could happen if the
     * domainTerms contains a {@code Constant} in the i-th position and the rangeTerms has a different term
     * (different {@code Constant}, or {@code Variable}), in the i-th position.
     *
     * @param domainTerms   list of {@code Term} which forms the domain of the substitution mapping.
     * @param rangeTerms    list of {@code Term} which forms the range of the substitution mapping.
     * @throws IllegalArgumentException if domainTerms or rangeTerms parameters are {@code null}.
     * @throws SubstitutionException    if arity mismatch between domain and range or if domain {@code Constant} is
     *                                  mapped to different {@code Constant} or {@code Variable}.
     */
    public Substitution(List<Term> domainTerms, List<Term> rangeTerms) {
        if (Objects.isNull(domainTerms)) throw new IllegalArgumentException("Domain terms cannot be null");
        if (Objects.isNull(rangeTerms)) throw new IllegalArgumentException("Range terms cannot be null");
        if (domainTerms.size() != rangeTerms.size())
            throw new SubstitutionException("Cannot create substitutions with different terms size");

        for (int i = 0; i < domainTerms.size(); i++) unifyTermsInSubstitution(domainTerms.get(i), rangeTerms.get(i));
    }

    private void unifyTermsInSubstitution(Term domainTerm, Term rangeTerm) {
        if (domainTerm instanceof Variable domainVariable) {
            this.addMapping(domainVariable, rangeTerm);
        } else {
            if (!domainTerm.equals(rangeTerm)) {
                throw new SubstitutionException(
                        "Cannot map non-variable term " + domainTerm.getName() +
                                " to " + rangeTerm.getName());
            }
        }
    }

    /**
     * Constructs a new {@code Substitution} making the union between this {@code Substitution}, and the
     * otherSubstitution.
     *
     * @param otherSubstitution different {@code Substitution}.
     * @return                  a new {@code Substitution} as a union of substitutions.
     * @throws IllegalArgumentException if the other {@code Substitution} is {@code null}.
     * @throws SubstitutionException    if both substitutions try to map the same variable to different terms.
     */
    public Substitution union(Substitution otherSubstitution) {
        if (Objects.isNull(otherSubstitution)) throw new IllegalArgumentException("otherSubstitution cannot be null");

        Substitution result = new Substitution(this);
        for (Map.Entry<Variable, Term> otherMapping : otherSubstitution.termsMap.entrySet()) {
            result.addMapping(otherMapping.getKey(), otherMapping.getValue());
        }
        return result;
    }

    /**
     * Modifies the {@code Substitution} adding a new mapping from the domainVariable to the rangeTerm.
     *
     * @param domainVariable    domain variable to be mapped.
     * @param rangeTerm         range term to be mapped.
     * @throws IllegalArgumentException if the domain {@code Variable} or the range {@code Term} are {@code null}.
     * @throws SubstitutionException    if such mapping already exists in the {@code Substitution}.
     */
    public void addMapping(Variable domainVariable, Term rangeTerm) {
        if (Objects.isNull(domainVariable)) throw new IllegalArgumentException("domainVariable cannot be null");
        if (Objects.isNull(rangeTerm)) throw new IllegalArgumentException("rangeTerm cannot be null");

        Term currentTermImage = termsMap.get(domainVariable);
        if (Objects.isNull(currentTermImage)) termsMap.put(domainVariable, rangeTerm);
        else if (!currentTermImage.equals(rangeTerm)) {
            throw new SubstitutionException(
                    "Substitution already maps " + domainVariable.getName() +
                            " to " + currentTermImage.getName() +
                            " which is not equal to " + rangeTerm.getName());
        }
    }

    /**
     * Returns the image of the {@code Variable}.
     *
     * @param variable  domain {@code Variable}.
     * @return          the image of the {@code Variable}.
     * @throws IllegalArgumentException if input domain {@code Variable} is {@code null}.
     */
    public Optional<Term> getTerm(Variable variable) {
        if (Objects.isNull(variable)) throw new IllegalArgumentException("Variable cannot be null");

        return Optional.ofNullable(termsMap.get(variable));
    }

    /**
     * Returns substitution map size.
     *
     * @return  substitution map size.
     */
    public int getSize() {
        return this.termsMap.size();
    }

    /**
     * Returns {@code true} if this substitution contains no mappings.
     *
     * @return  {@code true} if this substitution contains no mappings
     */
    public boolean isEmpty() {
        return this.termsMap.isEmpty();
    }

    /**
     * Returns {@code true} if any of the variable from the input appear as the domain of a mapping.
     *
     * @param variables set of {@code Variable}.
     * @return          {@code true} if any of the variable from the input appear as the domain of a mapping.
     */
    public boolean replacesSomeVariableOf(Set<Variable> variables) {
        return variables.stream().anyMatch(v -> this.termsMap.containsKey(v));
    }

    /**
     * Returns all the variables appearing in the mappings (either in the domain or the range).
     *
     * @return  all the variables appearing in the mappings (either in the domain or the range).
     */
    public Set<Variable> getUsedVariables() {
        Set<Variable> variablesInDomain = this.termsMap.keySet();
        Set<Variable> variablesInRange = this.termsMap.values().stream()
                .filter(Term::isVariable)
                .map(t -> new Variable(t.getName()))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Variable> result = new LinkedHashSet<>(variablesInDomain);
        result.addAll(variablesInRange);
        return result;
    }

    /**
     * Returns {@code true} if all mappings of the substitutions map variable to identical variables.
     *
     * @return  {@code true} if all mappings of the substitutions map variable to identical variables.
     */
    public boolean isIdentity() {
        return this.termsMap.entrySet()
                .stream()
                .allMatch(entry ->
                        entry.getValue().isVariable() &&
                                entry.getValue().getName().equals(entry.getKey().getName()));
    }

}
