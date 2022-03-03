package edu.upc.mpi.logicschema;

import edu.upc.mpi.logicschema.*;

import java.util.LinkedList;

/**
 *
 */
public class LogicSchemaTestHelper {
     public Atom getAtom(LogicSchema ls, String predicateName, String[] terms){
        Predicate p = ls.getPredicate(predicateName);
        if(p == null){
            p = new PredicateImpl(predicateName, terms.length);
            ls.addPredicate(p);
        }
        
        LinkedList<Term> termsList = new LinkedList();
        for(String term: terms){
            termsList.add(new Term(term));
        }
        
        return new Atom(p, termsList);
    }
    
    public OrdinaryLiteral getOrdinaryLiteral(LogicSchema ls, String predicateName, String[] terms){
        return this.getOrdinaryLiteral(ls, predicateName, terms, true);
    }
    
    public OrdinaryLiteral getOrdinaryLiteral(LogicSchema ls, String predicateName, String[] terms, boolean truth){
        Atom atom = this.getAtom(ls, predicateName, terms);
        return new OrdinaryLiteral(atom, truth);
    }
}
