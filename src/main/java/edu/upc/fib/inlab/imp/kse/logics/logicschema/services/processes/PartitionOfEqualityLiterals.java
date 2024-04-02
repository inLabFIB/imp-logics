package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Term;

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

    PartitionOfEqualityLiterals(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        this.equalityLiterals = new LinkedHashSet<>();
        addEqualities(equalityLiterals);
    }

    private void addEqualities(Set<ComparisonBuiltInLiteral> equalityLiterals) {
        for (ComparisonBuiltInLiteral equalityLiteral : equalityLiterals) {
            this.addEquality(equalityLiteral);
        }
    }

    private void addEquality(ComparisonBuiltInLiteral equalityLiteral) {
        EqualityLiteralsGroup targetEqualityLiteralGroup = computeTargetEqualityLiteralGroupFor(equalityLiteral);
        targetEqualityLiteralGroup.addEquality(equalityLiteral);
    }

    private EqualityLiteralsGroup computeTargetEqualityLiteralGroupFor(ComparisonBuiltInLiteral equalityLiteral) {
        Optional<EqualityLiteralsGroup> groupForLeftTermOpt = getEqualityLiteralsContainingTerm(equalityLiteral.getLeftTerm());
        Optional<EqualityLiteralsGroup> groupForRightTermOpt = getEqualityLiteralsContainingTerm(equalityLiteral.getRightTerm());
        if (groupForLeftTermOpt.isPresent() && groupForRightTermOpt.isPresent()) {
            EqualityLiteralsGroup groupedForLeftTerm = groupForLeftTermOpt.get();
            EqualityLiteralsGroup groupedForRightTerm = groupForRightTermOpt.get();
            if (groupedForLeftTerm == groupedForRightTerm) {
                return groupedForLeftTerm;
            } else {
                return mergeEqualityLiteralGroups(groupedForLeftTerm, groupedForRightTerm);
            }
        } else {
            return groupForLeftTermOpt
                    .or(() -> groupForRightTermOpt)
                    .orElseGet(this::createNewEqualityLiteralGroup);
        }
    }

    private Optional<EqualityLiteralsGroup> getEqualityLiteralsContainingTerm(Term term) {
        return equalityLiterals.stream().filter(g -> g.containsTerm(term)).findFirst();
    }

    private EqualityLiteralsGroup mergeEqualityLiteralGroups(EqualityLiteralsGroup firstEqualityLiterals, EqualityLiteralsGroup secondEqualityLiterals) {
        equalityLiterals.remove(firstEqualityLiterals);
        equalityLiterals.remove(secondEqualityLiterals);
        EqualityLiteralsGroup mergedGroup = firstEqualityLiterals.union(secondEqualityLiterals);
        equalityLiterals.add(mergedGroup);
        return mergedGroup;
    }

    private EqualityLiteralsGroup createNewEqualityLiteralGroup() {
        EqualityLiteralsGroup newGroup = new EqualityLiteralsGroup();
        equalityLiterals.add(newGroup);
        return newGroup;
    }

    SubstitutionForEqualities computeSubstitutionResult() {
        List<SubstitutionForEqualities> substitutionForEqualitiesSet = equalityLiterals.stream()
                .map(EqualityLiteralsGroup::computeSubstitutionResult)
                .toList();

        return substitutionForEqualitiesSet.stream()
                .reduce(SubstitutionForEqualities::union)
                .orElseGet(SubstitutionForEqualities::empty);

    }
}
