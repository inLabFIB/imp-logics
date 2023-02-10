package edu.upc.imp.old.logicschema_normalizer;

import edu.upc.imp.old.logicschema.*;
import edu.upc.imp.old.pipeline.LogicSchemaProcess;

import java.util.*;

/**
 * Class for obtaining a normalized version of a LogicSchema.
 * <p>
 * The class should be used like a Transaction Controller:
 * - Create LogicSchemaNormalizer
 * - Invoke normalize()/execute()
 * - Invoke getNormalizedLogicSchema()
 */
public class LogicSchemaNormalizer extends LogicSchemaProcess {
    private final LogicSchema inputLogicSchema;
    private final LogicSchema normalizedLogicSchema;

    public LogicSchemaNormalizer(LogicSchema inputLogicSchema) {
        assert inputLogicSchema != null : "Input logic schema cannot be null";
        this.inputLogicSchema = inputLogicSchema;
        this.normalizedLogicSchema = new LogicSchema();
    }

    @Override
    public void execute() {
        this.normalize();
    }

    /**
     * Normalize the previously given LogicSchema into the normalizedLogicSchema.
     * This process avoid aliasing and any kind of modification of the inputSchema
     */
    public void normalize() {
        this.copyPredicates();
        this.copyNormalClauses();

        this.applyPositiveUnfolding();
        this.applyNegativeUnfolding();
        this.applyLiteralsSorting();

        this.removeUnusedDerivedPredicates();
    }

    @Override
    public LogicSchema getOutputSchema() {
        return this.getNormalizedLogicSchema();
    }

    public LogicSchema getNormalizedLogicSchema() {
        return normalizedLogicSchema;
    }


    /**
     * Copies(deep copy) the predicates from the inputSchema to the normalized one.
     */
    private void copyPredicates() {
        for (Predicate pred : this.inputLogicSchema.getAllPredicates()) {
            pred.copyToLogicSchema(normalizedLogicSchema);
        }
    }

    /**
     * Copies all NormalClauses from the inputSchema to the normalizedSchema
     */
    private void copyNormalClauses() {
        //Apply unfoldings on DerivationRules
        for (DerivationRule dr : this.inputLogicSchema.getAllDerivationRules()) {
            Atom newHead = new Atom(normalizedLogicSchema.getPredicate(dr.getPredicateName()), dr.getHead().getTermsCopied());
            new DerivationRule(newHead, normalizedLogicSchema.getCopiedLiterals(dr.getLiterals()));
        }

        //Apply unfoldings on Constraints
        for (LogicConstraint lc : this.inputLogicSchema.getAllConstraints()) {
            LogicConstraint newConstraint = new LogicConstraint(normalizedLogicSchema.getCopiedLiterals(lc.getLiterals()));
            this.normalizedLogicSchema.addConstraint(newConstraint);
            this.recordOriginalConstraint(newConstraint, lc);
        }
    }


    /**
     * Replaces all Normal Clauses of the normalizedSchema having applied positive unfolding.
     * <p>
     * Recursively unfolds all the positive derived literals for all the logic constraints and for all the
     * derivation rules.
     * <p>
     * TODO: Works BUT: maybe could be optimized using DP or DFS avoiding redundant unfoldings between this method and
     *  computeUnfoldedLiterals
     */
    private void applyPositiveUnfolding() {
        //Apply unfoldings on DerivationRules
        for (DerivationRule dr : this.normalizedLogicSchema.getAllDerivationRules()) {
            dr.getPredicate().deleteDerivationRule(dr); // Delete Predicate reference
            for (List<Literal> unfoldedLiterals : getUnfoldedLiterals(dr.getLiterals())) {
                new DerivationRule(dr.getHead(), unfoldedLiterals);
            }
        }

        //Apply unfoldings on Constraints
        for (LogicConstraint lc : this.normalizedLogicSchema.getAllConstraints()) {
            LogicConstraint originalLogicConstraint = this.getOriginalConstraint(lc.getID());
            boolean first = true;
            for (List<Literal> unfoldedLiterals : getUnfoldedLiterals(lc.getLiterals())) {
                LogicConstraint newConstraint = new LogicConstraint(unfoldedLiterals);
                if (first) {
                    this.normalizedLogicSchema.deleteConstraint(lc.getID());
                    this.normalizedLogicSchema.addConstraint(newConstraint);
                    this.replaceOriginalConstraint(newConstraint, lc);
                    first = false;
                } else {
                    this.normalizedLogicSchema.addConstraint(newConstraint);
                    this.recordOriginalConstraint(newConstraint, originalLogicConstraint);
                }
            }
        }
    }

    /**
     * Given a constraint/derivationRule with a negated derived literal L with n derivation rules, this method creates
     * n predicates called L1...LN each of which with one derivationRule taken from L. Then, the method replace the L
     * literal with L1...LN literals.
     * <p>
     * This step Modifies each NormalClause previouslly added to the normalizedLogicSchema and applies a negative unfolding process
     */
    private void applyNegativeUnfolding() {
        List<String> visitedPredicates = new ArrayList<>();
        for (LogicConstraint lc : normalizedLogicSchema.getAllConstraints()) {
            applyRecursiveNegativeUnfolding(lc, 0, visitedPredicates);
        }
    }

    private void applyRecursiveNegativeUnfolding(NormalClause nc, int depth, List<String> visitedPredicates) {
        List<Literal> newBody = new ArrayList<>();
        boolean unfoldingPerformed = false;

        for (Literal l : nc.getLiterals()) {
            if (l instanceof OrdinaryLiteral) {
                OrdinaryLiteral olit = (OrdinaryLiteral) l;
                if (!olit.isBase()) {
                    String predicateName = olit.getPredicateName();
                    if (!visitedPredicates.contains(predicateName)) {
                        for (DerivationRule dr : olit.getPredicate().getDefinitionRules()) {
                            applyRecursiveNegativeUnfolding(dr, depth + 1, visitedPredicates);
                        }
                        visitedPredicates.add(predicateName);
                    }
                    if (olit.getNumberOfDerivationRules() > 1) {
                        newBody.addAll(getMultipleDerivationRuleUnfolding(olit));
                        unfoldingPerformed = true;
                    } else newBody.add(l);
                } else newBody.add(l);
            } else newBody.add(l);
        }

        //Sorting literals shouldn't be needed
        if (unfoldingPerformed) {
            if (depth == 0) { //LC
                LogicConstraint lc = (LogicConstraint) nc;
                LogicConstraint lcUnfolded = new LogicConstraint(newBody);
                this.normalizedLogicSchema.deleteConstraint(lc.getID());
                this.normalizedLogicSchema.addConstraint(lcUnfolded);
                replaceOriginalConstraint(lcUnfolded, lc);
            } else { //DR
                DerivationRule dr = (DerivationRule) nc;
                dr.getPredicate().deleteDerivationRule(dr); // Delete Predicate reference
                new DerivationRule(dr.getHead(), newBody);
            }
        }
    }

    /**
     * @return a list of literals L1...LN each one with one derivation rule taken from the list of derivation rules of
     * olit. If olit is base, or it has only one derivation rule it returns olit.
     */
    protected List<Literal> getMultipleDerivationRuleUnfolding(OrdinaryLiteral olit) {
        List<Literal> result = new LinkedList<>();

        //No unfolding needed
        if (olit.isBase() || olit.getPredicate().getDefinitionRules().size() == 1) {
            result.add(olit);
            return result;
        }

        int n = 1;
        for (DerivationRule dr : olit.getPredicate().getDefinitionRules()) {
            List<Literal> literals = dr.getLiterals();
            String predicateName = olit.getPredicateName() + n;

            // Avoid collisions with already existing predicates of the input Schema. ".m" is added to the end of
            // the predicate name.
            Predicate pred = this.inputLogicSchema.getPredicate(predicateName);
            int m = 0;
            while (pred != null) {
                predicateName = olit.getPredicateName() + n + "." + m++;
                pred = this.inputLogicSchema.getPredicate(predicateName);
            }

            // New Predicates + Derivation Rules are added if no previous olit with the same predicate was already
            // processed before.
            pred = this.normalizedLogicSchema.getPredicate(predicateName);
            if (pred == null) {
                pred = new PredicateImpl(predicateName, olit.getTerms().size());
                this.normalizedLogicSchema.addPredicate(pred);
                Atom atomHead = new Atom(pred, dr.getHead().getTermsCopied());
                new DerivationRule(atomHead, literals);
            }

            // Adds new Ordinary Literal with new predicate linked.
            result.add(new OrdinaryLiteral(new Atom(pred, olit.getTermsCopied()), olit.isPositive()));
            n++;
        }
        return result;
    }

    /**
     * Sort the literals of all Logic constraints and derivation rules
     * so that, positive literals appear before negative literals, and
     * negative literals before built-in literals
     */
    private void applyLiteralsSorting() {
        //Apply literals sorting on LogicConstraints
        for (LogicConstraint lc : this.normalizedLogicSchema.getAllConstraints()) {
            List<Literal> sortedLiterals = getSortedLiterals(lc.getLiterals());
            LogicConstraint sortedLiteralsLc = new LogicConstraint(sortedLiterals);
            this.normalizedLogicSchema.deleteConstraint(lc.getID());
            this.normalizedLogicSchema.addConstraint(sortedLiteralsLc);
            this.replaceOriginalConstraint(sortedLiteralsLc, lc);
        }

        //Apply literals sorting on DerivationRules
        for (DerivationRule dr : this.normalizedLogicSchema.getAllDerivationRules()) {
            List<Literal> sortedLiterals = getSortedLiterals(dr.getLiterals());
            dr.getPredicate().deleteDerivationRule(dr);
            new DerivationRule(dr.getHead(), sortedLiterals);
        }
    }


    private void removeUnusedDerivedPredicates() {
        Set<String> usedPredicates = new HashSet<>();
        for (LogicConstraint constraint : this.normalizedLogicSchema.getAllConstraints()) {
            usedPredicates.addAll(constraint.getAllPredicatesNamesClosure());
        }

        for (Predicate pred : this.normalizedLogicSchema.getAllPredicates()) {
            if (!pred.isBase() && !usedPredicates.contains(pred.getName())) {
                this.normalizedLogicSchema.deletePredicate(pred);
            }
        }
    }


    /**
     * @return a copy of this list in which positive literals appear before negative literals
     * and negative literals before built-in literals.
     */
    protected List<Literal> getSortedLiterals(List<Literal> literals) {
        List<Literal> result = new LinkedList<>();

        List<Literal> positiveOrdinaryLiterals = new LinkedList<>();
        List<Literal> negativeOrdinaryLiterals = new LinkedList<>();
        List<Literal> builtInLiterals = new LinkedList<>();

        for (Literal lit : literals) {
            if (lit instanceof OrdinaryLiteral) {
                OrdinaryLiteral oliteral = (OrdinaryLiteral) lit;
                if (oliteral.isPositive()) positiveOrdinaryLiterals.add(lit);
                else negativeOrdinaryLiterals.add(lit);
            } else builtInLiterals.add(lit);
        }

        result.addAll(positiveOrdinaryLiterals);
        result.addAll(negativeOrdinaryLiterals);
        result.addAll(builtInLiterals);

        return result;
    }

    /**
     * @return the different unfoldings of the given literals. All the positive literals returned are base.
     * <p>
     * Warning! Do not use recursive predicates or this method will hang.
     */
    protected List<List<Literal>> getUnfoldedLiterals(List<Literal> literals) {
        List<List<Literal>> result = new LinkedList<>();
        this.computeUnfoldedLiterals(result, literals, 0);
        return result;
    }

    /**
     * [Recursive Function] - Unfolds the literals of currentLiterals starting from the given index and stores the
     * result in result.
     */
    private void computeUnfoldedLiterals(List<List<Literal>> result, List<Literal> currentLiterals, int index) {
        if (index >= currentLiterals.size()) {
            result.add(currentLiterals);
        } else {
            Literal lit = currentLiterals.get(index);
            if (lit instanceof BuiltInLiteral) {
                this.computeUnfoldedLiterals(result, currentLiterals, index + 1);
            } else {
                OrdinaryLiteral olit = (OrdinaryLiteral) lit;
                if (olit.isNegated()) {
                    this.computeUnfoldedLiterals(result, currentLiterals, index + 1);
                } else {
                    if (olit.isBase()) {
                        this.computeUnfoldedLiterals(result, currentLiterals, index + 1);
                    } else {
                        List<Literal> upperLiterals = new LinkedList<>(currentLiterals);
                        for (List<Literal> unfoldedLiterals : olit.getDefinitionRulesWhenCalled(upperLiterals)) {
                            List<Literal> newCurrentLiterals = new LinkedList<>(currentLiterals.subList(0, index));
                            newCurrentLiterals.addAll(unfoldedLiterals);
                            newCurrentLiterals.addAll(currentLiterals.subList(index + 1, currentLiterals.size()));
                            this.computeUnfoldedLiterals(result, newCurrentLiterals, index);
                        }
                    }
                }
            }
        }
    }

}
