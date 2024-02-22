package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;
import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;

import java.util.*;

public class NonConflictingFDsAnalyzer {

    /**
     * @param tgd not null
     * @param fd  not null
     * @return whether the given TGD is conflicting with the given fd
     */
    public boolean isConflicting(TGD tgd, FunctionalDependency fd) {
        if (Objects.isNull(tgd)) throw new IllegalArgumentException("TGD cannot be null");
        if (Objects.isNull(fd)) throw new IllegalArgumentException("Functional Dependency cannot be null");
        Set<Variable> universalVariables = tgd.getUniversalVariables();

        if (affectsSamePredicate(tgd, fd) && containsRepeatedExistentialVariable(tgd)) return true;

        for (Atom headAtom : tgd.getHead()) {
            if (isConflicting(headAtom, universalVariables, fd)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param tgds not null
     * @param fds  not null
     * @return whether some TGD of the given list is conflicting with some FD of the given list
     */
    public boolean isConflicting(List<TGD> tgds, List<FunctionalDependency> fds) {
        if (Objects.isNull(tgds)) throw new IllegalArgumentException("TGDs cannot be null");
        if (Objects.isNull(fds)) throw new IllegalArgumentException("Functional Dependency cannot be null");

        for (TGD tgd : tgds) {
            for (FunctionalDependency fd : fds) {
                if (isConflicting(tgd, fd)) return true;
            }
        }

        return false;
    }

    private boolean containsRepeatedExistentialVariable(TGD tgd) {
        Set<Variable> existentialVars = tgd.getExistentialVariables();
        for (Variable existVar : existentialVars) {
            if (appearsMoreThanOnce(existVar, tgd)) {
                return true;
            }
        }
        return false;
    }

    private boolean appearsMoreThanOnce(Variable existVar, TGD tgd) {
        int counter = 0;
        for (Atom atomHead : tgd.getHead()) {
            if (atomHead.getTerms().contains(existVar)) {
                counter++;
            }
        }
        return counter > 1;
    }

    private boolean affectsSamePredicate(TGD tgd, FunctionalDependency fd) {
        return tgd.getHead().stream()
                .map(Atom::getPredicate)
                .anyMatch(p -> fd.predicate().equals(p));
    }


    private boolean isConflicting(Atom headAtom, Set<Variable> universalVariables, FunctionalDependency fd) {
        if (!headAtom.getPredicate().getName().equals(fd.getPredicateName())) return false;
        if (hasRepeatedExistentialVariable(headAtom, universalVariables)) return true;
        if (containsConstant(headAtom)) return true;

        Set<Integer> universalPositions = getPositionsOfUniversalVariables(headAtom, universalVariables);
        return universalPositions.stream().anyMatch(universalPos -> !fd.keyPositions().contains(universalPos));
    }

    private boolean containsConstant(Atom headAtom) {
        return headAtom.getTerms().stream().anyMatch(Term::isConstant);
    }

    private Set<Integer> getPositionsOfUniversalVariables(Atom headAtom, Set<Variable> universalVariables) {
        Set<Integer> result = new LinkedHashSet<>();
        for (int position = 0; position < headAtom.getTerms().size(); ++position) {
            Term term = headAtom.getTerms().get(position);
            if (term instanceof Variable variable && universalVariables.contains(variable)) {
                result.add(position);
            }
        }
        return result;
    }

    private boolean hasRepeatedExistentialVariable(Atom headAtom, Set<Variable> universalVariables) {
        List<Variable> existentialVarsList = headAtom.getTerms().stream()
                .filter(Variable.class::isInstance)
                .map(Variable.class::cast)
                .filter(variable -> !universalVariables.contains(variable))
                .toList();

        Set<Variable> existentialVarsSet = new HashSet<>(existentialVarsList);
        return existentialVarsSet.size() < existentialVarsList.size();
    }
}
