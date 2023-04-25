package edu.upc.fib.inlab.imp.kse.logics.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.schema.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;

import java.util.HashSet;
import java.util.Set;

record SubstitutionResult(Substitution substitution, Set<ComparisonBuiltInLiteral> equalityLiterals) {
    public SubstitutionResult union(SubstitutionResult substitutionResult) {
        Substitution unionSubstitution = substitution.union(substitutionResult.substitution());
        Set<ComparisonBuiltInLiteral> unionEqualityLiterals = new HashSet<>();
        unionEqualityLiterals.addAll(equalityLiterals);
        unionEqualityLiterals.addAll(substitutionResult.equalityLiterals());
        return new SubstitutionResult(unionSubstitution, unionEqualityLiterals);
    }
}
