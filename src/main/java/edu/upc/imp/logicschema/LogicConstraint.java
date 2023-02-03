package edu.upc.imp.logicschema;

import java.util.*;

/**
 * Implementation of a logic constraint. That is, a NormalClause  without head
 */
public class LogicConstraint extends NormalClause {
    private static int nextID = 1;
    private final int id;                             //ID


    /**
     * Constructs a new Constraint using the list passed by parameter and the given id;
     */
    public LogicConstraint(int id, List<Literal> literals) {
        super(literals);
        this.id = id;

        if (nextID <= id) nextID = id + 1;
    }

    /**
     * Constructs a new Constraint using the list passed by parameter
     */
    public LogicConstraint(List<Literal> literals) {
        super(literals);
        id = nextID++;
    }

    /**
     * This function is a quick patch to reset the constraints ID numbering to 1.
     * This function will be removed
     */
    @Deprecated
    public static void reset() {
        nextID = 1;
    }

    /**
     * Constructs a new Constraint using the id of c and the given literals
     */
    private LogicConstraint(LogicConstraint c, List<Literal> literals) {
        super(literals);
        this.id = c.id;
    }

    /**
     * @return the constraint identification number
     */
    public int getID() {
        return id;
    }

    @Override
    protected String getHeadAsString() {
        return "  @" + id;
    }

    @Override
    protected int hashCodeSpecific() {
        return Integer.valueOf(this.id).hashCode();
    }

    @Override
    protected HashMap<String, String> getVariableToVariableSubstitutionForHead(NormalClause nc) {
        return new HashMap<>();
    }

    @Override
    public LogicConstraint copyChangingBody(List<Literal> bodyCopy) {
        return new LogicConstraint(this, bodyCopy);
    }

    /**
     * @return a list containing a list of the different possibilities of unfolding the negated literals
     * of this. For such unfolding, the negated literals are interpreted as positive literals, and unfolded.
     * If the negated literals are base, this function just returns a list containing a list of the negated
     * literals copied as positive literals.
     */
    @Deprecated
    public List<List<Literal>> getNegatedLiteralsUnfolded() {
        List<List<Literal>> result = new LinkedList<>();
        for (OrdinaryLiteral literal : this.getOrdinaryLiterals()) {
            if (literal.isNegated()) {
                result.addAll(new OrdinaryLiteral(literal, true).getUnfoldedLiteral(new HashSet<>()));
            }
        }
        return result;
    }

    public Set<String> getPositivePredicateNames() {
        Set<String> result = new HashSet<>();
        for (OrdinaryLiteral literal : this.getPositiveOrdinaryLiterals()) {
            result.add(literal.getPredicateName());
        }
        return result;
    }

    /**
     * @return whether this constraint is a subset of the given one
     */
    public boolean isSubsetOf(LogicConstraint givenConstraint) {
        return this.getVariableToVariableSubstitution(givenConstraint) != null;
    }

    /**
     * @return whether this constraint is a subset of the given literals list
     */
    public boolean isSubsetOf(List<Literal> literals) {
        LogicConstraint dumpConstraint = new LogicConstraint(literals);
        return this.isSubsetOf(dumpConstraint);
    }

}
