# User Guide

IMP logics is the implementation of the metamodel of Datalog extended with negation and built-in literals. This
manual assumes that the user is comfortable with the traditional logic concepts of Term, Variable, Constant,
Atom, Literal, OrdinaryLiteral,  Built-in-Literal, NormalClause, LogicConstraint, and DerivationRule.

By contract, all IMP Logics entities are inmutable. For instance, if we have the literal "Emp(x)" and apply a substitution to obtain "Emp(John)",
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




