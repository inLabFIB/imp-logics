package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

import java.util.Optional;

/**
 * Implementation of a logic variable. E.g.: x, y, z, ...
 */
public class Variable extends Term {
    public Variable(String name) {
        super(name);
    }

    @Override
    public Term applySubstitution(Substitution substitution) {
        Optional<Term> substitutedTerm = substitution.getTerm(this);
        return substitutedTerm.orElse(this);
    }

    @Override
    public String toString() {
        return "Var{'" + this.getName() + "'}";
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
