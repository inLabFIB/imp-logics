package edu.upc.fib.inlab.imp.kse.logics.logicschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.operations.Substitution;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.visitor.LogicSchemaVisitor;

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
    public boolean isConstant() {
        return true;
    }

    @Override
    public <T> T accept(LogicSchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return "Const{'" + this.getName() + "'}";
    }
}
