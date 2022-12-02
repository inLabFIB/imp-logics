package edu.upc.mpi.utils;

import edu.upc.mpi.logicschema.*;
import edu.upc.mpi.parser.LogicSchemaParser;

import java.util.LinkedList;
import java.util.List;

public class LogicSchemaTestHelper {
     public Atom getAtom(LogicSchema ls, String predicateName, String[] terms){
        Predicate p = ls.getPredicate(predicateName);
        if(p == null){
            p = new PredicateImpl(predicateName, terms.length);
            ls.addPredicate(p);
        }
        
        LinkedList<Term> termsList = new LinkedList<>();
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

    protected LogicSchema createLogicSchemaWithConstraints(String constraints) {
        LogicSchemaParser parser = new LogicSchemaParser(constraints+"\n");
        parser.parse();
        return parser.getLogicSchema();
    }

    protected LogicConstraint createBasicLogicConstraint(LogicSchema schema) {
        List<Literal> body = new LinkedList<>();
        body.add(this.getOrdinaryLiteral(schema, "P", new String[]{"X", "Y"}));
        body.add(this.getOrdinaryLiteral(schema, "Q", new String[]{"X", "Y"}, false));
        body.add(new BuiltInLiteral(new Term("X"), new Term("Y"), "<>"));
        return new LogicConstraint(10, body);
    }

    protected LogicSchema createLogicSchemaWithPositiveUnfolding() {
        String positiveUnfoldingConstraint =
                "@1 :- P(x)\n" +
                        "P(x) :- A(x)\n" +
                        "P(x) :- B(x)\n";

        LogicSchemaParser parser = new LogicSchemaParser(positiveUnfoldingConstraint);
        parser.parse();
        return parser.getLogicSchema();
    }

    protected LogicSchema createLogicSchemaWithNegativeUnfolding() {
        String negativeUnfoldingConstraint =
                "@1 :- R(x), not(P(x))\n" +
                        "P(x) :- A(x)\n" +
                        "P(x) :- B(x)\n";

        LogicSchemaParser parser = new LogicSchemaParser(negativeUnfoldingConstraint);
        parser.parse();
        return parser.getLogicSchema();
    }
}
