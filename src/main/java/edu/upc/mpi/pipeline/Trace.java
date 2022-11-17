package edu.upc.mpi.pipeline;

import edu.upc.mpi.logicschema.LogicConstraint;

import java.util.LinkedList;
import java.util.List;

public class Trace {
    private final LinkedList<LogicConstraint> list;

    Trace(LogicConstraint lc) {
        list = new LinkedList<>();
        list.add(lc);
    }

    public void addPrevious(LogicConstraint lc) {
        list.addFirst(lc);
    }

    public List<LogicConstraint> getList() {
        return list;
    }
}
