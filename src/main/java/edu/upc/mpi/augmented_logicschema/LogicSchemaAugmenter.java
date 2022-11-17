package edu.upc.mpi.augmented_logicschema;

import edu.upc.mpi.augmented_logicschema.EventPredicate.EventType;
import edu.upc.mpi.logicschema.Atom;
import edu.upc.mpi.logicschema.BuiltInLiteral;
import edu.upc.mpi.logicschema.DerivationRule;
import edu.upc.mpi.logicschema.Literal;
import edu.upc.mpi.logicschema.LogicConstraint;
import edu.upc.mpi.logicschema.LogicSchema;
import edu.upc.mpi.logicschema.OrdinaryLiteral;
import edu.upc.mpi.logicschema.Predicate;
import edu.upc.mpi.logicschema.PredicateImpl;
import edu.upc.mpi.pipeline.LogicSchemaProcess;

import java.util.*;

/**
 * Class for augmenting a logic schema.
 * 
 * The class should be used like a Transaction Controller:
 - Create LogicSchemaAugmenter
 - Invoke augment()
 - Invoke getAugmentedLogicSchema()
 * 
 * Important: this method corrupts the input logicSchema. Please, use
 * only the output logic schema after calling augment()
 * 
 */
public class LogicSchemaAugmenter extends LogicSchemaProcess {
    private final LogicSchema inputLogicSchema;
    private final LogicSchema augmentedSchema;
    
    public LogicSchemaAugmenter(LogicSchema logicSchema){
        assert logicSchema != null:"Input logic schema cannot be null";
        this.inputLogicSchema = logicSchema;
        this.augmentedSchema = new LogicSchema();
    }
    
    /**
     * Augments the previously given logicSchema
     */
    public void augment(){
        this.createEventPredicates();
        this.augmentConstraints();
        this.augmentDerivationRules();
    }
    
    public LogicSchema getAugmentedLogicSchema(){
        return augmentedSchema;
    }

    /**
     * Creates the ins_ and del_ event predicates for each predicate of the inputLogicSchema
     */
    private void createEventPredicates() {
        for(Predicate p: inputLogicSchema.getAllPredicates()){
            EventPredicate ins_p = new EventPredicate(p, EventPredicate.EventType.INSERT);
            EventPredicate del_p = new EventPredicate(p, EventPredicate.EventType.DELETE);
            
            this.augmentedSchema.addPredicate(p);
            this.augmentedSchema.addPredicate(ins_p);
            this.augmentedSchema.addPredicate(del_p);
        }
    }

    /**
     * Augments each constraint of the inputLogicSchema
     */
    private void augmentConstraints() {
        for(LogicConstraint logicConstraint: inputLogicSchema.getAllConstraints()){
            for(LogicConstraint augmentedLogicConstraint: this.getAumgentedLogicConstraint(logicConstraint)){
                this.augmentedSchema.addConstraint(augmentedLogicConstraint);
                this.recordOriginalConstraint(augmentedLogicConstraint, logicConstraint);
            }
        }
    }



    /**
     * 
     * @param logicConstraint
     * @return an augmented logic constraint corresponding to the given one
     */
    private List<LogicConstraint> getAumgentedLogicConstraint(LogicConstraint logicConstraint) {
        List<LogicConstraint> result = new LinkedList<>();
        
        int i = 0;
        for(List<Literal> body: getAugmentedLiterals(logicConstraint.getLiterals())){
            //Checking for a positive event in the body
            boolean positiveEventFound = getHasPositiveEvent(body);
            if(positiveEventFound){
                int identifier = logicConstraint.getID()*1000+i;
                result.add(new LogicConstraint(identifier, body));
                i++;
            }
        }
        
        return result;
    }
    
    /**
     * 
     * @param literalsToAugment
     * @return a list containing the augmented literals list of literalsToAugment
     */
    protected List<List<Literal>> getAugmentedLiterals(List<Literal> literalsToAugment) {
        return this.getAugmentedLiterals(new LinkedList<>(), literalsToAugment);
    }

    /**
     * @param augmentedLiterals
     * @param literalsToAugment
     * @return a list containing the augmented literals list of literalsToAugment with augmentedLiterals in the front.
     */
    private List<List<Literal>> getAugmentedLiterals(List<Literal> augmentedLiterals, List<Literal> literalsToAugment) {
        List<List<Literal>> result = new LinkedList<>();
        if(literalsToAugment.isEmpty()){
            result.add(new LinkedList<>(augmentedLiterals));
        } else {
            Literal literalToAugment = literalsToAugment.remove(0);
            if(literalToAugment instanceof BuiltInLiteral){
                BuiltInLiteral bil = new BuiltInLiteral((BuiltInLiteral) literalToAugment);
                augmentedLiterals.add(bil);
                result = this.getAugmentedLiterals(augmentedLiterals, literalsToAugment);
                augmentedLiterals.remove(bil);
            } else {
                OrdinaryLiteral oliteral = (OrdinaryLiteral) literalToAugment;
                if(oliteral.isPositive()){
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
            literalsToAugment.add(0,literalToAugment);
        }
        return result;
    }

    /**
     * 
     * @param oliteral
     * @return a copy of the given literal changing its predicate to its insertion predicate
     */
    private OrdinaryLiteral getInsertion(OrdinaryLiteral oliteral) {
        return getEvent(EventType.INSERT, oliteral);
    }
    
    /**
     * 
     * @param oliteral
     * @return a copy of the given literal changing its predicate to its deletion predicate
     */
    private OrdinaryLiteral getDeletion(OrdinaryLiteral oliteral) {
        return getEvent(EventType.DELETE, oliteral);
    }

    /**
     * 
     * @param eventType
     * @param oliteral
     * @return a copy of the given literal changing its predicate to its corresponding event predicate
     */
    private OrdinaryLiteral getEvent(EventType eventType, OrdinaryLiteral oliteral) {
        Predicate pred = this.augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(eventType, oliteral.getPredicate()));
        assert pred!=null:"We could not find the "+eventType+ " predicate for "+oliteral;
        return new OrdinaryLiteral(new Atom(pred, oliteral.getTermsCopied()));
    }

    /**
     * Augments each derivation rule of the input schema. That is, given P a derived predicate,
     * the method creates in the augmetnedSchema the rules for ins_P, del_P and P'. The method
     * creates the predicate P' if necessary.
     */
    private void augmentDerivationRules() {
        for(DerivationRule derivationRule: this.inputLogicSchema.getAllDerivationRules()){
            Predicate predicate = derivationRule.getPredicate();
            String postPredicateName = predicate.getName()+"'";
            Predicate postPredicate = this.augmentedSchema.getPredicate(postPredicateName);
            if(postPredicate == null){
                postPredicate = new PredicateImpl(postPredicateName, derivationRule.getHead().getTerms().size());
                this.augmentedSchema.addPredicate(postPredicate);
            }
            
            //Creating the rules for ins_P and P'
            Atom head = derivationRule.getHead();
            Predicate insPredicate = augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(EventType.INSERT, predicate));
            Atom postHead = new Atom(postPredicate, head.getTermsCopied());
            for(List<Literal> postBody: this.getAugmentedLiterals(derivationRule.getLiterals())){
                new DerivationRule(new Atom(postHead), postBody);
                
                Atom insHead = new Atom(insPredicate, head.getTermsCopied());
                List<Literal> insBody = new LinkedList<>();
                for(Literal lit: postBody){
                    insBody.add(lit.copy());
                }
                insBody.add(new OrdinaryLiteral(new Atom(head), false));
                if(this.getHasPositiveEvent(insBody)){
                    new DerivationRule(insHead, insBody);
                }
            }
            
            //Creating the rules for del_P
            Predicate delPredicate = augmentedSchema.getPredicate(EventPredicate.getPredicateNameFor(EventType.DELETE, predicate));
            for(int i = 0; i < derivationRule.getLiterals().size(); ++i){
                Literal literal = derivationRule.getLiterals().get(i);
                if(literal instanceof OrdinaryLiteral){
                    Atom delHead = new Atom(delPredicate, head.getTermsCopied());

                    List<Literal> delBody = new LinkedList<>(derivationRule.getLiteralsCopied().subList(0, i));
                    OrdinaryLiteral oliteral = (OrdinaryLiteral) literal;
                    
                    if(oliteral.isPositive()){
                        delBody.add(this.getDeletion(oliteral));
                    }else {
                        delBody.add(this.getInsertion(oliteral));
                    }
                    
                    delBody.addAll(derivationRule.getLiteralsCopied().subList(i+1, derivationRule.getLiterals().size()));
                    delBody.add(new OrdinaryLiteral(postHead, false));
                    new DerivationRule(delHead, delBody);
                }
            }
        }
    }

    /**
     * @param body
     * @return true iff body contains some positive event ordinary literal
     */
    private boolean getHasPositiveEvent(List<Literal> body) {
        boolean positiveEventFound = false;
        for (Iterator<Literal> it = body.iterator(); it.hasNext() && !positiveEventFound;) {
            Literal lit = it.next();
            if(lit instanceof OrdinaryLiteral){
                OrdinaryLiteral olit = (OrdinaryLiteral) lit;
                positiveEventFound = positiveEventFound || (olit.isPositive() && olit.getPredicate() instanceof EventPredicate);
            }
        }
        return positiveEventFound;
    }

    @Override
    public void execute() {
        this.augment();
    }

    @Override
    public LogicSchema getOutputSchema() {
        return this.getAugmentedLogicSchema();
    }
}
