package edu.upc.imp.logics.schema.utils;

import edu.upc.imp.logics.schema.Predicate;

/**
 * <p> This class implements the definition of hierarchical database as defined in "Basis for Deductive Database Systems"
 * by J. W. Lloyd AND R. W. Topor.</p>
 *
 * <p>A database is called hierarchical if its predicates can be partitioned into
 * levels so that the definitions of level 0 predicates consist solely of base predicates
 * and the bodies of the derived predicates of level j contain only level i predicates, where i <j. </p>
 */
public class LevelHierarchy {

    public int getNumberOfLevels() {
        return -1;
    }

    public Level getLevel(int index) {
        return null;
    }

    public int getLevelIndexOfPredicate(Predicate p) {
        return -1;
    }

    public Level getLevelOfPredicate(Predicate p) {
        return null;
    }

}
