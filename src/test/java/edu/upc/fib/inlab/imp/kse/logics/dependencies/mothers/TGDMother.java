package edu.upc.fib.inlab.imp.kse.logics.dependencies.mothers;

import edu.upc.fib.inlab.imp.kse.logics.dependencies.TGD;

public class TGDMother {
    public static TGD createTGD(String tgdString) {
        return (TGD) DependencySchemaMother.buildDependencySchema(tgdString).getAllDependencies().iterator().next();
    }
}
