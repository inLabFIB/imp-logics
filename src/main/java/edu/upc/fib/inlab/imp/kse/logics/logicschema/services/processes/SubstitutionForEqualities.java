package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ComparisonBuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;


record SubstitutionForEqualities(Substitution substitution, Set<ComparisonBuiltInLiteral> equalityLiterals) {

    static SubstitutionForEqualities empty() {
        return new SubstitutionForEqualities(new Substitution(), Collections.emptySet());
    }

    SubstitutionForEqualities union(SubstitutionForEqualities substitutionForEqualities) {
        Substitution unionSubstitution = substitution.union(substitutionForEqualities.substitution());
        Set<ComparisonBuiltInLiteral> unionEqualityLiterals = new LinkedHashSet<>();
        unionEqualityLiterals.addAll(equalityLiterals);
        unionEqualityLiterals.addAll(substitutionForEqualities.equalityLiterals());
        return new SubstitutionForEqualities(unionSubstitution, unionEqualityLiterals);
    }

}
