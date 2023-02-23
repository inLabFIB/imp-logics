# User Guide

IMP logics is the implementation of the metamodel of Datalog extended with negation and built-in literals. This
manual assumes that the user is comfortable with the traditional logic concepts of Term, Variable, Constant,
Atom, Literal, OrdinaryLiteral, Built-in-Literal, NormalClause, LogicConstraint, and DerivationRule.

By contract, almost all IMP Logics entities are immutable. For instance, if we have the literal "Emp(x)" and apply a
substitution to obtain "Emp(John)",
we are obtaining a new object "Emp(John)", rather than modifying the original "Emp(x)".

Users of IMP logics can freely create the instances of the metamodel as they wish. For instance, they can freely
create Terms, Atoms, Literals, etc. and manage them manually.

However, to better manage such objects, we strongly recommend the usage of LogicSchema.

Structurally, a LogicSchema is a set of:

- Predicates, which might be derived, or base
- LogicConstraints (a subclass of NormalClause)

A logic schema bounds all such objects together and ensures their consistency. That is:

- PredicateKey: There are no 2 predicates with the same name in the logic schema
- LogicConstraintKey: There are no 2 logic constraints with the same constriantID
- PredicateClosure: All derived predicates and logic constraints are defined using predicates that belongs to the schema

## Instantiating a logic schema

To better instantiate a logic schema, we provide two mechanisms:

- LogicSchemaBuilder
- LogicSchemaFactory

### LogicSchemaBuilder

The LogicSchemaBuilder is meant to create the LogicSchema incrementally. That is, it offers some operations to add
predicates, logic constraints, and derivation rules into it, and the LogicSchemaBuilder immediately checks their
consistency. E.g. a LogicSchemaBuilder will throw an error if trying to add a LogicConstraint `:-P(), P(x)`
since predicate `P` is being used as a 2-ary and 3-ary predicate at the same time.

To add such entities, the LogicSchemaBuilder receives as input some specification classes `PredicateSpec`,
`LogicConstraintSpec` and `DerivationRuleSpec`. Such classes are Value Objects used to specify the real
entities `Predicate`,
`LogicConstraint` and `DerivationRule` that the user wants to create.

With regards to the `LogicConstraintSpec`, the user can decide to instantiate `LogicConstraintWithIDSpec`
or `LogicConstraintWithoutIDSpec`.
In the first case, the user is forced to specify the ID he wants to use for the constraints, whereas in the second, the
user cannot specify
any ID.

The user cannot create a logic schema using both, `LogicConstraintWithIDSpec` and `LogicConstraintWithoutIDSpec`, hence
the LogicSchemaBuilder is type parametrized to ensure so, and force the user take a decision when instantiating the
builder.

Finally, to facilitate the creation of such specification classes, we provide some builders and helpers:

- StringToTermSpecFactory: it is used to specify
- ... //TODO

//TODO: add code examples

### LogicSchemaFactory

//TODO


