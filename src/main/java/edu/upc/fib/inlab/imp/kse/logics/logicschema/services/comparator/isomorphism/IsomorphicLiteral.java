package edu.upc.fib.inlab.imp.kse.logics.logicschema.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.Literal;

/**
 * Class to remember a literal together a termMap that prove that it is isomorphic to some other literal. The termMap
 * might map other terms not present in the given literal.
 *
 * @param literal not null
 * @param termMap not null
 */
record IsomorphicLiteral(Literal literal, TermMap termMap) {

}
