package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.Atom;
import edu.upc.fib.inlab.imp.kse.logics.schema.ImmutableAtomList;
import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;
import edu.upc.fib.inlab.imp.kse.logics.schema.Variable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    protected TGD(List<Literal> body, List<Atom> head) {
        super(body);
        if (Objects.isNull(head)) throw new IllegalArgumentException("Head cannot be null");
        if (head.isEmpty()) throw new IllegalArgumentException("Head cannot be empty");
        this.head = new ImmutableAtomList(head);
    }

    public ImmutableAtomList getHead() {
        return head;
    }

    @Override
    public Set<Variable> getUniversalVariables() {
        return Collections.emptySet();
    }

    @Override
    public Set<Variable> getExistentialVariables() {
        return Collections.emptySet();
    }

    //TODO: IMPL-569 Implement LINEAR DATALOG check
    @Override
    public boolean isLinear() {
        return false;
    }

    //TODO: IMPR-187 Implement Guarded Datalog check
    @Override
    public boolean isGuarded() {
        return false;
    }
}
