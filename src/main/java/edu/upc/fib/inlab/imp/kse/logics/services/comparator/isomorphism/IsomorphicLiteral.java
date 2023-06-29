package edu.upc.fib.inlab.imp.kse.logics.services.comparator.isomorphism;

import edu.upc.fib.inlab.imp.kse.logics.schema.Literal;

/**
 * Class to remember a literal together a termMap that prove that it is isomorphic to some other literal.
 * The termMap might map other terms not present in the given literal.
 *
 * @param literal not null
 * @param termMap not null
 */
record IsomorphicLiteral(Literal literal, TermMap termMap) {

}
