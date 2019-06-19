package edu.upc.mpi.logicschema;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of a logic OrdinaryLiteral
 *
 * @author Guillem Lubary & Xavier Oriol
 */
public class OrdinaryLiteral extends Literal {

    private boolean sign;
    private final Atom atom;

    /**
     * Constructots a new OrdinaryLiteral by copying the atom of the given
     * literal and taking its same sign (positive or negative).
     *
     * @param ol
     */
    public OrdinaryLiteral(OrdinaryLiteral ol) {
        this(ol, ol.sign);
    }

    /**
     * Constructots a new OrdinaryLiteral by copying the atom of the given
     * literal and taking the sign (positive or negative) given by parameter
     *
     * @param ol
     */
    public OrdinaryLiteral(OrdinaryLiteral ol, boolean sign) {
        assert ol != null;
        this.atom = new Atom(ol.atom);
        this.sign = sign;

    }

    /**
     * Constructs a new positive ordinary literal by using the given atom
     *
     * @param atom
     */
    public OrdinaryLiteral(Atom atom) {
        this(atom, true);
    }

    /**
     * Constructts a new ordinary literal using the given atom and sign
     *
     * @param atom
     */
    public OrdinaryLiteral(Atom atom, boolean sign) {
        assert atom != null;
        this.atom = atom;
        this.sign = sign;
    }

    /**
     * @return true iff the sign is positive
     */
    public boolean isPositive() {
        return sign;
    }

    /**
     * @return true iff the sign is negative
     */
    public boolean isNegated() {
        return !isPositive();
    }

    public Atom getAtom() {
        return atom;
    }

    public void setPositive() {
        this.sign = true;
    }

    public void setNegative() {
        this.sign = false;
    }

    /**
     * If the literal was positive it is set to negative
     * and viceversa.
     */
    public void invertSign() {
        this.sign = !this.sign;
    }

    /**
     * @param target
     * @return the substitutions that when applied to this, unifies with target, or null
     * if such substitution does not exists
     */
    public Map<String, String> getUnification(OrdinaryLiteral target) {
        return this.getAtom().getUnification(target.getAtom());
    }

    /**
     *
     * @param substitution
     * @return a copy of this after applying the substitution. If substitution is null
     * it returns a copy of this literal
     */
    public OrdinaryLiteral getLiteralAfterSubstitution(Map<String, String> substitution) {
        OrdinaryLiteral result = new OrdinaryLiteral(this.getAtom().getSubstitutedAtom(substitution));
        if (!this.isPositive()) {
            result.setNegative();
        }
        return result;
    }

    @Override
    /**
     * Two OrdinaryLiterals are equals if their atoms are equal
     */
    public boolean equals(Object o) {
        try {
            OrdinaryLiteral l = (OrdinaryLiteral) o;
            return this.getAtom().equals(l.getAtom()) && this.sign == l.sign;
        } catch (ClassCastException err) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.sign ? 1 : 0);
        hash = 37 * hash + (this.atom != null ? this.atom.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        String s = new String();
        if (!isPositive()) {
            s += "not(";
            s += getAtom().toString();
            s += ")";
        } else {
            s += getAtom().toString();
        }
        return s;
    }

    @Override
    public Set<String> getVariablesNames() {
            return this.atom.getVariablesNames();
    }

    /**
     * 
     * @return a copy of the actual variable terms
     */
    public List<Term> getVariables(){
        return this.atom.getVariables();
    }

    /**
     * @return true iff the atom of this is not derived
     */
    public boolean isBase() {
        return this.atom.isBase();
    }

    @Override
    public OrdinaryLiteral copy() {
        return new OrdinaryLiteral(this);
    }

    @Override
    protected Map<String, String> getVariableToVariableSubstitutionSpecific( Map<String, String> hashMap, Literal thatLiteral) {
        if(thatLiteral instanceof OrdinaryLiteral){
            OrdinaryLiteral oLiteral = (OrdinaryLiteral)thatLiteral;
            if(this.sign == oLiteral.sign){
                return atom.getVariableToVariableUnification(oLiteral.getAtom(), hashMap);
            }
        }
        return null;
    }

    public String getPredicateName() {
        return this.getAtom().getPredicateName();
    }

    public int getPredicateArity() {
        return this.getAtom().getPredicateArity();
    }

    public Term getTerm(int index) {
        return this.getAtom().getTerm(index);
    }

    /**
     * @return the predicate of the atom of this
     */
    public Predicate getPredicate() {
        return this.atom.getPredicate();
    }


    /**
     * @return a copied list of the actual terms used in this ordinary literal
     */
    public List<Term> getTerms() {
        return this.atom.getTerms();
    }


    /**
     *
     * @param term
     * @return the indexes of the term appearances of the given term or an empty list
     */
    public List<Integer> getIndexesOfTerm(Term term) {
        return this.atom.getIndexesOfTerm(term);
    }


    /**
     * @param upperVariableNames a list of the variable names of the level in which
     * the ordinaryliteral is invoked. After applying the method it contains the variables
     * for which we have avoided any unification.
     * @return a list of copies of the derivation rules after applying a term
     * substitution to unify this with the head of the different derivaitionRules
     * where the substitution guarantees not to accidentally unify with upperVariableNames
     * In the literal is base, it returns an empty list.
     *
     */
    public List<List<Literal>> getDefinitionRulesWhenCalled(Set<String> upperVariableNames){
        return this.atom.getDefinitionRulesWhenCalled(upperVariableNames);
    }

    /**
     * @param rule the rule in which this appears
     * @return a list of copies of the derivation rules after applying a term
     * substitution to unify this with the head of the different derivaitionRules
     * where the substitution guarantees not to accidentally unify with some
     * variable of rule
     *
     */
    public List<List<Literal>> getDefinitionRulesWhenCalled(NormalClause rule) {
        Set<String> variablesOfRuleNotToUnify = rule.getVariablesNames();
        //variablesOfRuleNotToUnify.removeAll(this.getVariablesNames());
        return this.getDefinitionRulesWhenCalled(variablesOfRuleNotToUnify);
    }

    public List<List<Literal>> getDefinitionRulesWhenCalled(List<Literal> literals) {
        Set<String> forbiddenVariables = new HashSet();
        for(Literal lit: literals){
            forbiddenVariables.addAll(lit.getVariablesNames());
        }
        //forbiddenVariables.removeAll(this.getVariablesNames());
        return this.getDefinitionRulesWhenCalled(forbiddenVariables);
    }

    /**
     * @rule rule
     * @return a list containing the different possible lists of literals after unfolding this literal recursively.
     * If the literal is base or negated, it just returns a list containing one list of just this.
     * The unfolding takes care not to accidentally bound to some variable of rule.
     */
    public List<List<Literal>> getUnfoldedLiteral(Set<String> forbiddenTerms){
        List<List<Literal>> result = new LinkedList();
        if(this.isBase() || this.isNegated()){
            List<Literal> list = new LinkedList();
            list.add(new OrdinaryLiteral(this));
            result.add(list);
        }else {
            for(List<Literal> literalsList: getDefinitionRulesWhenCalled(forbiddenTerms)){
                List<List<Literal>> resultForOneDerivationRule = new LinkedList();
                resultForOneDerivationRule.add(new LinkedList());
                for(Literal literal: literalsList){
                    if(literal instanceof BuiltInLiteral){
                        for(List<Literal> list: resultForOneDerivationRule){
                            list.add(new BuiltInLiteral((BuiltInLiteral) literal));
                        }
                    }
                    else resultForOneDerivationRule = cartesianProduct(resultForOneDerivationRule, ((OrdinaryLiteral) literal).getUnfoldedLiteral(forbiddenTerms));
                }
                result.addAll(resultForOneDerivationRule);
            }
        }
        return result;
    }

    private List<List<Literal>> cartesianProduct(List<List<Literal>> list1, List<List<Literal>> list2) {
        List<List<Literal>> result = new LinkedList();
        for(List<Literal> list11: list1){
            for(List<Literal> list22: list2){
                List<Literal> list1122 = new LinkedList();
                list1122.addAll(list11);
                list1122.addAll(list22);
                result.add(list1122);
            }
        }
        return result;
    }

    @Override
    public Literal getLiteralWithVariableSuffix(String suffix) {
        return new OrdinaryLiteral(this.atom.getAtomWithVariableSuffix(suffix));
    }

    @Override
    public List<String> getTermNamesList() {
        return this.atom.getTermsNamesList();
    }

    /**
     *
     * @return a copied list containing a copy of the terms of this
     */
    public List<Term> getTermsCopied() {
        return this.atom.getTermsCopied();
    }

    /**
     *
     * @return the number of derivation rules its predicate has
     */
    public int getNumberOfDerivationRules() {
        return this.getAtom().getNumberOfDefinitionRules();
    }
}
