package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.Term;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Class that partitions a set of equalities.
 * <p>
 * The partition groups all the equality built-in literals that share some term, so that, every term
 * appears in one of such groups, and only in one.
 */
class PartitionOfEqualityLiterals {
    private final Set<EqualityLiteralsGroup> equalityLiterals;

    public PartitionOfEqualityLiterals(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        this.equalityLiterals = new LinkedHashSet<>();
        addEqualities(equalityLiterals);
    }

    private void addEqualities(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        for (ComparisonBuiltInLiteral equalityLiteral : equalityLiterals) {
            this.addEquality(equalityLiteral);
        }
    }

    private void addEquality(ComparisonBuiltInLiteral equalityLiteral) {
        Optional<EqualityLiteralsGroup> groupForLeftTermOpt = getEqualityLiteralsContainingTerm(equalityLiteral.getLeftTerm());
        Optional<EqualityLiteralsGroup> groupForRightTermOpt = getEqualityLiteralsContainingTerm(equalityLiteral.getRightTerm());
        if (groupForLeftTermOpt.isPresent() && groupForRightTermOpt.isPresent()) {
            EqualityLiteralsGroup groupedTermsForLeft = groupForLeftTermOpt.get();
            EqualityLiteralsGroup groupedTermsForRight = groupForRightTermOpt.get();
            if (groupedTermsForRight == groupedTermsForLeft) {
                //If it equals two terms of the same group, add the equality to that group
                groupedTermsForLeft.addEquality(equalityLiteral);
            } else {
                //If it equals two terms of different groups, join those groups and add the equality to that group
                EqualityLiteralsGroup mergedGroup = this.mergeEqualityLiterals(groupedTermsForLeft, groupedTermsForRight);
                mergedGroup.addEquality(equalityLiteral);
            }
        } else if (groupForLeftTermOpt.isPresent()) {
            //If it equals a term of a group, with a new term, add the new term and the equality to the group
            groupForLeftTermOpt.get().addTerm(equalityLiteral.getRightTerm());
            groupForLeftTermOpt.get().addEquality(equalityLiteral);
        } else if (groupForRightTermOpt.isPresent()) {
            //If it equals a term of a group, with a new term, add the new term and the equality to the group
            groupForRightTermOpt.get().addTerm(equalityLiteral.getLeftTerm());
            groupForRightTermOpt.get().addEquality(equalityLiteral);
        } else {
            //If it equals two new terms, create a new group with those two terms, and put the equality in the group
            EqualityLiteralsGroup newGroup = new EqualityLiteralsGroup(equalityLiteral);
            this.equalityLiterals.add(newGroup);
        }
    }

    private Optional<EqualityLiteralsGroup> getEqualityLiteralsContainingTerm(Term term) {
        return this.equalityLiterals.stream().filter(g -> g.containsTerm(term)).findFirst();
    }

    private EqualityLiteralsGroup mergeEqualityLiterals(EqualityLiteralsGroup firstEqualityLiterals, EqualityLiteralsGroup secondEqualityLiterals) {
        equalityLiterals.remove(firstEqualityLiterals);
        equalityLiterals.remove(secondEqualityLiterals);
        EqualityLiteralsGroup mergedGroup = firstEqualityLiterals.union(secondEqualityLiterals);
        equalityLiterals.add(mergedGroup);
        return mergedGroup;
    }

    public SubstitutionResult computeSubstitutionResult() {
        List<SubstitutionResult> substitutionResultSet = equalityLiterals.stream()
                .map(EqualityLiteralsGroup::computeSubstitutionResult)
                .toList();

        if (substitutionResultSet.isEmpty()) {
            return new SubstitutionResult(new Substitution(), Set.of());
        } else if (substitutionResultSet.size() == 1) {
            return substitutionResultSet.get(0);
        } else {
            Optional<SubstitutionResult> reduce = substitutionResultSet.stream()
                    .reduce(SubstitutionResult::union);
            return reduce.orElseThrow();
        }
    }
}
