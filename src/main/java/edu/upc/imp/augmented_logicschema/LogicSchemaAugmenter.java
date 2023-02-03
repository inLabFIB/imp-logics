package edu.upc.imp.augmented_logicschema;

import edu.upc.imp.augmented_logicschema.EventPredicate.EventType;
import edu.upc.imp.logicschema.*;
import edu.upc.imp.pipeline.LogicSchemaProcess;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for augmenting a logic schema.
 * <p>
 * The class should be used like a Transaction Controller:
 * - Create LogicSchemaAugmenter
 * - Invoke augment()/execute()
 * - Invoke getAugmentedLogicSchema()
 */
public class LogicSchemaAugmenter extends LogicSchemaProcess {
    private final LogicSchema inputLogicSchema;
    private final LogicSchema augmentedSchema;

    public LogicSchemaAugmenter(LogicSchema logicSchema) {
        assert logicSchema != null : "Input logic schema cannot be null";
        this.inputLogicSchema = logicSchema;
        this.augmentedSchema = new LogicSchema();
    }

    @Override
    public void execute() {
        this.augment();
    }

    /**
     * Augments the previously given logicSchema
     */
    public void augment() {
        this.createEventPredicates();

        this.augmentConstraints();
        this.augmentDerivationRules();
    }

    @Override
    public LogicSchema getOutputSchema() {
        return this.getAugmentedLogicSchema();
    }

    public LogicSchema getAugmentedLogicSchema() {
        return augmentedSchema;
    }

    /**
     * Implicitly duplicates all predicates into augmentedSchema and explicitly creates an ins_ del_ predicate for each
     */
    private void createEventPredicates() {
        for (Predicate p : inputLogicSchema.getAllPredicates()) {
            (new EventPredicate(p, EventType.INSERT)).copyToLogicSchema(augmentedSchema);
            (new EventPredicate(p, EventType.DELETE)).copyToLogicSchema(augmentedSchema);
        }
    }


    /**
     * Augments each constraint of the inputLogicSchema
     */
    private void augmentConstraints() {
        for (LogicConstraint logicConstraint : inputLogicSchema.getAllConstraints()) {
            for (LogicConstraint augmentedLogicConstraint : this.getAugmentedLogicConstraint(logicConstraint)) {
                this.augmentedSchema.addConstraint(augmentedLogicConstraint);
                this.recordOriginalConstraint(augmentedLogicConstraint, logicConstraint);
            }
        }
    }

    /**
     * @return an augmented logic constraint corresponding to the given one
     */
    private List<LogicConstraint> getAugmentedLogicConstraint(LogicConstraint logicConstraint) {
        List<LogicConstraint> result = new LinkedList<>();

        for (List<Literal> body : getAugmentedLiterals(augmentedSchema.getCopiedLiterals(logicConstraint.getLiterals()))) {
            //Checking for a positive event in the body
            boolean positiveEventFound = getHasPositiveEvent(body);
            if (positiveEventFound) {
                result.add(new LogicConstraint(body));
            }
        }

        return result;
    }


    /**
     * Augments each derivation rule of the input schema. That is, given P a derived predicate, the method creates in
     * the augmentedSchema the rules for ins_P, del_P and P'. The method creates the predicate P' if necessary.
     * <p>
     * This method uses the <i>Antoni Olivé</i> VLDB91 paper method of augmenting derivation rules.
     * The augmentation of del derivation rules are a bit different. Instead of generating more derivation rules (for
     * the different literals of P') a "not(P')" is added where P' has multiple derivation rules with the different
     * permutations.
     * <p>
     * Given a  DerivationRule of a P predicate in the inputSchema we obtain:
     * P     --> The same DR but copied to the augmentedSchema
     * P'    --> The different possible stats of the database using NEW and OLD over existing literals/Predicates
     * ins_P --> Used in New(P)
     * del_P --> Used in Old(P)
     */
    private void augmentDerivationRules() {
        for (DerivationRule inputDR : this.inputLogicSchema.getAllDerivationRules()) {
            // Copy dr into augmentedSchema
            Predicate p = augmentedSchema.getPredicate(inputDR.getPredicateName());
            Atom pHead = new Atom(p, inputDR.getHead().getTermsCopied());
            List<Literal> pLiterals = augmentedSchema.getCopiedLiterals(inputDR.getLiterals());
            new DerivationRule(pHead, pLiterals);

            List<List<Literal>> augmentedPLiterals = getAugmentedLiterals(pLiterals);

            Atom pPostHead = addPostDerivationRules(p, pHead, augmentedPLiterals);
            addInsDerivationRule(p, pHead, augmentedPLiterals);
            addDelDerivationRule(p, pHead, pLiterals, pPostHead);
        }
    }

    /**
     * This method adds a new predicate P'(pPost) and multiple derivation rules for it. There are 2^n rules generated
     * from all the combinations of NEW/OLD over the existing literals. It represents the possible future states of the
     * database where P will be true.
     *
     * @return The pPostHead needed to create the del_p derivation rules
     */
    private Atom addPostDerivationRules(Predicate p, Atom pHead, List<List<Literal>> augmentedPLiterals) {
        String pPostName = p.getName() + "'"; // aux --> aux'
        Predicate pPost = new PredicateImpl(pPostName, pHead.getTerms().size());
        this.augmentedSchema.addPredicate(pPost);

        // should not be needed if the input schema didn't contain any predicate with multiple derivation rules
        pPost = augmentedSchema.getPredicate(pPostName);

        Atom pPostHead = new Atom(pPost, pHead.getTermsCopied());
        for (List<Literal> pPostBody : augmentedPLiterals) {
            new DerivationRule(new Atom(pPostHead), getLiteralsCopied(pPostBody));
        }
        return pPostHead;
    }

    /**
     * The ins_p derivation rule is calculated using the following formula:
     * ---- ins_p <- l_1, ..., l_n, not(p(x))  which in the VLDB91 paper is found as
     * (11) ins_p <- p'_i,j(x), not(p(x))
     */
    private void addInsDerivationRule(Predicate p, Atom pHead, List<List<Literal>> augmentedPLiterals) {
        Predicate ins_p = augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(EventType.INSERT, p));
        for (List<Literal> pPostBody : augmentedPLiterals) {
            Atom insHead = new Atom(ins_p, pHead.getTermsCopied());
            List<Literal> insBody = getLiteralsCopied(pPostBody); // P'_i,j(x) for some i/j
            insBody.add(new OrdinaryLiteral(new Atom(pHead), false)); // not(P(x))
            if (getHasPositiveEvent(insBody)) {
                new DerivationRule(insHead, insBody);
            }
        }
    }

    /**
     * The del_p derivation rule is calculated using the following formula:
     * ---- del_p <- l_1, ..., l_i-1, NEW(not(l_i)), l_i+1, ..., l_n, not(p'(x)) which in the VLDB91 paper is found as
     * (17) del_p <- l_1, ..., l_i-1, [del(l_i)|ins(l_i)), l_i+1, ..., l_n, not(p'_1(x), ... ,p'_m(x)) (approx)
     */
    private void addDelDerivationRule(Predicate p, Atom pHead, List<Literal> pLiterals, Atom pPostHead) {
        Predicate del_p = augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(EventType.DELETE, p));
        for (int i = 0; i < pLiterals.size(); ++i) {
            Literal literal = pLiterals.get(i);
            if (literal instanceof OrdinaryLiteral) {
                OrdinaryLiteral oliteral = (OrdinaryLiteral) literal;
                List<Literal> pLiteralsCopy = getLiteralsCopied(pLiterals);

                Atom delPHead = new Atom(del_p, pHead.getTermsCopied());
                List<Literal> delPBody = new LinkedList<>(pLiteralsCopy.subList(0, i));
                if (oliteral.isPositive()) delPBody.add(this.getDeletion(oliteral)); // adds NEW(not(p(x)))
                else delPBody.add(this.getInsertion(oliteral));
                delPBody.addAll(pLiteralsCopy.subList(i + 1, pLiterals.size()));
                delPBody.add(new OrdinaryLiteral(new Atom(pPostHead), false)); // not(P'(x))
                new DerivationRule(delPHead, delPBody);
            }
        }
    }


    /**
     * @return a list containing the augmented literals list of literalsToAugment
     */
    protected List<List<Literal>> getAugmentedLiterals(List<Literal> literalsToAugment) {
        return this.getAugmentedLiterals(new LinkedList<>(), literalsToAugment);
    }

    /**
     * @return a list containing the augmented literals list of literalsToAugment with augmentedLiterals in the front.
     */
    private List<List<Literal>> getAugmentedLiterals(List<Literal> augmentedLiterals, List<Literal> literalsToAugment) {
        List<List<Literal>> result = new LinkedList<>();
        if (literalsToAugment.isEmpty()) {
            result.add(new LinkedList<>(augmentedLiterals));
        } else {
            Literal literalToAugment = literalsToAugment.remove(0);
            if (literalToAugment instanceof BuiltInLiteral) {
                BuiltInLiteral bil = new BuiltInLiteral((BuiltInLiteral) literalToAugment);
                augmentedLiterals.add(bil);
                result = this.getAugmentedLiterals(augmentedLiterals, literalsToAugment);
                augmentedLiterals.remove(bil);
            } else {
                OrdinaryLiteral oliteral = (OrdinaryLiteral) literalToAugment;
                if (oliteral.isPositive()) {
                    OrdinaryLiteral insertionLiteral = getInsertion(oliteral);
                    augmentedLiterals.add(insertionLiteral);
                    result.addAll(this.getAugmentedLiterals(augmentedLiterals, literalsToAugment));
                    augmentedLiterals.remove(insertionLiteral);

                    OrdinaryLiteral existsLiteral = new OrdinaryLiteral(oliteral);
                    OrdinaryLiteral deletionLiteral = getDeletion(oliteral);
                    deletionLiteral.setNegative();
                    augmentedLiterals.add(existsLiteral);
                    augmentedLiterals.add(deletionLiteral);
                    result.addAll(this.getAugmentedLiterals(augmentedLiterals, literalsToAugment));
                    augmentedLiterals.remove(existsLiteral);
                    augmentedLiterals.remove(deletionLiteral);
                } else {
                    OrdinaryLiteral deletionLiteral = getDeletion(oliteral);
                    augmentedLiterals.add(deletionLiteral);
                    result.addAll(this.getAugmentedLiterals(augmentedLiterals, literalsToAugment));
                    augmentedLiterals.remove(deletionLiteral);

                    OrdinaryLiteral existsLiteral = new OrdinaryLiteral(oliteral);
                    OrdinaryLiteral insertionLiteral = getInsertion(oliteral);
                    insertionLiteral.setNegative();
                    augmentedLiterals.add(existsLiteral);
                    augmentedLiterals.add(insertionLiteral);
                    result.addAll(this.getAugmentedLiterals(augmentedLiterals, literalsToAugment));
                    augmentedLiterals.remove(existsLiteral);
                    augmentedLiterals.remove(insertionLiteral);
                }
            }
            literalsToAugment.add(0, literalToAugment);
        }
        return result;
    }


    /**
     * @return a copy of the given literal changing its predicate to its insertion predicate
     */
    private OrdinaryLiteral getInsertion(OrdinaryLiteral oliteral) {
        return getEvent(EventType.INSERT, oliteral);
    }

    /**
     * @return a copy of the given literal changing its predicate to its deletion predicate
     */
    private OrdinaryLiteral getDeletion(OrdinaryLiteral oliteral) {
        return getEvent(EventType.DELETE, oliteral);
    }

    /**
     * @return a copy of the given literal changing its predicate to its corresponding event predicate
     */
    private OrdinaryLiteral getEvent(EventType eventType, OrdinaryLiteral oliteral) {
        Predicate pred = this.augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(eventType, oliteral.getPredicate()));
        assert pred != null : "We could not find the " + eventType + " predicate for " + oliteral;
        return new OrdinaryLiteral(new Atom(pred, oliteral.getTermsCopied()));
    }

    /**
     * @return Copies of all literals passed as a parameter in a new list.
     */
    private List<Literal> getLiteralsCopied(List<Literal> literals) {
        return literals.stream().map(Literal::copy).collect(Collectors.toList());
    }

    /**
     * @return true iff body contains some positive event ordinary literal
     */
    private boolean getHasPositiveEvent(List<Literal> body) {
        boolean positiveEventFound = false;
        for (Iterator<Literal> it = body.iterator(); it.hasNext() && !positiveEventFound; ) {
            Literal lit = it.next();
            if (lit instanceof OrdinaryLiteral) {
                OrdinaryLiteral olit = (OrdinaryLiteral) lit;
                positiveEventFound = (olit.isPositive() && olit.getPredicate() instanceof EventPredicate);
            }
        }
        return positiveEventFound;
    }

}
