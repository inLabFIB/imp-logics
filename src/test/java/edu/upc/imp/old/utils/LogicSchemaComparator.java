package edu.upc.imp.old.utils;

import edu.upc.imp.old.logicschema.*;

import java.util.HashSet;
import java.util.Set;

public class LogicSchemaComparator {

    private final LogicSchema originalLogicSchema;

    private final Set<Integer> logicConstraintIDs = new HashSet<>();
    private final Set<Integer> derivationRuleIDs = new HashSet<>();

    private final Set<Integer> literalIDs = new HashSet<>();
    private final Set<Integer> predicateIDs = new HashSet<>();

    private final Set<Integer> atomIDs = new HashSet<>();
//    private final Set<Integer> termIDs = new HashSet<>();

    public LogicSchemaComparator(LogicSchema originalLogicSchema) {
        this.originalLogicSchema = originalLogicSchema;
        storeOriginalLSObjects();
    }

    public boolean checkRepeatedObjectsWith(LogicSchema newLogicSchema) {
        for (LogicConstraint lc : newLogicSchema.getAllConstraints()) {
            if (checkID(lc)) return true;
            if (checkObjectsInNormalClauses(lc)) return true;
        }
        return false;
    }

    private boolean checkObjectsInNormalClauses(NormalClause nc) {
        if (nc instanceof DerivationRule) {
            if (checkID((DerivationRule) nc)) return true;
            if (checkID(((DerivationRule) nc).getHead())) return true;
        }
        for (Literal l : nc.getLiterals()) {
            if (checkID(l)) return true;
            if (l instanceof BuiltInLiteral) continue;
            OrdinaryLiteral ol = (OrdinaryLiteral) l;
            if (checkID(ol.getPredicate())) return true;
            for (DerivationRule dr : ol.getPredicate().getDefinitionRules())
                if (checkObjectsInNormalClauses(dr)) return true;
        }
        return false;
    }

    private void storeOriginalLSObjects() {
        for (LogicConstraint lc : originalLogicSchema.getAllConstraints()) {
            logicConstraintIDs.add(System.identityHashCode(lc));
            storeNormalClauseInfo(lc);
        }
    }

    private void storeNormalClauseInfo(NormalClause nc) {
        if (nc instanceof DerivationRule) {
            derivationRuleIDs.add(System.identityHashCode(nc));
            atomIDs.add(System.identityHashCode(((DerivationRule) nc).getHead()));
        }
        for (Literal l : nc.getLiterals()) {
            literalIDs.add(System.identityHashCode(l));
            if (l instanceof BuiltInLiteral) continue;
            OrdinaryLiteral ol = (OrdinaryLiteral) l;
            predicateIDs.add(System.identityHashCode(ol.getPredicate()));
            for (DerivationRule dr : ol.getPredicate().getDefinitionRules()) storeNormalClauseInfo(dr);
        }
    }


    private boolean checkID(LogicConstraint lc) {
        return logicConstraintIDs.contains(System.identityHashCode(lc));
    }

    private boolean checkID(DerivationRule dr) {
        return derivationRuleIDs.contains(System.identityHashCode(dr));
    }

    private boolean checkID(Literal l) {
        return literalIDs.contains(System.identityHashCode(l));
    }

    private boolean checkID(Predicate p) {
        return predicateIDs.contains(System.identityHashCode(p));
    }

    private boolean checkID(Atom a) {
        return atomIDs.contains(System.identityHashCode(a));
    }

//    private boolean checkID(Term t) {
//        return termIDs.contains(System.identityHashCode(t));
//    }
}
