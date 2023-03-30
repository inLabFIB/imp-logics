package edu.upc.fib.inlab.imp.kse.logics.schema.mothers;


import edu.upc.fib.inlab.imp.kse.logics.schema.*;

import java.util.LinkedList;
import java.util.List;

public class AtomMother {
    public static Atom createAtomWithVariableNames(String predicateName, List<String> variableNames) {
        List<Term> termsList = new LinkedList<>(variableNames.stream().map(Variable::new).toList());
        return createAtom(predicateName, termsList);
    }

    public static Atom createAtom(String predicateName, List<Term> terms) {
        Predicate predicate = new MutablePredicate(predicateName, terms.size());
        return new Atom(predicate, terms);
    }

    public static Atom createAtom(String predicateName, String... termNames) {
        Predicate predicate = new MutablePredicate(predicateName, termNames.length);
        return new Atom(predicate, TermMother.createTerms(termNames));
    }

    public static Atom createAtom(LogicSchema logicSchema, String predicateName, String... termNames) {
        Predicate predicateFromSchema = logicSchema.getPredicateByName(predicateName);
        return new Atom(predicateFromSchema, TermMother.createTerms(termNames));
    }

}
