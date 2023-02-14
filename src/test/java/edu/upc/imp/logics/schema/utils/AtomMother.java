package edu.upc.imp.logics.schema.utils;


import edu.upc.imp.logics.schema.*;

import java.util.LinkedList;
import java.util.List;

public class AtomMother {
    public static Atom createAtomWithVariableNames(String predicateName, List<String> variableNames) {
        List<Term> termsList = new LinkedList<>(variableNames.stream().map(Variable::new).toList());
        return createAtom(predicateName, termsList);
    }

    public static Atom createAtom(String predicateName, List<Term> terms) {
        Predicate predicate = new BasePredicate(predicateName, new Arity(terms.size()));
        return new Atom(predicate, terms);
    }
}
