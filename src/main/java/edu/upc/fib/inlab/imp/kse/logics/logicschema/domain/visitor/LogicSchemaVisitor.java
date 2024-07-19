package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;

/**
 * Visitor for the main classes of the logic schema domain.
 *
 * @param <T> return type for visit methods.
 */
public interface LogicSchemaVisitor<T> {

    T visit(LogicSchema logicSchema);

    T visit(LogicConstraint constraint);

    T visit(DerivationRule derivationRule);

    T visit(ConstraintID constraintID);

    T visit(ImmutableLiteralsList literals);

    T visit(OrdinaryLiteral ordinaryLiteral);

    T visit(ComparisonBuiltInLiteral comparisonBuiltInLiteral);

    T visit(BooleanBuiltInLiteral comparisonBuiltInLiteral);

    T visit(CustomBuiltInLiteral customBuiltInLiteral);

    T visit(Atom atom);

    T visit(ComparisonOperator comparisonOperator);

    T visit(Predicate predicate);

    T visit(ImmutableTermList terms);

    T visit(Variable variable);

    T visit(Constant constant);

    T visit(ImmutableAtomList atoms);
}
