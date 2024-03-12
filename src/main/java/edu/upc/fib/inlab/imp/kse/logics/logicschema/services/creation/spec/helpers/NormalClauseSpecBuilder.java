package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.helpers;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.BuiltInLiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.LiteralSpec;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.creation.spec.OrdinaryLiteralSpec;

import java.util.LinkedList;
import java.util.List;

/**
 * Common part of the LogicConstraintSpec and DerivationRuleSpec builders
 *
 * @param <T> kind of NormalClauseSpecBuilder being extended.
 */
public abstract class NormalClauseSpecBuilder<T extends NormalClauseSpecBuilder<T>> {
    /**
     * This class implements a hierarchy of builders as discussed in
     * <a href="https://stackoverflow.com/questions/21086417/builder-pattern-and-inheritance">stackoverflow</a>
     */
    protected final StringToTermSpecFactory stringToTermSpecFactory;
    protected final List<LiteralSpec> bodySpec = new LinkedList<>();

    protected NormalClauseSpecBuilder(TermTypeCriteria termTypeCriteria) {
        this.stringToTermSpecFactory = new StringToTermSpecFactory(termTypeCriteria);
    }

    public T addAllLiteralSpecs(List<LiteralSpec> literals) {
        bodySpec.addAll(literals);
        return self();
    }

    public T addLiteralSpec(LiteralSpec literal) {
        bodySpec.add(literal);
        return self();
    }

    public T addOrdinaryLiteral(String predicateName, String... terms) {
        return addOrdinaryLiteral(predicateName, true, terms);
    }

    public T addNegatedOrdinaryLiteral(String predicateName, String... terms) {
        return addOrdinaryLiteral(predicateName, false, terms);
    }

    public T addOrdinaryLiteral(String predicateName, boolean isPositive, String... terms) {
        bodySpec.add(new OrdinaryLiteralSpec(predicateName, stringToTermSpecFactory.createTermSpecs(terms), isPositive));
        return self();
    }

    public T addBuiltInLiteral(String builtInOperation, String... terms) {
        bodySpec.add(new BuiltInLiteralSpec(builtInOperation, stringToTermSpecFactory.createTermSpecs(terms)));
        return self();
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

}
