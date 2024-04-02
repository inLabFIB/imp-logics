package edu.upc.fib.inlab.imp.kse.logics.dependencyschema.mothers;

import edu.upc.fib.inlab.imp.kse.logics.dependencyschema.domain.TGD;

public class TGDMother {
    public static TGD createTGD(String tgdString) {
        return (TGD) DependencySchemaMother.buildDependencySchema(tgdString).getAllDependencies().iterator().next();
    }
}
