package edu.upc.imp.old.utils;

import edu.upc.imp.old.logicschema.Atom;
import edu.upc.imp.old.logicschema.Predicate;
import edu.upc.imp.old.logicschema.PredicateImpl;
import edu.upc.imp.old.logicschema.Term;

import java.util.List;

public class AtomMother {
    public static Atom buildAtom(String predicateName, List<String> termNames) {
        Predicate predicate = new PredicateImpl(predicateName, termNames.size());
        List<Term> termsList = termNames.stream().map(Term::new).toList();
        return new Atom(predicate, termsList);
    }
}
