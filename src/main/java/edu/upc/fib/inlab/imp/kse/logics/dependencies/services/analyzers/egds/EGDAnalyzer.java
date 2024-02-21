package edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.EGD;
import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.*;
import java.util.stream.Collectors;

public class EGDAnalyzer {

    public EGDAnalysis analyze(List<EGD> egdList) {
        List<FunctionalDependencyWithEGDs> functionalDependenciesEGDs = new ArrayList<>();
        List<EGD> nonFunctionalDependenciesEGDs = new ArrayList<>();

        for (EGD egd : egdList) {
            if (isFunctionalDependency(egd)) {
                Optional<FunctionalDependencyWithEGDs> optionalFD = getFunctionalDependencyWithSameKeyAndPredicate(egd, functionalDependenciesEGDs);
                if (optionalFD.isPresent()) {
                    FunctionalDependencyWithEGDs fd = optionalFD.get();
                    functionalDependenciesEGDs.remove(fd);
                    functionalDependenciesEGDs.add(createNewFunctionalDependency(fd, egd));
                } else {
                    functionalDependenciesEGDs.add(createNewFunctionalDependency(egd));
                }
            } else nonFunctionalDependenciesEGDs.add(egd);
        }

        return new EGDAnalysis(functionalDependenciesEGDs, nonFunctionalDependenciesEGDs);
    }

    private FunctionalDependencyWithEGDs createNewFunctionalDependency(EGD egd) {
        Set<Integer> keyPositions = getKeyPositions(egd);
        Integer determinedPosition = getDeterminedPosition(egd);
        Predicate affectedPredicate = getAffectedPredicate(egd);

        return new FunctionalDependencyWithEGDs(List.of(egd), new FunctionalDependency(affectedPredicate, keyPositions, Set.of(determinedPosition)));
    }

    /**
     * @param fd  not null
     * @param egd affects the same predicate as fd, and has the same key positions
     * @return a new functional dependency that adds the determined positions of the egd to fd
     */
    private FunctionalDependencyWithEGDs createNewFunctionalDependency(FunctionalDependencyWithEGDs fd, EGD egd) {
        List<EGD> newEGDList = new ArrayList<>(fd.egdList());
        newEGDList.add(egd);

        FunctionalDependency currentFD = fd.functionalDependency();
        Set<Integer> newDeterminedPositions = new LinkedHashSet<>(currentFD.determinedPositions());
        newDeterminedPositions.add(getDeterminedPosition(egd));

        return new FunctionalDependencyWithEGDs(newEGDList,
                new FunctionalDependency(currentFD.predicate(), currentFD.keyPositions(), newDeterminedPositions));
    }

    /**
     * @param egd that defines a functional dependency
     * @return the positions determined by this dependency
     */
    private Integer getDeterminedPosition(EGD egd) {
        Term leftTerm = egd.getHead().getLeftTerm();
        Set<LiteralPosition> literalPositionWithVariable = egd.getBody().getLiteralPositionWithVariable((Variable) leftTerm);
        return literalPositionWithVariable.iterator().next().position();
    }

    /**
     * @param egd                        that defines a functional dependency
     * @param functionalDependenciesEGDs not null
     * @return the functionalDependency with the same Key and Predicate already contained in functionalDependenciesEGDs
     */
    private Optional<FunctionalDependencyWithEGDs> getFunctionalDependencyWithSameKeyAndPredicate(EGD egd, List<FunctionalDependencyWithEGDs> functionalDependenciesEGDs) {
        Predicate predicate = getAffectedPredicate(egd);
        Set<Integer> keyPositions = getKeyPositions(egd);
        return functionalDependenciesEGDs.stream()
                .filter(fd -> areSameKeyAndPredicate(fd, predicate, keyPositions))
                .findFirst();
    }

    private static boolean areSameKeyAndPredicate(FunctionalDependencyWithEGDs fd, Predicate predicate, Set<Integer> keyPositions) {
        return fd.getPredicateName().equals(predicate.getName()) && fd.functionalDependency().keyPositions().equals(keyPositions);
    }

    /**
     * @param egd that defines a functional dependency
     * @return the positions of the affected predicate that are key positions
     */
    private Set<Integer> getKeyPositions(EGD egd) {
        Set<Integer> keyPositions = new LinkedHashSet<>();

        Literal lit1 = egd.getBody().get(0);
        Literal lit2 = egd.getBody().get(1);

        for (int position = 0; position < lit1.getArity(); ++position) {
            Term term1 = lit1.getTerms().get(position);
            Term term2 = lit2.getTerms().get(position);
            if (term1.equals(term2)) {
                keyPositions.add(position);
            }
        }

        return keyPositions;
    }

    /**
     * @param egd that defines a functional dependency
     * @return the affected predicate of the functional dependency
     */
    private Predicate getAffectedPredicate(EGD egd) {
        return ((OrdinaryLiteral) egd.getBody().get(0)).getPredicate();
    }

    private boolean isFunctionalDependency(EGD egd) {
        if (egd.getBody().size() != 2) return false;

        return egd.getBody().get(0) instanceof OrdinaryLiteral oLit1 &&
                egd.getBody().get(1) instanceof OrdinaryLiteral oLit2 &&
                hasSamePredicate(oLit1, oLit2) &&
                shareSomeVariable(oLit1, oLit2) &&
                allTermsAreDifferentVariables(oLit1) &&
                allTermsAreDifferentVariables(oLit2) &&
                equatesSamePositionOfDifferentLiterals(egd);
    }

    private boolean allTermsAreDifferentVariables(OrdinaryLiteral oLit) {
        return oLit.getTerms().stream()
                .filter(Variable.class::isInstance)
                .collect(Collectors.toSet())
                .size() == oLit.getArity();
    }

    /**
     * @param egd where no literal contain a constant, nor a variable repeated in the very same literal.
     * @return whether the egd equates the same predicate position of two different literals
     */
    private boolean equatesSamePositionOfDifferentLiterals(EGD egd) {
        Term term1 = egd.getHead().getLeftTerm();
        Term term2 = egd.getHead().getRightTerm();
        if (term1 instanceof Variable var1 && term2 instanceof Variable var2) {
            Optional<Integer> position1 = getPositionOfVariable(egd, var1);
            Optional<Integer> position2 = getPositionOfVariable(egd, var2);
            Optional<Literal> literal1 = getLiteralOfVariable(egd, var1);
            Optional<Literal> literal2 = getLiteralOfVariable(egd, var2);
            return position1.isPresent() && position2.isPresent() &&
                    areSamePosition(position1.get(), position2.get()) &&
                    literal1.isPresent() && literal2.isPresent() &&
                    areDifferentReference(literal1.get(), literal2.get());
        } else return false;
    }

    private Optional<Integer> getPositionOfVariable(EGD egd, Variable var1) {
        return egd.getBody().getPredicatePositionsWithVar(var1).stream().map(PredicatePosition::position).findFirst();
    }

    private Optional<Literal> getLiteralOfVariable(EGD egd, Variable var1) {
        return egd.getBody().getLiteralPositionWithVariable(var1).stream().map(LiteralPosition::literal).findFirst();
    }

    private static boolean areSamePosition(Integer pos1, Integer pos2) {
        return pos1.equals(pos2);
    }

    private static boolean areDifferentReference(Object o1, Object o2) {
        return o1 != o2;
    }

    private boolean shareSomeVariable(OrdinaryLiteral oLit1, OrdinaryLiteral oLit2) {
        return oLit1.getTerms().stream().anyMatch(t -> t instanceof Variable && oLit2.getTerms().contains(t));
    }

    private boolean hasSamePredicate(OrdinaryLiteral oLit1, OrdinaryLiteral oLit2) {
        return oLit1.getPredicateName().equals(oLit2.getPredicateName());
    }
}
