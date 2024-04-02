package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.processes.utils;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.BuiltInLiteral;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.OrdinaryLiteral;

import java.util.Comparator;

public class LiteralComparator implements Comparator<Literal> {
    @Override
    public int compare(Literal l1, Literal l2) {
        if (l1 instanceof OrdinaryLiteral ol1 && l2 instanceof OrdinaryLiteral ol2) {
            if (ol1.isPositive() && ol2.isNegative()) return -1;
            else if (ol1.isNegative() && ol2.isPositive()) return 1;
        } else if (l1 instanceof OrdinaryLiteral && l2 instanceof BuiltInLiteral) {
            return -1;
        } else if (l1 instanceof BuiltInLiteral && l2 instanceof OrdinaryLiteral) {
            return 1;
        }
        return 0;
    }
}
