package edu.upc.imp.logics.services.comparator;

public class ExtendedHomomorphismFinder extends HomomorphismFinder {

    public ExtendedHomomorphismFinder() {
        super(new DefaultDerivedLiteralHomomorphismCriteria());
    }

}
