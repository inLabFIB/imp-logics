package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Atom;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.ImmutableAtomList;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Variable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tuple-Generating Dependency
 */
public class TGD extends Dependency {

    /**
     * Invariants:
     * <ul>
     *     <li>head must not be null</li>
     *     <li>head must not be empty</li>
     *     <li>head must be immutable</li>
     * </ul>
     */
    private final ImmutableAtomList head;

    public TGD(List<Literal> body, List<Atom> head) {
        super(body);
        if (Objects.isNull(head)) throw new IllegalArgumentException("Head cannot be null");
        if (head.isEmpty()) throw new IllegalArgumentException("Head cannot be empty");
        this.head = new ImmutableAtomList(head);
    }

    public Set<Variable> getExistentialVariables() {
        Set<Variable> universalVariables = getUniversalVariables();
        return getHead().stream()
                .flatMap(a -> a.getTerms().stream())
                .filter(Variable.class::isInstance)
                .map(t -> (Variable) t)
                .filter(t -> !universalVariables.contains(t))
                .collect(Collectors.toSet());
    }

    public ImmutableAtomList getHead() {
        return head;
    }

    /**
     * @return those variables that appear both, in the head and the body of the TGD
     */
    public Set<Variable> getFrontierVariables() {
        Set<Variable> result = new LinkedHashSet<>();
        for (Atom headAtom : head) {
            Set<Variable> headVariables = headAtom.getVariables();
            headVariables.retainAll(getUniversalVariables());
            result.addAll(headVariables);
        }
        return result;
    }

    public boolean isLinear() {
        if (containsBuiltInOrNegatedLiteralInBody())
            throw new UnsupportedOperationException("Linearity check not implemented yet when body contains built-in or negated literals");
        return getBody().size() == 1;
    }

    public boolean isGuarded() {
        if (containsBuiltInOrNegatedLiteralInBody())
            throw new UnsupportedOperationException("Guardedness check not implemented yet when body contains built-in or negated literals");
        Set<Variable> uVars = getUniversalVariables();
        for (Literal l : getBody()) {
            if (new HashSet<>(l.getTerms()).containsAll(uVars)) return true;
        }
        return false;
    }
    //get guard??

    ///get side atoms??


    @Override
    public <T> T accept(DependencySchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return getBody().toString() + " -> " + head.toString();
    }
}
