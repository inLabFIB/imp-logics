package edu.upc.imp.utils;

import edu.upc.imp.logicschema.Atom;
import edu.upc.imp.logicschema.Predicate;
import edu.upc.imp.logicschema.PredicateImpl;
import edu.upc.imp.logicschema.Term;

import java.util.List;

public class AtomMother {
    public static Atom buildAtom(String predicateName, List<String> termNames) {
        Predicate predicate = new PredicateImpl(predicateName, termNames.size());
        List<Term> termsList = termNames.stream().map(Term::new).toList();
        return new Atom(predicate, termsList);
    }
}
