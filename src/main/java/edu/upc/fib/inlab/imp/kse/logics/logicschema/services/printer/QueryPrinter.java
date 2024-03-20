package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Query;

public class QueryPrinter {

    private LogicSchemaPrinter literalsPrinter = new LogicSchemaPrinter();

    public String print(Query query) {
        String headTermsString = literalsPrinter.visit(query.getHeadTerms());
        String bodyString = literalsPrinter.visit(query.getBody());
        return "(" + headTermsString + ") :- " + bodyString;
    }
}
