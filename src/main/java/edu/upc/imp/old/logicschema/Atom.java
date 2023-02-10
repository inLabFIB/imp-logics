package edu.upc.imp.old.logicschema;

import java.util.*;


/**
 * Implementation of a logic Atom.
 * An Atom consists of a Predicate (e.g. "Employee") together with a list of Terms.
 */
public class Atom {
    private final List<Term> terms;
    private final Predicate predicate;

    /**
     * Constructs a new Atom copying the terms of the given atom and using the same predicate.
     */
    public Atom(Atom atom) {
        assert atom != null : "Atom cannot be null";

        terms = new LinkedList<>();
        for (Term term : atom.terms) {
            terms.add(term.copy());
        }
        predicate = atom.predicate;
    }

    /**
     * Constructs a new Atom using the predicate and list of terms passed by
     * parameter.
     */
    public Atom(Predicate predicate, List<Term> terms) {
        assert predicate != null : "Predicate cannot be null";
        assert terms != null : "Terms cannot be null";
        assert terms.size() == predicate.getArity() : "Predicate " + predicate.getName() + " has arity " + predicate.getArity() + " but you are giving " + terms.size() + " terms";
        for (Term term : terms) {
            assert term != null : "Null term found in " + terms;
        }

        this.terms = terms;
        this.predicate = predicate;
    }

    /**
     * @return a copied list of the actual terms
     */
    public List<Term> getTerms() {
        return new LinkedList<>(terms);
    }

    /**
     * @return a list of copied terms
     */
    public List<Term> getTermsCopied() {
        List<Term> result = new LinkedList<>();
        for (Term term : this.terms) {
            result.add(term.copy());
        }
        return result;
    }

    /**
     * @return a list containing the names of all the terms in this atom
     */
    public List<String> getTermsNamesList() {
        List<String> result = new LinkedList<>();
        for (Term t : this.getTerms()) {
            result.add(t.getName());
        }
        return result;
    }

    /**
     * @return the actual term appearing in the given index
     */
    public Term getTerm(int index) {
        return this.terms.get(index);
    }

    /**
     * @return a set containing the names of the variable terms of this atom
     */
    public Set<String> getVariablesNames() {
        Set<String> result = new HashSet<>();
        for (Term term : this.getTerms()) {
            if (term.isVariable()) {
                result.add(term.getName());
            }
        }
        return result;
    }

    /**
     * @return a copied list of the actual variables of this atom
     */
    public List<Term> getVariables() {
        List<Term> result = new LinkedList<>();
        for (Term term : this.getTerms()) {
            if (term.isVariable()) {
                result.add(term);
            }
        }
        return result;
    }

    /**
     * @return the indexes of the term appearances of the given term in this atom, in ascending order. It might be empty
     */
    public List<Integer> getIndexesOfTerm(Term term) {
        List<Integer> result = new LinkedList<>();
        for (int i = 0; i < this.getPredicateArity(); ++i) {
            if (this.terms.get(i).equals(term)) {
                result.add(i);
            }
        }
        return result;
    }

    /**
     * @return the actual predicate
     */
    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * @return true iff the atom's predicate is base
     */
    public boolean isBase() {
        return this.predicate.isBase();
    }

    /**
     * @return the predicate's name
     */
    public String getPredicateName() {
        return this.predicate.getName();
    }

    /**
     * @return the predicate's arity
     */
    public int getPredicateArity() {
        return this.predicate.getArity();
    }

    /**
     * @return a copy of this atom after applying the given substitution. If the substitution is null,
     * it returns a copy of this atom.
     */
    public Atom getSubstitutedAtom(Map<String, String> substitution) {
        List<Term> newTerms = new LinkedList<>();
        for (Term term : this.getTerms()) {
            newTerms.add(term.getSubstitutedTerm(substitution));
        }
        return new Atom(this.predicate, newTerms);
    }

    /**
     * @return the substitution of this terms that unifies this atom with the given target atom, or
     * null if no unifying substitution exists.
     */
    public Map<String, String> getUnification(Atom target) {
        assert target != null : "Target atom cannot be nulll";

        if (!this.getPredicateName().equals(target.getPredicateName())) {
            return null;
        }
        if (this.getTerms().size() != target.getTerms().size()) {
            return null;
        }

        Map<String, String> result = new HashMap<>();

        Iterator<Term> thisIterator = this.getTerms().iterator();
        Iterator<Term> targetIterator = target.getTerms().iterator();

        while (thisIterator.hasNext() && targetIterator.hasNext()) {
            Term thisTerm = thisIterator.next();
            Term targetTerm = targetIterator.next();

            if (thisTerm.isVariable()) {
                if (result.containsKey(thisTerm.getName())) {
                    if (!result.get(thisTerm.getName()).equals(targetTerm.getName())) {
                        return null;
                    }
                } else {
                    result.put(thisTerm.getName(), targetTerm.getName());
                }
            } else if (!thisTerm.getName().equals(targetTerm.getName())) {
                return null;
            }
        }
        assert thisIterator.hasNext() == targetIterator.hasNext();

        return result;
    }

    /**
     * @return the substitution containing the currentSubstitution such that unifies this atom with
     * target, but without substituting any variable for a constant; or null if such substitution
     * does not exists
     */
    public Map<String, String> getVariableToVariableUnification(Atom target, Map<String, String> currentSubstitution) {
        if (this.getPredicateName().equals(target.getPredicateName())) {
            Iterator<Term> thisTerms = this.terms.iterator();
            Iterator<Term> targetTerms = target.getTerms().iterator();
            Map<String, String> substitution = new HashMap<>(currentSubstitution);
            while (thisTerms.hasNext()) {
                Term thisTerm = thisTerms.next();
                Term targetTerm = targetTerms.next();
                substitution = thisTerm.getVariableToVariableUnification(targetTerm, substitution);
                if (substitution == null) return null;
            }
            assert !targetTerms.hasNext();
            return substitution;
        } else return null;
    }


    /**
     * @param forbiddenVariableNames a list of variable names.
     * @return a list of list of literals of the derivation rules after applying a term
     * substitution to:
     * - unify this atom with the head of the derivation rule.
     * - replace any variable appearing in the given forbiddenVariableNames for another variable name.
     * If the atom is base, it returns an empty list.
     */
    public List<List<Literal>> getDefinitionRulesWhenCalled(Set<String> forbiddenVariableNames) {
        List<List<Literal>> result = new LinkedList<>();
        for (DerivationRule rule : this.getPredicate().getDefinitionRules()) {
            List<Literal> substitutedLiteralsList = rule.applyCallSubstitution(this, forbiddenVariableNames);
            result.add(substitutedLiteralsList);
        }
        return result;
    }

    /**
     * @return a copy of this atom but adding to its current variables the given suffix
     */
    public Atom getAtomWithVariableSuffix(String suffix) {
        Atom result = new Atom(this);
        for (Term term : result.terms) {
            if (term.isVariable()) {
                term.setSuffix(suffix);
            }
        }
        return result;
    }

    /**
     * @return the number of definition rules of its predicate
     */
    public int getNumberOfDefinitionRules() {
        return this.getPredicate().getNumberOfDefinitionRules();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.terms != null ? this.terms.hashCode() : 0);
        hash = 71 * hash + (this.predicate != null ? this.predicate.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Atom) {
            Atom atom = (Atom) o;
            Iterator<Term> iT1, iT2;
            Term t1, t2;

            if (this.predicate.getName().equals(atom.predicate.getName())) {
                iT1 = this.terms.iterator();
                iT2 = atom.terms.iterator();

                while (iT1.hasNext() && iT2.hasNext()) {
                    t1 = iT1.next();
                    t2 = iT2.next();
                    if (!t1.equals(t2)) {
                        return false;
                    }
                }
                return iT1.hasNext() == iT2.hasNext();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(this.getPredicateName() + "(");
        boolean commaRequired = false;
        for (Term term : this.terms) {
            if (commaRequired) {
                result.append(",");
            }
            result.append(term.toString());
            commaRequired = true;
        }
        return result + ")";
    }
}
