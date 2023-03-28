package edu.upc.imp.logics.services.normalizer;

import edu.upc.imp.logics.schema.LogicSchema;

import java.util.Objects;

/**
 * This service normalizes a schema.
 * A schema is normalized when it has no positive derived ordinary literals, no derived predicate has more than 1 derivation
 * rule, all bodies of normal clauses are sorted, and all predicates are used in some constraint.
 */
public class Normalizer {
    private final SchemaUnfolder schemaUnfolder;
    private final SingleDerivationRuleTransformer singleDerivationRuleTransformer;
    private final BodySorter bodySorter;
    private final PredicateCleaner predicateCleaner;

    public Normalizer() {
        this(new SchemaUnfolder(), new SingleDerivationRuleTransformer(), new BodySorter(), new PredicateCleaner());
    }

    protected Normalizer(SchemaUnfolder schemaUnfolder, SingleDerivationRuleTransformer singleDerivationRuleTransformer, BodySorter bodySorter, PredicateCleaner predicateCleaner) {
        this.schemaUnfolder = schemaUnfolder;
        this.singleDerivationRuleTransformer = singleDerivationRuleTransformer;
        this.bodySorter = bodySorter;
        this.predicateCleaner = predicateCleaner;
    }

    /**
     * @param logicSchema a non-null schema
     * @return a normalized schema
     */
    public LogicSchema normalize(LogicSchema logicSchema) {
        if (Objects.isNull(logicSchema)) {
            throw new IllegalArgumentException("LogicSchema cannot be null");
        }
        LogicSchema unfoldedSchema = schemaUnfolder.unfold(logicSchema);
        LogicSchema singleDerivationRulesSchema = singleDerivationRuleTransformer.transform(unfoldedSchema);
        LogicSchema sortedSchema = bodySorter.sort(singleDerivationRulesSchema);
        return predicateCleaner.clean(sortedSchema);
    }
}
