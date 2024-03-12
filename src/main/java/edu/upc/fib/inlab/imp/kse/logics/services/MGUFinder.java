package edu.upc.fib.inlab.imp.kse.logics.services;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

import java.util.*;

/**
 * Class with static functions to find Most General Unifier between
 * literals, and atoms.
 * <p>
 * In the current version, two comparison built-in literals such as
 * "2 < a" and "a > 2" are considered to be non-unifiable since
 * they are using different comparison built-in.
 *
 */
public class MGUFinder {

    public static Optional<Substitution> getLiteralsMGU(Literal... literals) {
        return getLiteralsMGU(Arrays.stream(literals).toList());
    }

    public static Optional<Substitution> getLiteralsMGU(Collection<Literal> literals) {
        if(literals.isEmpty()) return Optional.of(new Substitution());
        else if (literals.size()==1){
            Literal literal = literals.iterator().next();
            return Optional.of(new Substitution(literal.getTerms(), literal.getTerms()));
        }
        else {
            Iterator<Literal> literalIterator = literals.iterator();
            Literal lit1 = literalIterator.next();
            Literal lit2 = literalIterator.next();
            Optional<Substitution> result = getMGU(lit1, lit2);

            while (literalIterator.hasNext() && result.isPresent()) {
                Literal otherLiteral = literalIterator.next();
                if (!schemaIsUnifiable(lit1, otherLiteral)) return Optional.empty();
                addTermsIfNotMapped(result.get(), otherLiteral.getUsedVariables());
                result = getMGURecursive(result.get(), lit1.getTerms(), otherLiteral.getTerms());
            }
            return result;
        }
    }

    private static void addTermsIfNotMapped(Substitution substitution, Set<Variable> otherVars) {
        for(Variable var: otherVars){
            if(substitution.getTerm(var).isEmpty()){
                substitution.addMapping(var, var);
            }
        }
    }

    private static Optional<Substitution> getMGU(Literal lit1, Literal lit2) {
        if(!schemaIsUnifiable(lit1, lit2)) return Optional.empty();

        Set<Variable> allVariables = lit1.getUsedVariables();
        allVariables.addAll(lit2.getUsedVariables());

        Substitution substitution = new Substitution();
        for(Variable var: allVariables){
            substitution.addMapping(var, var);
        }

        return getMGURecursive(substitution, lit1.getTerms(), lit2.getTerms());
    }

    public static boolean areLiteralsUnifiable(Collection<Literal> literals) {
        return getLiteralsMGU(literals).isPresent();
    }

    public static boolean areLiteralsUnifiable(Literal... literals) {
        return getLiteralsMGU(literals).isPresent();
    }

    /**
     *
     * @param lit1 not null
     * @param lit2 not null
     * @return whether the schema part of the literals is unifiable, that is, whether they share the same
     * predicate (and polarity), or they share the same operator.
     */
    private static boolean schemaIsUnifiable(Literal lit1, Literal lit2) {
        if(lit1 instanceof OrdinaryLiteral oLit1 && lit2 instanceof OrdinaryLiteral oLit2){
            return oLit1.getPredicate().equals(oLit2.getPredicate()) && oLit1.isPositive() == oLit2.isPositive();
        }
        else if (lit1 instanceof BooleanBuiltInLiteral bil1 && lit2 instanceof BooleanBuiltInLiteral bil2){
            return bil1.isTrue() == bil2.isTrue();
        }
        else if (lit1 instanceof ComparisonBuiltInLiteral comp1 && lit2 instanceof ComparisonBuiltInLiteral comp2){
            return comp1.getOperator().equals(comp2.getOperator());
        }
        else if (lit1 instanceof CustomBuiltInLiteral cust1 && lit2 instanceof CustomBuiltInLiteral cust2){
            return cust1.getOperationName().equals(cust2.getOperationName()) && cust1.getTerms().size() == cust2.getTerms().size();
        }
        return false;
    }

    //Methods with atom

    /**
     *
     * @param atoms not null
     * @return a Maximum General Unifier substitution between actual and atom2, if it exists
     */
    public static Optional<Substitution> getAtomsMGU(Collection<Atom> atoms) {
        if(atoms.isEmpty()) return Optional.of(new Substitution());
        else if (atoms.size()==1){
            Atom atom = atoms.iterator().next();
            return Optional.of(new Substitution(atom.getTerms(), atom.getTerms()));
        }
        else {
            Iterator<Atom> atomIterator = atoms.iterator();
            Atom atom1 = atomIterator.next();
            Atom atom2 = atomIterator.next();
            Predicate predicate = atom1.getPredicate();
            Optional<Substitution> result = getMGU(atom1, atom2);

            while (atomIterator.hasNext() && result.isPresent()) {
                Atom otherAtom = atomIterator.next();
                if (!predicate.equals(otherAtom.getPredicate())) return Optional.empty();
                addTermsIfNotMapped(result.get(), otherAtom.getVariables());
                result = getMGURecursive(result.get(), atom1.getTerms(), otherAtom.getTerms());
            }
            return result;
        }
    }

    public static Optional<Substitution> getAtomsMGU(Atom... atoms) {
        return getAtomsMGU(Arrays.stream(atoms).toList());
    }

    public static boolean areAtomsUnifiable(List<Atom> atoms){
        return getAtomsMGU(atoms).isPresent();
    }

    public static boolean areAtomsUnifiable(Atom... atoms){
        return getAtomsMGU(atoms).isPresent();
    }

    /**
     *
     * @param atom1 not null
     * @param atom2 not null
     * @return a Maximum General Unifier substitution between actual and atom2, if it exists
     */
    private static Optional<Substitution> getMGU(Atom atom1, Atom atom2) {
        if(!atom1.getPredicate().equals(atom2.getPredicate())) return Optional.empty();

        Set<Variable> allVariables = atom1.getVariables();
        allVariables.addAll(atom2.getVariables());

        Substitution substitution = new Substitution();
        for(Variable var: allVariables){
            substitution.addMapping(var, var);
        }

        return getMGURecursive(substitution, atom1.getTerms(), atom2.getTerms());
    }

    /**
     *
     * @param subs not null, might be empty, maps every variable appearing in terms1 and terms2
     * @param terms1 not null, might be empty
     * @param terms2 not null, might be empty, has same size as terms1
     * @return a new substitution, that unifies terms1 with terms2 and contains all the maps of subs
     * (where such the image of such maps might change due to the unification of terms1 with terms2),
     * if exists
     */
    private static Optional<Substitution> getMGURecursive(Substitution subs, ImmutableTermList terms1, ImmutableTermList terms2){
        if(terms1.isEmpty()) return Optional.of(subs);

        Term term1 = terms1.get(0);
        Term term2 = terms2.get(0);
        Optional<Substitution> newSubs = unifyTerms(subs, term1, term2);
        if (newSubs.isPresent()){
            ImmutableTermList remainingTerms1 = terms1.subList(1, terms1.size());
            ImmutableTermList remainingTerms2 = terms2.subList(1, terms2.size());
            return getMGURecursive(newSubs.get(), remainingTerms1, remainingTerms2);
        }
        else return Optional.empty();
    }



    /**
     *
     * @param subs not null, contains a substitution for term1 and term2 if they are variables
     * @param term1 not null
     * @param term2 not null
     * @return a substitution that unifies term1 with term2, and contains all the mappings from subs, applying
     * the corresponding substitution to unify term1 with term2.
     */
    private static Optional<Substitution> unifyTerms(Substitution subs, Term term1, Term term2) {
        if (bothAreMappedToSameConstant(subs, term1, term2)) return Optional.of(subs);
        if(bothAreMappedToDifferentConstants(subs, term1, term2)) return Optional.empty();
        else if(firstIsMappedToVarAndSecondToConstant(subs, term1, term2)){
            return Optional.of(newSubstitutionFromFirstToSecondImage(subs, (Variable) term1, term2));
        }
        else if(firstIsMappedToVarAndSecondToConstant(subs, term2, term1)){
            return Optional.of(newSubstitutionFromFirstToSecondImage(subs, (Variable) term2, term1));
        }
        else {
            //Both are mapped to variables
            return Optional.of(newSubstitutionFromFirstToSecondImage(subs, (Variable) term1, term2));
        }
    }

    private static Substitution newSubstitutionFromFirstToSecondImage(Substitution oldSubs, Variable term1, Term term2) {
        Term image1 = term1.applySubstitution(oldSubs);
        Term image2 = term2.applySubstitution(oldSubs);
        Substitution newSubs = new Substitution();
        for(Variable variable: oldSubs.getUsedVariables()) {
            if(variable.equals(term1)){
                newSubs.addMapping(term1, image2);
            }
            else if(variable.equals(image1)){
                newSubs.addMapping(variable, image2);
            }
            else {
                Term imageInOldSubs = variable.applySubstitution(oldSubs);
                if(imageInOldSubs.equals(term1) ||imageInOldSubs.equals(image1)){
                    newSubs.addMapping(variable, image2);
                }
                else newSubs.addMapping(variable, imageInOldSubs);
            }
        }
        return newSubs;
    }

    private static boolean firstIsMappedToVarAndSecondToConstant(Substitution subs, Term term1, Term term2) {
        Term image1 = term1.applySubstitution(subs);
        Term image2 = term2.applySubstitution(subs);
        return image1.isVariable() && image2.isConstant();
    }

    private static boolean bothAreMappedToSameConstant(Substitution subs, Term term1, Term term2) {
        Term image1 = term1.applySubstitution(subs);
        Term image2 = term2.applySubstitution(subs);
        return image1.isConstant() && image2.isConstant() && image1.getName().equals(image2.getName());
    }

    private static boolean bothAreMappedToDifferentConstants(Substitution subs, Term term1, Term term2) {
        Term image1 = term1.applySubstitution(subs);
        Term image2 = term2.applySubstitution(subs);
        return image1.isConstant() && image2.isConstant() && !image1.getName().equals(image2.getName());
    }

}
