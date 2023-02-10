package edu.upc.imp.old.logicschema;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class implements the logic translation representation of a conceptual schema.
 */
public class LogicSchema {

    /**
     * All the constraints
     */
    private final LinkedList<LogicConstraint> constraints;

    /**
     * All the predicates
     */
    private final Map<String, Predicate> predicates;

    private DerivationRule goal;

    public LogicSchema() {
        constraints = new LinkedList<>();
        predicates = new HashMap<>();
    }

    public List<Literal> getCopiedLiterals(List<Literal> literals) {
        List<Literal> result = new ArrayList<>();
        for (Literal l : literals) result.add(getCopiedLiteral(l));
        return result;
    }

    public Literal getCopiedLiteral(Literal l) {
        if (l instanceof OrdinaryLiteral) {
            OrdinaryLiteral ol = (OrdinaryLiteral) l;
            return new OrdinaryLiteral(
                    new Atom(
                            this.getPredicate(ol.getPredicateName()),
                            ol.getTermsCopied()),
                    ol.isPositive());
        } else {
            return l.copy();
        }
    }

    /**
     * @return a list containing all the constraints
     */
    public List<LogicConstraint> getAllConstraints() {
        return new LinkedList<>(constraints);
    }

    /**
     * @return a list containing all the predicates
     */
    public LinkedList<Predicate> getAllPredicates() {
        return new LinkedList<>(predicates.values());
    }

    /**
     * @return the constraint whose id is the given by parameter or null
     * if it does not exists
     */
    public LogicConstraint getConstraintByNumber(int id) {
        for (LogicConstraint c : this.constraints) {
            if (c.getID() == id) return c;
        }
        return null;
    }

    /**
     * Adds the given predicate to the schema if it does not exist
     */
    public void addPredicate(Predicate p) {
        if (!predicates.containsKey(p.getName())) {
            predicates.put(p.getName(), p);
        }
    }

    /**
     * Adds the given constraint to the schema and any predicate inside it
     * not appearing in the schema.
     */
    public void addConstraint(LogicConstraint rule) {
        List<NormalClause> safeRules = new LinkedList<>();
        safeRules.add(rule);
        for (NormalClause safeRule : safeRules) {
            for (Predicate p : safeRule.getAllPredicatesClosure()) {
                if (!this.predicates.containsKey(p.getName())) {
                    this.addPredicate(p);
                }
            }
            if (!constraints.contains((LogicConstraint) safeRule)) constraints.add((LogicConstraint) safeRule);
        }
    }

    /**
     * Adds the given constraint to the schema without checking whether it currently
     * it is present in the schema or not
     */
    public void addConstraintWithoutCheckingExistance(LogicConstraint rule) {
        List<NormalClause> safeRules = new LinkedList<>();
        safeRules.add(rule);
        for (NormalClause safeRule : safeRules) {
            constraints.add((LogicConstraint) safeRule);
        }
    }

    /**
     * Sets the given rule as the logic goal
     *
     * @param rule != null
     */
    public void setGoal(DerivationRule rule) {
        assert rule != null;
        this.goal = rule;
    }

    /**
     * @return the logic goal
     */
    public DerivationRule getGoal() {
        return this.goal;
    }

    public void takeOutGoal() {
        this.goal = null;
    }

    /**
     * @param name name of the desired predicate
     * @return the predicate that has the given name or null if it does not exists
     */
    public Predicate getPredicate(String name) {
        return this.predicates.get(name);
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("% Constraints\n");
        for (LogicConstraint c : this.constraints) {
            result.append(c.toString()).append("\n");
        }
        result.append("\n");
        result.append("% DerivationRules\n");
        for (DerivationRule r : this.getAllDerivationRules()) {
            result.append(r.toString()).append("\n");
        }
        result.append("\n");
        if (goal != null) {
            result.append("% Goal\n");
            result.append(goal);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * This procedure prints all the schema in a recognisible format for SVTe
     * in the file given by parameter
     *
     * @param file file in which the schema will be printed
     */
    public void printSchema(File file) throws Exception {
        printSchema(file.getCanonicalPath());
    }

    /**
     * This procedure prints all the schema in a recognisible format for SVTe
     * in the file whose name is filename
     *
     * @param fileName fileName of the file in which the schema will be printed
     */
    public void printSchema(String fileName) throws Exception {
        try (FileWriter writer = new FileWriter(fileName); PrintWriter pw = new PrintWriter(writer)) {
            printSchema(pw);
        }
    }


    /**
     * This procedure prints all the schema in a recognisible format for SVTe
     * in the given printer
     *
     * @param pw printer
     */
    public void printSchema(PrintWriter pw) {
        pw.println(this);
    }

    /**
     * @return a LinkedList containing all the derivation rules of the schema
     */
    public List<DerivationRule> getAllDerivationRules() {
        LinkedList<DerivationRule> result = new LinkedList<>();
        for (Predicate p : this.getAllPredicates()) {
            result.addAll(p.getDefinitionRules());
        }
        return result;
    }

    /**
     * @return a copied list of the definition rules of the predicate whose name is
     * predicateName. The list will be empty if the predicate is base.
     */
    public List<DerivationRule> getDerivationRulesByHead(String predicateName) {
        return this.getPredicate(predicateName).getDefinitionRules();
    }

    /**
     * @return a list containing all the constraints and derivation rules of the schema
     */
    public List<NormalClause> getAllNormalClauses() {
        List<NormalClause> result = new LinkedList<>();
        result.addAll(this.getAllConstraints());
        result.addAll(this.getAllDerivationRules());
        return result;
    }

    /**
     * @return a list containing all the base predicates
     */
    public List<Predicate> getAllBasePredicates() {
        LinkedList<Predicate> result = new LinkedList<>();
        for (Predicate pred : this.getAllPredicates()) {
            if (pred.isBase()) {
                result.add(pred);
            }
        }
        return result;
    }

    public void deleteConstraint(int id) {
        this.constraints.remove(this.getConstraintByNumber(id));
    }

    public void deletePredicate(Predicate pred) {
        this.predicates.remove(pred.getName());
    }

    public void deleteUnusedPredicates() {
        Set<Predicate> usedPredicates = new HashSet<>();
        for (LogicConstraint lc : this.constraints) {
            usedPredicates.addAll(lc.getAllPredicatesClosure());
        }
        for (Predicate p : this.getAllPredicates()) {
            if (!usedPredicates.contains(p)) this.deletePredicate(p);
        }
    }
}
