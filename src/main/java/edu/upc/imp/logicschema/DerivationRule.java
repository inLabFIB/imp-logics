package edu.upc.imp.logicschema;

import java.util.*;

/**
 * Implementation of a logic derivation rule. That is, a NormalClause with head
 *
 */
public class DerivationRule extends NormalClause {
    private final Atom head;

    private DerivationRule(Atom head, List<Literal> body, boolean addToPredicate) {
        super(body);
        assert head != null;
        this.head = head;
        if (addToPredicate) this.head.getPredicate().addDerivationRule(this);
    }

    /**
     * Constructs a derivation rule, using the given head and body.
     * It also inserts this as a definitionRule of the head's predicate.
     *
     * @param head != null
     */
    public DerivationRule(Atom head, List<Literal> body) {
        this(head, body, true);
    }

    /**
     * Constructs a new Derivation rule by taking the head of the given rule and the
     * given body literals
     */
    private DerivationRule(DerivationRule rule, List<Literal> body) {
        super(body);
        this.head = rule.head;
    }

    /**
     * Constructs a new Derivation rule using the given head as head and the
     * ordinaryLiteral as body
     *
     * @param head            != null
     * @param ordinaryLiteral != null
     */
    public DerivationRule(Atom head, OrdinaryLiteral ordinaryLiteral) {
        this(head, getLiteralAsLiteralsList(ordinaryLiteral));
    }

    private static List<Literal> getLiteralAsLiteralsList(OrdinaryLiteral lit) {
        LinkedList<Literal> result = new LinkedList<>();
        result.add(lit);
        return result;
    }

    public Atom getHead() {
        return head;
    }

    /**
     * Usefull operation for unfolding literals.
     * Given the atom callingAtom to unfold
     *
     * @param callingAtom            an atom that has the same predicate as the head of this
     * @return a copy of the List of Literals after substituting the head terms
     * with the terms of callingAtom, and all the other variables X with
     * another variable X+suffix in case they appear in forbiddenVariableNames
     * where X+suffix do not appear in forbiddenVariableNames
     */
    public List<Literal> applyCallSubstitution(Atom callingAtom, Set<String> forbiddenVariableNames) {
        assert head != null;
        assert head.getPredicate().getName().equals(callingAtom.getPredicate().getName());

        Iterator<Term> headTerms = this.head.getTerms().iterator();
        Iterator<Term> callingTerms = callingAtom.getTerms().iterator();

        Set<String> currentForbiddenVariableNames = new HashSet<>(forbiddenVariableNames);
        Map<String, String> substitution = new HashMap<>();

        while (headTerms.hasNext() && callingTerms.hasNext()) {
            Term headTerm = headTerms.next();
            Term callingTerm = callingTerms.next();
            substitution.put(headTerm.getName(), callingTerm.getName());
        }
        assert headTerms.hasNext() == callingTerms.hasNext();

        for (String termName : this.getVariablesNames()) {
            if (!substitution.containsKey(termName)) {
                String termNameReplacement = termName;
                int i = 0;
                while (currentForbiddenVariableNames.contains(termNameReplacement)) {
                    termNameReplacement = termName + "_" + i++;
                }
                currentForbiddenVariableNames.add(termNameReplacement);
                substitution.put(termName, termNameReplacement);
            }
        }

        LinkedList<Literal> resultBody = new LinkedList<>();
        for (Literal ol : this.getLiterals()) {
            resultBody.add(ol.getLiteralAfterSubstitution(substitution));
        }

        return resultBody;
    }

    @Override
    protected String getHeadAsString() {
        return "  " + head.toString();
    }

    public String getPredicateName() {
        return this.head.getPredicateName();
    }

    @Override
    protected int hashCodeSpecific() {
        return this.head.hashCode();
    }

    @Override
    protected Map<String, String> getVariableToVariableSubstitutionForHead(NormalClause nc) {
        if (nc instanceof DerivationRule) {
            DerivationRule derivationRule = (DerivationRule) nc;
            return this.head.getVariableToVariableUnification(derivationRule.getHead(), new HashMap<>());
        }
        return null;
    }

    @Override
    public Set<String> getVariableNamesInHead() {
        return this.head.getVariablesNames();
    }

    @Override
    public NormalClause copyChangingBody(List<Literal> bodyCopy) {
        return new DerivationRule(this, bodyCopy);
    }

    public Predicate getPredicate() {
        return this.getHead().getPredicate();
    }
}
