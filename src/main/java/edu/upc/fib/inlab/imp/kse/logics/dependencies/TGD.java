package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.schema.*;

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
        return getBody().stream()
                .flatMap(l -> l.getTerms().stream())
                .filter(Variable.class::isInstance)
                .map(t -> (Variable) t)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Variable> getExistentialVariables() {
        return getHead().stream()
                .flatMap(a -> a.getTerms().stream())
                .filter(Variable.class::isInstance)
                .map(t -> (Variable) t)
                .filter(t -> !getUniversalVariables().contains(t))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isLinear() {
        return getBody().size() == 1;
    }


    @Override
    public boolean isGuarded() {
        Set<Variable> uVars = getUniversalVariables();
        for (Literal l: getBody()) {
            if (new HashSet<>(l.getTerms()).containsAll(uVars)) return true;
        }
        return false;
    }
}
