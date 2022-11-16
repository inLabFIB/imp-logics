package edu.upc.mpi.logicschema_normalizer;

import edu.upc.mpi.augmented_logicschema.LogicSchemaProcess;
import edu.upc.mpi.logicschema.*;

import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

/**
 * Class for normalizing a logic schema.
 * <p>
 * The class should be used like a Transaction Controller:
 * - Create LogicSchemaNormalizer
 * - Invoke normalize()
 * - Invoke getNormalizedLogicSchema()
 * <p>
 * Important: this method corrupts the input logicSchema. Please, use
 * only the output logic schema after calling augment()
 *
 */
public class LogicSchemaNormalizer extends LogicSchemaProcess {
    private final LogicSchema inputLogicSchema;
    private final LogicSchema normalizedLogicSchema;

    public LogicSchemaNormalizer(LogicSchema logicSchema) {
        assert logicSchema != null : "Input logic schema cannot be null";
        this.inputLogicSchema = logicSchema;
        this.normalizedLogicSchema = new LogicSchema();
    }

    /**
     * Normalize the previously given logicSchema
     */
    public void normalize() {
        this.copyPredicates();
        this.applyPositiveUnfolding();
        this.applyNegativeUnfolding();
        this.applyLiteralsSorting();
        this.removeUnusedDerivedPredicates();
    }

    public LogicSchema getNormalizedLogicSchema() {
        return normalizedLogicSchema;
    }

    /**
     * Sort the literals of all Logic constraints and derivaton rules
     * so that, positive literals appear before negative literals, and
     * negative literals before built-in literals
     */
    private void applyLiteralsSorting() {
        //Apply literals sorting on LogicConstraints
        for (LogicConstraint lc : this.normalizedLogicSchema.getAllConstraints()) {
            List<Literal> sortedLiterals = getSortedLiterals(lc.getLiterals());
            LogicConstraint sortedLiteralsLc = new LogicConstraint(lc.getID(), sortedLiterals);
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

    /**
     * @param literals
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
     * Given a constraint/derivationRule with a negated derived literal L with n derivation rules,
     * this method creates n predicates called L1...LN each of which with one derivationRule
     * taken from L. Then, the method replace the L literal with L1...LN literals
     */
    private void applyNegativeUnfolding() {
        //Apply negative unfolding on LogicConstraints
        for (LogicConstraint lc : this.normalizedLogicSchema.getAllConstraints()) {
            List<Literal> literals = new LinkedList<>();
            for (Literal l : lc.getLiterals()) {
                if (l instanceof OrdinaryLiteral) {
                    OrdinaryLiteral olit = (OrdinaryLiteral) l;
                    if (!olit.isPositive()) {
                        literals.addAll(getMultipleDerivationRuleUnfolding(olit));
                    } else literals.add(l);
                } else literals.add(l);
            }

            LogicConstraint lcUnfolded = new LogicConstraint(lc.getID(), literals);
            this.normalizedLogicSchema.deleteConstraint(lc.getID());
            this.normalizedLogicSchema.addConstraint(lcUnfolded);
            replaceOriginalConstraint(lcUnfolded, lc);
        }

        //Apply negative unfolding on DerivationRule
        for (DerivationRule dr : this.normalizedLogicSchema.getAllDerivationRules()) {
            List<Literal> literals = new LinkedList<>();
            for (Literal l : dr.getLiterals()) {
                if (l instanceof OrdinaryLiteral) {
                    OrdinaryLiteral olit = (OrdinaryLiteral) l;
                    if (!olit.isPositive()) {
                        literals.addAll(getMultipleDerivationRuleUnfolding(olit));
                    } else literals.add(l);
                } else literals.add(l);
            }

            List<Literal> sortedLiterals = getSortedLiterals(literals);
            dr.getPredicate().deleteDerivationRule(dr);
            new DerivationRule(dr.getHead(), sortedLiterals);
        }
    }



    /**
     * @param olit
     * @return a list of literals L1...LN each one with one derivation rule taken from the
     * list of derivation rules of olit. If olit is base, or it has only one derivation rule it returns olit.
     */
    protected List<Literal> getMultipleDerivationRuleUnfolding(OrdinaryLiteral olit) {
        List<Literal> result = new LinkedList<>();

        if (olit.isBase() || olit.getDefinitionRulesWhenCalled(new LinkedList<>()).size() == 1) {
            result.add(olit);
        } else {
            int n = 1;
            for (DerivationRule dr: olit.getPredicate().getDefinitionRules()) {
                List<Literal> literals = dr.getLiterals();
                String predicateName = olit.getPredicateName() + n;

                //Avoid duplicates with already existing predicates(of the input Schema)
                Predicate pred = this.inputLogicSchema.getPredicate(predicateName);
                int m = 0;
                while(pred != null) {
                    predicateName = olit.getPredicateName() + n + "." + m++;
                    pred = this.inputLogicSchema.getPredicate(predicateName);
                }

                pred = this.normalizedLogicSchema.getPredicate(predicateName);
                if(pred == null) {
                    pred = new PredicateImpl(predicateName, olit.getTerms().size());
                    this.normalizedLogicSchema.addPredicate(pred);
                    Atom atomHead = new Atom(pred, dr.getHead().getTermsCopied());
                    new DerivationRule(atomHead, literals);
                }

                result.add(new OrdinaryLiteral(new Atom(pred, olit.getTermsCopied()), olit.isPositive()));
                n++;
            }
        }
        return result;
    }

    /**
     * Recursively unfolds all the positive derived literals for all the logic
     * constraints and forAll the derivationRules
     */
    private void applyPositiveUnfolding() {
        //Apply unfoldings on derivationRules
        for (DerivationRule dr : this.inputLogicSchema.getAllDerivationRules()) {
            dr.getPredicate().deleteDerivationRule(dr);
            for (List<Literal> unfoldedLiterals : getUnfoldedLiterals(dr.getLiterals())) {
                new DerivationRule(dr.getHead(), unfoldedLiterals);
            }
        }

        //Apply unfoldings on Constraints
        for (LogicConstraint lc : this.inputLogicSchema.getAllConstraints()) {
            int id = lc.getID() * 100;
            for (List<Literal> unfoldedLiterals : getUnfoldedLiterals(lc.getLiterals())) {
                LogicConstraint newConstraint = new LogicConstraint(id, unfoldedLiterals);
                this.normalizedLogicSchema.addConstraint(newConstraint);
                this.recordOriginalConstraint(newConstraint, lc);
                id++;
            }
        }
    }

    /**
     * Copies the predicates from the inputSchema to the normalized one.
     */
    private void copyPredicates() {
        for (Predicate pred : this.inputLogicSchema.getAllPredicates()) {
            this.normalizedLogicSchema.addPredicate(pred);
        }
    }

    /**
     * Unfolds the literals of currentLiterals starting from the given index
     * and stores the result in result.
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

    /**
     * @param literals
     * @return the different unfoldings of the given literals. All the literals returned are base.
     * Please, do not use recursive predicates or this method will hang.
     */
    protected List<List<Literal>> getUnfoldedLiterals(List<Literal> literals) {
        List<List<Literal>> result = new LinkedList<>();
        this.computeUnfoldedLiterals(result, literals, 0);
        return result;
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


}
