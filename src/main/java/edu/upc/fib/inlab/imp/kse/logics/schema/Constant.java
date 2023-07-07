package edu.upc.fib.inlab.imp.kse.logics.schema;

import edu.upc.fib.inlab.imp.kse.logics.schema.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.schema.visitor.LogicSchemaVisitor;

/**
 * Implementation of a logic constant. E.g.: `Socrates`, 4, etc.
 */
public class Constant extends Term {
    public Constant(String name) {
        super(name);
    }

    @Override
    public Term applySubstitution(Substitution substitution) {
        return this;
    }

    @Override
    public String toString() {
        return "Const{'" + this.getName() + "'}";
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean isConstant() {
        return true;
    }
}
