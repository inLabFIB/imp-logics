package edu.upc.fib.inlab.imp.kse.logics.dependencies;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.StickyMarkingAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.EGDToFDAnalysisResult;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.EGDToFDAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.services.analyzers.egds.NonConflictingFDsAnalyzer;
import edu.upc.fib.inlab.imp.kse.logics.dependencies.visitor.DependencySchemaVisitor;
import edu.upc.fib.inlab.imp.kse.logics.schema.*;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateIsNotDerived;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateNotExists;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.PredicateOutsideSchema;
import edu.upc.fib.inlab.imp.kse.logics.schema.exceptions.RepeatedPredicateName;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A dependency schema bounds several predicates and dependencies together guaranteeing their consistency.
 */
public class DependencySchema {

    /**
     * Invariants:
     * <ul>
     *     <li>We cannot have two predicates with the same name</li>
     *     <li>All predicates used in the dependencies appear in the predicates set</li>
     *     <li>All dependencies are defined through predicates of the dependencySchema</li>
     *     <li>All derivation rules defining predicates are included in this dependencySchema</li>
     * </ul>
     */
    private final Map<String, Predicate> predicatesByName = new HashMap<>();
    private final Set<Dependency> dependencies = new LinkedHashSet<>();

    public DependencySchema(Set<Predicate> predicates, Set<Dependency> dependencies) {
        predicates.forEach(predicate -> {
            if (predicatesByName.containsKey(predicate.getName())) throw new RepeatedPredicateName(predicate.getName());
            this.predicatesByName.put(predicate.getName(), predicate);
        });

        dependencies.forEach(d -> {
            checkPredicatesBelongsToSchema(d);
            this.dependencies.add(d);
        });

        checkDerivedPredicatesUsesPredicatesFromSchema();
    }

    private void checkDerivedPredicatesUsesPredicatesFromSchema() {
        for (Predicate p : predicatesByName.values()) {
            if (p.isDerived()) {
                p.getDerivationRules().forEach(this::checkPredicatesBelongsToSchema);
            }
        }
    }

    private void checkPredicatesBelongsToSchema(NormalClause c) {
        for (Literal l : c.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    private void checkPredicatesBelongsToSchema(Dependency d) {
        for (Literal l : d.getBody()) {
            if (l instanceof OrdinaryLiteral ol) {
                Predicate predicateFromConstraint = ol.getAtom().getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
        if (d instanceof TGD tgd) {
            for (Atom atom : tgd.getHead()) {
                Predicate predicateFromConstraint = atom.getPredicate();
                Predicate predicateFromSchemaWithSameName = predicatesByName.get(predicateFromConstraint.getName());
                if (predicateFromConstraint != predicateFromSchemaWithSameName)
                    throw new PredicateOutsideSchema(predicateFromConstraint.getName());
            }
        }
    }

    public Set<Predicate> getAllPredicates() {
        return new LinkedHashSet<>(predicatesByName.values());
    }

    public Predicate getPredicateByName(String predicateName) {
        if (!predicatesByName.containsKey(predicateName)) {
            throw new PredicateNotExists(predicateName);
        }
        return predicatesByName.get(predicateName);
    }

    public List<DerivationRule> getDerivationRulesByPredicateName(String derivedPredicateName) {
        if (!predicatesByName.containsKey(derivedPredicateName)) throw new PredicateNotExists(derivedPredicateName);

        Predicate predicate = predicatesByName.get(derivedPredicateName);
        if (predicate.isDerived()) {
            return predicate.getDerivationRules();
        } else throw new PredicateIsNotDerived(derivedPredicateName);
    }

    public Set<DerivationRule> getAllDerivationRules() {
        return predicatesByName.values().stream()
                .filter(Predicate::isDerived)
                .flatMap(p -> p.getDerivationRules().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Predicate> getAllDerivedPredicates() {
        return predicatesByName.values().stream()
                .filter(Predicate::isDerived)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Dependency> getAllDependencies() {
        return new LinkedHashSet<>(dependencies);
    }

    public List<TGD> getAllTGDs() {
        List<TGD> result = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            if (dependency instanceof TGD tgd) {
                result.add(tgd);
            }
        }
        return result;
    }

    public List<EGD> getAllEGDs() {
        return dependencies.stream()
                .filter(EGD.class::isInstance)
                .map(d -> (EGD) d)
                .toList();
    }


    public <T> T accept(DependencySchemaVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isEmpty() {
        return predicatesByName.isEmpty() && dependencies.isEmpty();
    }

    public boolean isLinear() {
        if (!this.areEGDsNonConflictingWithTGDs()) return false;

        return this.getAllTGDs().stream().allMatch(TGD::isLinear);
    }

    public boolean isGuarded() {
        return this.dependencies.stream()
                .map(d -> {
                    if (d instanceof TGD tgd) return tgd.isGuarded();
                    else return false;
                })
                .reduce(true, (a, b) -> a && b);
    }

    public boolean isSticky() {
        Set<LiteralPosition> stickyMarking = StickyMarkingAnalyzer.getStickyMarking(getAllTGDs());

        for (TGD tgd : this.getAllTGDs()) {
            if (hasVariableAppearingTwiceInMarkedPositions(tgd, stickyMarking)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasVariableAppearingTwiceInMarkedPositions(TGD tgd, Set<LiteralPosition> finalMarking) {
        for (Variable variable : tgd.getUniversalVariables()) {
            Set<LiteralPosition> positionsWithVar = tgd.getBody().getLiteralPositionWithVariable(variable);
            positionsWithVar.retainAll(finalMarking);
            if (positionsWithVar.size() > 1) return true;
        }
        return false;
    }


    public boolean isWeaklyGuarded() {
        Set<PredicatePosition> affectedPositions = this.getAffectedPositions();
        for (TGD tgd : this.getAllTGDs()) {
            if (!isWeaklyGuarded(tgd, affectedPositions)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method implements the affected positions definition
     * given in IEEE Symposion on Logic in Computer Science 2010
     * "Datalog+/-: A Family of Logical Knowledge Representation
     * and Query Languages for New Applications" by Cali, et al.
     *
     * @return those predicate positions that might contain null values
     * when chasing the schema dependencies.
     */
    public Set<PredicatePosition> getAffectedPositions() {
        Set<PredicatePosition> positionsWithExistsVars = getPositionsWithExistentialVars();
        return getAffectedPositions(positionsWithExistsVars);
    }

    private Set<PredicatePosition> getPositionsWithExistentialVars() {
        Set<PredicatePosition> result = new HashSet<>();
        for (TGD tgd : getAllTGDs()) {
            Set<Variable> existentialVariables = tgd.getExistentialVariables();
            if (existentialVariables.isEmpty()) continue;

            for (Atom headAtom : tgd.getHead()) {
                for (int position = 0; position < headAtom.getPredicate().getArity(); ++position) {
                    Term term = headAtom.getTerms().get(position);
                    if (term instanceof Variable variable && existentialVariables.contains(variable)) {
                        result.add(new PredicatePosition(headAtom.getPredicate(), position));
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method computes the affected positions of this schema by
     * saturating the set of affectedPositions.
     * That is, it recursively keeps adding predicatePositions to
     * affectedPositions until no more predicatePositions can be
     * added. When no more predicatePositions can be added, the algorithm
     * finishes
     *
     * @param affectedPositions not null
     * @return the set of affected positions given the initial set of affected positions
     */
    private Set<PredicatePosition> getAffectedPositions(Set<PredicatePosition> affectedPositions) {
        Set<PredicatePosition> newAffectedPositions = new HashSet<>(affectedPositions);
        for (Dependency dependency : this.dependencies) {
            if (dependency instanceof TGD tgd) {
                Set<Variable> frontierVariables = tgd.getFrontierVariables();
                for (Variable variable : frontierVariables) {
                    Set<PredicatePosition> bodyPositions = tgd.getBody().getPredicatePositionsWithVar(variable);
                    if (affectedPositions.containsAll(bodyPositions)) {
                        newAffectedPositions.addAll(tgd.getHead().getPredicatePositionsWithVar(variable));
                    }
                }
            }
        }
        if (!affectedPositions.containsAll(newAffectedPositions)) return getAffectedPositions(newAffectedPositions);
        else return newAffectedPositions;
    }

    /**
     * @param tgd               not null
     * @param affectedPositions not null, might be empty
     * @return whether the given tgd is weakly acyclic according to the given set of affected positions
     */
    boolean isWeaklyGuarded(TGD tgd, Set<PredicatePosition> affectedPositions) {
        Set<Variable> universalVars = tgd.getUniversalVariables();
        List<Variable> affectedVars = universalVars.stream().filter(u ->
                        affectedPositions.containsAll(tgd.getBody().getPredicatePositionsWithVar(u)))
                .toList();

        //Searching the guard
        for (Literal lit : tgd.getBody()) {
            if (lit instanceof OrdinaryLiteral &&
                    lit.getTerms().containsAll(affectedVars)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Method responsible to check if the set of EGDs is non-conflicting from the set of TGDs according to
     * the paper "Datalog+/-: A Family of Logical Knowledge Representation and Query Languages for
     * New Applications" published in 2010 25th Annual IEEE Symposium on Logic in Computer Science
     *
     * @return whether the egds of this schema are non-conflicting with the TGDs
     */
    public boolean areEGDsNonConflictingWithTGDs() {
        EGDToFDAnalysisResult egdToFDAnalysisResult = new EGDToFDAnalyzer().analyze(getAllEGDs());

        if (egdToFDAnalysisResult.allEGDsDefinesKeyDependencies()) {
            return new NonConflictingFDsAnalyzer().isNonConflicting(getAllTGDs(), egdToFDAnalysisResult.getFunctionalDependencies());
        } else return false;
    }
}
