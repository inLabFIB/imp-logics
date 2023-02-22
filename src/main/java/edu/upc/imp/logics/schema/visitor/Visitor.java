package edu.upc.imp.logics.schema.visitor;

import edu.upc.imp.logics.schema.*;

public interface Visitor<T, R> {

    T visitAtom(Atom atom, R context);

    T visitDerivationRule(DerivationRule derivationRule, R context);

    T visitLogicConstraint(LogicConstraint logicConstraint, R context);

    T visitOrdinaryLiteral(OrdinaryLiteral ordinaryLiteral, R context);

    T visitBuiltInLiteral(BuiltInLiteral builtInLiteral, R context);

}
