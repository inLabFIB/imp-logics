package edu.upc.imp.old.logicschema;

import java.util.*;
import java.util.Map.Entry;


/**
 * Implementation of a logic normal clause
 */
public abstract class NormalClause {
    /**
     * Body
     */
    private final List<Literal> literals;

    /**
     * Constructs a NormalClause using the given literals.
     */
    public NormalClause(List<Literal> literals) {
        assert literals != null : "Cannot create a NormalClause with a null body";
        assert literals.size() > 0 : "Cannot create a NormalClause with an empty body";
        for (Literal lit : literals) assert lit != null;
        this.literals = literals;
    }

    /**
     * Constructs a NormalClause by copying the literals
     * of the body
     */
    public NormalClause(NormalClause nc) {
        this.literals = new LinkedList<>();
        for (Literal ncLiteral : nc.literals) {
            this.literals.add(ncLiteral.copy());
        }
    }

    /**
     * @return a copied list of the actual literals
     */
    public List<Literal> getLiterals() {
        return new LinkedList<>(literals);
    }

    /**
     * @return a list of the actual literals copied
     */
    public List<Literal> getLiteralsCopied() {
        List<Literal> result = new LinkedList<>();
        for (Literal lit : getLiterals()) {
            result.add(lit.copy());
        }
        return result;
    }

    /**
     * @return a copied list of the actual OrdinaryLiterals
     */
    public List<OrdinaryLiteral> getOrdinaryLiterals() {
        LinkedList<OrdinaryLiteral> oliterals = new LinkedList<>();
        for (Literal literal : this.literals) {
            if (literal instanceof OrdinaryLiteral) {
                oliterals.add((OrdinaryLiteral) literal);
            }
        }
        return oliterals;
    }

    /**
     * @return the variable names of all the literals of the normal clause
     */
    public Set<String> getVariablesNames() {
        Set<String> result = new HashSet<>();
        for (Literal literal : this.literals) {
            result.addAll(literal.getVariablesNames());
        }
        return result;
    }

    /**
     * @return the variable names of the head of the normal clause
     */
    public Set<String> getVariableNamesInHead() {
        return new HashSet<>();
    }

    /**
     * @return a copied list of the actual negated derived literals
     */
    public List<OrdinaryLiteral> getNegatedDerivedLiterals() {
        LinkedList<OrdinaryLiteral> repairs = new LinkedList<>();
        for (Literal literal : this.literals) {
            if (literal instanceof OrdinaryLiteral
                    && !((OrdinaryLiteral) literal).isPositive()
                    && !((OrdinaryLiteral) literal).isBase())
                repairs.add((OrdinaryLiteral) literal);
        }
        return repairs;
    }

    /**
     * @return a copied list of the actual built in literals
     */
    public List<BuiltInLiteral> getBuiltInLiterals() {
        LinkedList<BuiltInLiteral> biliterals = new LinkedList<>();
        for (Literal literal : this.literals) {
            if (literal instanceof BuiltInLiteral)
                biliterals.add((BuiltInLiteral) literal);
        }
        return biliterals;
    }

    @Override
    public String toString() {
        String result = this.getHeadAsString() + " :- ";
        boolean commaRequired = false;
        for (Literal lit : this.literals) {
            if (commaRequired) result += ", ";
            result += lit.toString();
            commaRequired = true;
        }
        return result;
    }

    /**
     * @return a set containing the name of all the safe variables of the normal clause. A variable
     * is safe if it appears in a positive literal in the body.
     */
    public Set<String> getSafeVariablesNames() {
        Set<String> result = new HashSet<>();
        for (OrdinaryLiteral olit : this.getOrdinaryLiterals()) {
            if (olit.isPositive()) {
                result.addAll(olit.getVariablesNames());
            }
        }
        return result;
    }

    /**
     * @return a set containing a copy of the safe terms of this. A variable
     * is safe if it appears in a positive literal in the body.
     */
    public Set<Term> getSafeVariables() {
        Set<Term> result = new HashSet<>();
        for (OrdinaryLiteral olit : this.getOrdinaryLiterals()) {
            if (olit.isPositive()) {
                result.addAll(olit.getVariables());
            }
        }
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof NormalClause) {
            NormalClause nc = (NormalClause) o;
            return this.getLiterals().size() == nc.getLiterals().size() &&
                    this.getVariableToVariableSubstitution(nc) != null &&
                    nc.getVariableToVariableSubstitution(this) != null;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.literals) + hashCodeSpecific();
        return hash;
    }

    protected abstract String getHeadAsString();

    /**
     * Hook method for adding extra hash values of predicate subclass fields.
     */
    protected abstract int hashCodeSpecific();

    /**
     * @return a Substitution from the variables of this to the variables of nc such that
     * the body of this is included in the body of nc and the head of this is equal to the head
     * of nc, or null if this is not possible.
     */
    public Map<String, String> getVariableToVariableSubstitution(NormalClause nc) {
        Map<String, String> headSubstitution = this.getVariableToVariableSubstitutionForHead(nc);
        if (headSubstitution != null) {
            Map<String, String> substitution = getVariableToVariableSubstitutionForLiterals(headSubstitution, this.literals, nc.getLiterals());

            //Checking that unsafe variables are still unsafe
            if (substitution != null) {
                for (Entry<String, String> entry : substitution.entrySet()) {
                    String originalVariable = entry.getKey();
                    if (!this.getSafeVariablesNames().contains(originalVariable)) {
                        String mappedVariable = entry.getValue();
                        if (nc.getSafeVariablesNames().contains(mappedVariable)) {
                            return null;
                        }
                    }
                }
            }

            return substitution;
        }
        return null;
    }

    protected abstract Map<String, String> getVariableToVariableSubstitutionForHead(NormalClause nc);

    /**
     * @param substitution initial substitution to start with. You might invoke it with an empty substitution
     * @return a new substitution that, when applied to makes source literals, makes source literals be contained in target literals
     */
    public static Map<String, String> getVariableToVariableSubstitutionForLiterals(Map<String, String> substitution, List<Literal> source, List<Literal> target) {
        List<Literal> sortedLitersls = getLiteralsAdvancingBuiltInLiterals(source);
        return getVariableToVariableSubstitutionForLiteralsRecursive(substitution, sortedLitersls.listIterator(), target);
    }

    private static Map<String, String> getVariableToVariableSubstitutionForLiteralsRecursive(Map<String, String> substitution, ListIterator<Literal> sourceIterator, List<Literal> target) {
        if (!sourceIterator.hasNext()) return substitution;
        else {
            Literal thisLiteral = sourceIterator.next();
            for (Literal ncLiteral : getVariableUnifiableLiterals(substitution, thisLiteral, target)) {
                Map<String, String> newSubstitution = thisLiteral.getVariableToVariableSubstitution(substitution, ncLiteral);
                Map<String, String> result = getVariableToVariableSubstitutionForLiteralsRecursive(newSubstitution, sourceIterator, target);
                if (result != null) {
                    sourceIterator.previous();
                    return result;
                }
            }
            sourceIterator.previous();
            return null;
        }
    }

    /**
     * @return a list of the literals of this that can be unified with thatLiteral after applying the substitution S,
     * but without substituting any variable for a term.
     */
    private List<Literal> getVariableUnifiableLiterals(Literal thatLiteral, Map<String, String> substitution) {
        return getVariableUnifiableLiterals(substitution, thatLiteral, this.literals);
    }

    /**
     * @return a list of the literals of target that can be unified with sourceLiteral after applying the substitution,
     * but without substituting any variable for a term.
     */
    public static List<Literal> getVariableUnifiableLiterals(Map<String, String> substitution, Literal sourceLiteral, List<Literal> target) {
        List<Literal> result = new LinkedList<>();
        for (Literal thisLiteral : target) {
            if (sourceLiteral.isVariableUnifiable(thisLiteral, substitution)) result.add(thisLiteral);
        }
        return result;
    }


    public abstract NormalClause copyChangingBody(List<Literal> bodyCopy);

    /**
     * @return a set containing all the predicates used in the NormalClause and
     * in its appearing derived predicates.
     */
    public Set<Predicate> getAllPredicatesClosure() {
        Set<Predicate> result = new HashSet<>();
        for (Predicate p : this.getBodyPredicates()) {
            result.add(p);
            result.addAll(p.getAllPredicatesClosureInDefinitionRules());
        }
        return result;
    }

    /**
     * @return a set containing all the derivationRules closure used in this body
     */
    public Set<DerivationRule> getAllDerivationRulesClosure() {
        Set<DerivationRule> result = new HashSet<>();
        for (Predicate p : this.getAllPredicatesClosure()) {
            result.addAll(p.getDefinitionRules());
        }
        return result;
    }

    /**
     * @return a set containing all the predicates used in the body of the NormalClause
     * but not including those used in the derivation rules of such predicates.
     */
    protected Set<Predicate> getBodyPredicates() {
        Set<Predicate> result = new HashSet<>();
        for (OrdinaryLiteral l : this.getOrdinaryLiterals()) {
            result.add(l.getPredicate());
        }
        return result;
    }

    public NormalClause getNormalClauseAfterSubstitution(Map<String, String> substitution) {
        LinkedList<Literal> newBody = new LinkedList<>();
        for (Literal lit : this.literals) {
            newBody.add(lit.getLiteralAfterSubstitution(substitution));
        }
        return this.copyChangingBody(newBody);
    }

    /**
     * @return a list containing all the positive OrdinaryLiterals directly
     * from the body or in the body of a positive derivation rule
     */
    public List<OrdinaryLiteral> getPositiveOrdinaryLiterals() {
        List<OrdinaryLiteral> result = new LinkedList<>();
        for (OrdinaryLiteral oliteral : this.getOrdinaryLiterals()) {
            if (oliteral.isPositive()) {
                Set<String> forbiddenTermNames = new HashSet<>(this.getVariablesNames());
                forbiddenTermNames.removeAll(oliteral.getVariablesNames());
                for (List<Literal> list : oliteral.getUnfoldedLiteral(forbiddenTermNames)) {
                    for (Literal lit : list) {
                        if (lit instanceof OrdinaryLiteral) {
                            OrdinaryLiteral oli = (OrdinaryLiteral) lit;
                            if (oli.isPositive()) result.add(oli);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return a list containing all the positive OrdinaryLiterals whose predicateName is predicateName.
     * Directly from the body or in the body of a positive derivation rule
     */
    public List<OrdinaryLiteral> getPositiveOrdinaryLiteralsByPredicateName(String predicateName) {
        List<OrdinaryLiteral> result = new LinkedList<>();
        for (OrdinaryLiteral oliteral : this.getOrdinaryLiterals()) {
            if (oliteral.isPositive()) {
                if (oliteral.getPredicateName().equals(predicateName)) {
                    result.add(oliteral);
                } else if (!oliteral.isBase()) {
                    for (DerivationRule rule : oliteral.getPredicate().getDefinitionRules()) {
                        result.addAll(rule.getPositiveOrdinaryLiteralsByPredicateName(predicateName));
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return a list containing all the positive ordinary literals that uses the term given by parameter.
     * This method does not unfold derived literals.
     */
    public List<OrdinaryLiteral> getPositiveOrdinaryLiteralsByTerm(Term term) {
        List<OrdinaryLiteral> result = new LinkedList<>();
        for (OrdinaryLiteral oliteral : this.getOrdinaryLiterals()) {
            if (oliteral.isPositive()) {
                if (oliteral.getTerms().contains(term)) {
                    result.add(oliteral);
                }
            }
        }
        return result;
    }

    /**
     * @return a list containing all the base predicate names used in the normal clause
     */
    public List<String> getAllPredicatesNamesClosure() {
        List<String> result = new LinkedList<>();
        for (Predicate p : this.getAllPredicatesClosure()) {
            if (!result.contains(p.getName())) {
                result.add(p.getName());
            }
        }
        return result;
    }

    /**
     * @return the base literals of this normal clause
     */
    public List<Literal> getBaseAndBuiltInLiterals() {
        List<Literal> result = new LinkedList<>();
        for (Literal lit : this.getLiterals()) {
            if (lit instanceof BuiltInLiteral) {
                result.add(lit);
            } else {
                OrdinaryLiteral olit = (OrdinaryLiteral) lit;
                if (olit.isBase()) {
                    result.add(olit);
                }
            }
        }
        return result;
    }

    /**
     * @return the given literals but advancing the built-in-literals.
     * Ex:
     * P(x, y), Q(y, z), x > y
     * Returns:
     * P(x, y), x > y, Q(y, z)
     */
    private static List<Literal> getLiteralsAdvancingBuiltInLiterals(List<Literal> literals) {
        List<OrdinaryLiteral> ordinaryLiterals = new LinkedList<>();
        List<BuiltInLiteral> builtInLiterals = new LinkedList<>();

        for (Literal lit : literals) {
            if (lit instanceof OrdinaryLiteral) {
                ordinaryLiterals.add((OrdinaryLiteral) lit);
            } else if (lit instanceof BuiltInLiteral) {
                builtInLiterals.add((BuiltInLiteral) lit);
            } else
                assert false : "Wait? " + lit + " lit is not an OrdinaryLiteral neither a BuiltInLiteral, but a " + lit.getClass().getName();
        }

        List<Literal> result = new LinkedList<>();
        Set<String> variables = new HashSet<>();
        for (Literal lit : ordinaryLiterals) {
            result.add(lit);
            variables.addAll(lit.getVariablesNames());
            Iterator<BuiltInLiteral> iterator = builtInLiterals.iterator();
            while (iterator.hasNext()) {
                BuiltInLiteral bil = iterator.next();
                if (variables.containsAll(bil.getVariablesNames())) {
                    result.add(bil);
                    iterator.remove();
                }
            }
        }

        for (Iterator<BuiltInLiteral> it = builtInLiterals.iterator(); it.hasNext(); ) {
            BuiltInLiteral bil = it.next();
            result.add(bil);
            it.remove();
        }
        assert builtInLiterals.isEmpty() : "BuiltInLiterals is not empty!: " + builtInLiterals + " " + literals;

        return result;
    }
}
