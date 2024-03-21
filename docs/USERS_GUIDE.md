# User Guide

IMP logics is the implementation of the metamodel of Datalog extended with negation and built-in literals. This
manual assumes that the user is comfortable with the traditional logic concepts of Term, Variable, Constant,
Atom, Literal, OrdinaryLiteral, Built-in-Literal, NormalClause, LogicConstraint, and DerivationRule.

By contract, almost all IMP Logics entities are immutable. For instance, if we have the literal "Emp(x)" and apply a
substitution to obtain "Emp(John)", we are obtaining a new object "Emp(John)", rather than modifying the original "Emp(
x)".
Since, transformations over logic objects generates new logic objects, a user can apply as many transformations as
he/she
wants without any side effect on its originally created objects.

Users of IMP logics can freely create the instances of the metamodel as they wish. For instance, they can freely
create Terms, Atoms, Literals, etc. and manage them manually.

However, to better manage such objects, we strongly recommend the usage of a LogicSchema.

Structurally, a LogicSchema is a set of:

- Predicates, which might be derived, or base
- LogicConstraints (a subclass of NormalClause)

A logic schema bounds all such objects together and ensures their consistency. That is:

- PredicateKey: There are no 2 predicates with the same name in the logic schema
- LogicConstraintKey: There are no 2 logic constraints with the same constraintID
- PredicateClosure: All derived predicates and logic constraints are defined using predicates that belongs to the schema

## Instantiating a logic schema

To better instantiate a logic schema, we provide three mechanisms:

- LogicSchemaParser: parses a String codifying a logic schema according to some grammar
- LogicSchemaBuilder: programmatically creates, in an incremental fashion, a logic schema
- LogicSchemaFactory: programmatically creates, in a single step, a logic schema

To better grasp the intuition between the three methods, we provide an easy example for each of them.

Example of usage of a LogicSchemaParser:

```java
String schemaString="""
       :- Dept(D), not(MinOneEmp(E))
       MinOneEmp(D) :- Empd(E, D), Happy(E)
    """;

LogicSchema logicSchema = new LogicSchemaWithoutIDsParser().parse(schemaString);
```

Example of usage of a LogicSchemaBuilder:

```java
LogicSchemaBuilder<LogicConstraintWithoutIDSpec> logicSchemaBuilder=LogicSchemaBuilder.defaultLogicSchemaWithoutIDsBuilder();

LogicConstraintWithoutIDSpec logicConstraint = new LogicConstraintWithoutIDSpecBuilder()
   .addOrdinaryLiteral("Dept","D")
   .addNegatedOrdinaryLiteral("MinOneEmp","D")
   .build();

logicSchemaBuilder.addLogicConstraint(logicConstraint);
        
DerivationRuleSpec derivationRule = new DerivationRuleSpecBuilder()
    .addHead("MinOneEmp","D")
    .addOrdinaryLiteral("Emp","E","D")
    .build();

logicSchemaBuilder.addDerivationRule(derivationRule);
        
LogicSchema logicSchema = logicSchemaBuilder.build();
```

Example of usage of a LogicSchemaFactory:

```java
LogicConstraintWithoutIDSpec logicConstraint = new LogicConstraintWithoutIDSpecBuilder()
   .addOrdinaryLiteral("Dept","D")
   .addNegatedOrdinaryLiteral("MinOneEmp","D")
   .build();

logicSchemaSpec.addLogicConstraint(logicConstraint);

DerivationRuleSpec derivationRule = new DerivationRuleSpecBuilder()
  .addHead("MinOneEmp","D")
  .addOrdinaryLiteral("Emp","E","D")
  .build();

logicSchemaSpec.addDerivationRule(derivationRule);

LogicSchema logicSchema = LogicSchemaFactory.defaultLogicSchemaWithoutIDsFactory().createLogicSchema(logicSchemaSpec);
```

### How to use the LogicSchemaBuilder and LogicSchemaFactory?

We recommend using the `LogicSchemaBuilder` and `LogicSchemaFactory` on the following fashion:

1. Instantiate all the logic constraints and derivation rules you like, in the order you like
2. Add the predicates that are not appearing in the logic constraints and derivation rules through the `addPredicate`
   operation (`LogicSchemaBuilder::addPredicate`, or `LogicSchemaSpecification::addPredicate`). The other predicates
   are automatically managed by the builder/factory and hence, you do not require to specify them.

### How to manage the logic constraint IDs?

Every logic constraint has an ID that permits identifying it. You can decide to create such identifiers manually,
or delegate IMP logics to decide them for you.

#### Managing the logic constraint IDs manually

If you want to manage the IDs manually, you should use:

- LogicSchemaParserWithIDs in case you are parsing.
- LogicSchemaBuilder<LogicConstraintWithIDSpec> in case you are using the builder.
- LogicSchemaFactory<LogicConstraintWithIDSpec> in case you are using the factory.

If you are using the LogicSchemaParserWithIDs, every constraint should be preceded by '@ID'.
E.g. "@1 :- Dept(D), not(MinOneEmp(D))"

If you are using the factory, or the builder, you might be interested in using the operation
`LogicConstraintWithIDSpecBuilder::addConstraintId` to add constraint ids to your logic constraint specifications.

#### Managing the logic constraint through IMP logics

If you want to manage the IDs automatically, you should use:

- LogicSchemaParserWithoutIDs in case you are parsing.
- LogicSchemaBuilder<LogicConstraintWithoutIDSpec> in case you are using the builder.
- LogicSchemaFactory<LogicConstraintWithoutIDSpec> in case you are using the factory.

By default, IMP logics will use consecutive numbers, starting form 1, to identify your constraints (e.g. 1, 2, 3, ...)
However, such strategy can be overridden by providing a new implementation of the class `ConstraintIDGenerator`.

### Creating constants or variables

When using the parser, and the builders, IMP logics will, by default, interpret that:

- Constants are specified with numbers and (single or doubled) quoted strings. E.g.: 1, 2, "Socrates", 'Plato'...
- Variables are the rest of strings

However, such strategy can be overridden by providing a new implementation of the class `StringToTermSpecFactory`.

## Applying processes to the Schema

There are several processes that, given some logic schema, returns a new logic schema. Such processes
ranges from unfolding the schema, to a process to remove equalities and replace them for the corresponding
substitution. The idea of the processes is to retrieve an equivalent logic schema but with some transformation
that makes it easier to work with.

We distinguish between two kinds of services. Those which retrieves a query-equivalent logic schema, and those
which retrieves a consistent-equivalent logic schema.

We refer as `query-equivalent logic schema` to some new logic schema `S` s.t., for any query `q` posed
over the original schema, `q` brings the very same result in `S`. This means that a query-equivalent logic
schema has the very same predicates (base and derived) than the original schema, since a query might be posed
over such predicates. On the following we list such processes:

- BodySorter
- EqualityReplacer
- SchemaUnfolder
- TrivialLiteralCleaner

The complete specification of each process is given in the corresponding Javadocs.

We refer as `consistent-equivalent logic schema` to some new logic schema `S` s.t., for any set of base
facts posed over the original schema, such set of facts is consistent in `S` iff it was consistent in the original
schema. This means that a consistent-equivalent logic schema might have fewer predicates than the original schema
(i.e., those predicates and derivation rules not used in any logic constraint might disappear). On the following we
list such processes, together the reason they are not query-equivalent:

- PredicateCleaner: it removes those predicates not used (neither directly, nor transitively) in the logic constraints
- SingleDerivationRuleTransformer: it replaces the derived predicates with multiple derivation rules for several
  derived predicates with single derivation rules.

The complete specification of each process is given in the corresponding Javadocs.

### Using pipelines

We can apply several logic processes to a logic schema through a LogicProcessPipeline. E.g.

```java
List<LogicProcess> logicProcesses=List.of(new BodySorter(),new PredicateCleaner());
        LogicProcessPipeline pipeline=new LogicProcessPipeline(logicProcesses);
        LogicSchema logicSchemaOutput=pipeline.execute(schema);
```

### Using SchemaTransformation

Similarly to the logic schema process, there is the concept of logic schema transformation process.
A `LogicSchemaTransformationProcess` is a logic process that, additionally, permits storing some traceability
between the transformation in a new object we call `SchemaTransformation`.

More details can be found in the Javadocs.