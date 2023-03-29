package edu.upc.fib.inlab.imp.kse.logics.services.normalizer;

import edu.upc.fib.inlab.imp.kse.logics.schema.LogicSchema;

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
     * @param schema not null
     * @return a transformation of the given schema where the resulting schema has been normalized
     */
    public SchemaTransformation normalizeTransformation(LogicSchema schema) {
        if (Objects.isNull(schema)) {
            throw new IllegalArgumentException("LogicSchema cannot be null");
        }
        SchemaTransformation unfoldedSchema = schemaUnfolder.unfoldTransformation(schema);
        SchemaTransformation singleDerivationRulesSchema = singleDerivationRuleTransformer.transformTransformation(unfoldedSchema.transformed());
        SchemaTransformation sortedSchema = bodySorter.sortTransformation(singleDerivationRulesSchema.transformed());
        SchemaTransformation cleanTransformation = predicateCleaner.cleanTransformation(sortedSchema.transformed());

        SchemaTraceabilityMap schemaTraceabilityMap = SchemaTraceabilityMap.collapseMaps(
                unfoldedSchema.schemaTraceabilityMap(),
                singleDerivationRulesSchema.schemaTraceabilityMap(),
                sortedSchema.schemaTraceabilityMap(),
                cleanTransformation.schemaTraceabilityMap()
        );
        return new SchemaTransformation(schema, cleanTransformation.transformed(), schemaTraceabilityMap);
    }

    /**
     * @param logicSchema a non-null schema
     * @return a normalized schema
     */
    public LogicSchema normalize(LogicSchema logicSchema) {
        return normalizeTransformation(logicSchema).transformed();
    }


}
