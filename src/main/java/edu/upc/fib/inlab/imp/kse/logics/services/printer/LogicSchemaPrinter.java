package edu.upc.fib.inlab.imp.kse.logics.services.printer;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.stream.Collectors;

public class LogicSchemaPrinter implements LogicSchemaVisitor<String> {
    private static final String CONSTRAINT_ID_PREFIX = "@";
    public static final String NORMAL_CLAUSE_SEPARATOR = ":-";
    public static final String COMMA_SEPARATOR = ",";
    public static final String NOT = "not";

    public String print(LogicSchema logicSchema) {
        return this.visit(logicSchema);
    }

    @Override
    public String visit(LogicSchema logicSchema) {
        StringBuilder resultBuilder = new StringBuilder();
        logicSchema.getAllLogicConstraints().forEach(constraint -> {
            resultBuilder.append(constraint.accept(this));
            resultBuilder.append("\n");
        });
        logicSchema.getAllDerivationRules().forEach(derivationRule -> {
            resultBuilder.append(derivationRule.accept(this));
            resultBuilder.append("\n");
        });
        return resultBuilder.toString();
    }

    @Override
    public String visit(LogicConstraint constraint) {
        ConstraintID constraintID = constraint.getID();
        ImmutableLiteralsList body = constraint.getBody();
        return CONSTRAINT_ID_PREFIX +
                constraintID.accept(this) +
                " " + NORMAL_CLAUSE_SEPARATOR + " " +
                body.accept(this);
    }

    @Override
    public String visit(DerivationRule derivationRule) {
        Atom head = derivationRule.getHead();
        ImmutableLiteralsList body = derivationRule.getBody();
        return head.accept(this) +
                " " + NORMAL_CLAUSE_SEPARATOR + " " +
                body.accept(this);
    }

    @Override
    public String visit(ConstraintID constraintID) {
        return constraintID.id();
    }

    @Override
    public String visit(ImmutableLiteralsList literals) {
        return literals.stream().map(literal ->
                literal.accept(this)
        ).collect(Collectors.joining(COMMA_SEPARATOR + " "));
    }

    @Override
    public String visit(OrdinaryLiteral ordinaryLiteral) {
        Atom atom = ordinaryLiteral.getAtom();
        String atomString = atom.accept(this);
        StringBuilder stringBuilder = new StringBuilder();
        if (ordinaryLiteral.isNegative()) {
            stringBuilder
                    .append(NOT)
                    .append("(")
                    .append(atomString)
                    .append(")");
        } else {
            stringBuilder.append(atomString);
        }
        return stringBuilder.toString();
    }

    @Override
    public String visit(ComparisonBuiltInLiteral comparisonBuiltInLiteral) {
        Term leftTerm = comparisonBuiltInLiteral.getLeftTerm();
        ComparisonOperator operator = comparisonBuiltInLiteral.getOperator();
        Term rightTerm = comparisonBuiltInLiteral.getRightTerm();
        return leftTerm.accept(this) +
                operator.accept(this) +
                rightTerm.accept(this);
    }

    @Override
    public String visit(BooleanBuiltInLiteral booleanBuiltInLiteral) {
        String operationName = booleanBuiltInLiteral.getOperationName();
        ImmutableTermList terms = booleanBuiltInLiteral.getTerms();
        return operationName + "(" + terms.accept(this) + ")";
    }

    @Override
    public String visit(CustomBuiltInLiteral customBuiltInLiteral) {
        String operationName = customBuiltInLiteral.getOperationName();
        ImmutableTermList terms = customBuiltInLiteral.getTerms();
        return operationName + "(" + terms.accept(this) + ")";
    }

    @Override
    public String visit(ComparisonOperator comparisonOperator) {
        return comparisonOperator.getSymbol();
    }

    @Override
    public String visit(Predicate predicate) {
        return predicate.getName();
    }

    @Override
    public String visit(ImmutableTermList terms) {
        return terms.stream().map(term -> term.accept(this))
                .collect(Collectors.joining(COMMA_SEPARATOR + " "));
    }

    @Override
    public String visit(Variable variable) {
        return variable.getName();
    }

    @Override
    public String visit(Constant constant) {
        return constant.getName();
    }

    @Override
    public String visit(Atom atom) {
        Predicate predicate = atom.getPredicate();
        ImmutableTermList terms = atom.getTerms();
        return predicate.accept(this) + "(" + terms.accept(this) + ")";
    }
}
