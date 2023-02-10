# User Guide

IMP logics is the implementation of the metamodel of Datalog extended with negation and built-in literals. This
manual assumes that the user is comfortable with the traditional logic concepts of Term, Variable, Constant,
Atom, Literal, OrdinaryLiteral,  Built-in-Literal, NormalClause, LogicConstraint, and DerivationRule.

Users of IMP logics can freely create the instances of the metamodel as they wish. For instance, they can freely
create Terms, Atoms, Literals, Derivation Rules, etc. and manage them manually.

However, to better manage such objects, we strongly recommend the usage of LogicSchema, and comply with some
programming guidelines we provide.

## Using the Logic Schema to manage the metamodel objects
Structurally, a LogicSchema is a set of:
- Predicates
- LogicConstraints (a subclass of NormalClause)
- DerivationRules (a subclass of NormalClause)

In terms of behavior, the LogicSchema is responsible for:
- Offering the capability to store/retrieve any of such objects
- Guaranteeing the consistency of these objects

### Storing and retrieving the objects from the LogicSchema

The LogicSchema offers operations to retrieve such objects:
- Predicates can be retrieved by name.
- LogicConstraints can be retrieved by id.
- (Sets of) DerivationRules can be retrieved by predicate name.

Hence, a user that creates Predicates, LogicConstraints, and DerivationRules is expected to insert them into a LogicSchema.

Do not that a user can create a DerivationRule defining some predicate P, but untill such DerivationRule is not added
in the schema, P still does not have such definition.

### Guaranteeing the consistency of the objects

The LogicSchema is meant to guarantee that all NormalClauses uses the Predicates from this Schema, and that any Predicate
can directly access all its DerivationRules.

To achieve so, when including a new NormalClause into the schema, the LogicSchema does the following:
- All the Predicates used in a NormalClause are included as Predicates. If some Predicate is not included
in the LogicSchema, it is inserted when inserting the NormalClause
- If some NormalClause uses a Predicate whose name coincides with some other Predicate of the LogicSchema, an exception
is thrown. This is used to avoid confusion between different Predicates (from different Schemas)
- When inserting a DerivationRule, such DerivationRule is inserted in the definition rules of the corresponding Predicate

## Programming guidelines

Terms are Value Objects, since they are immutable, can be reused among several constructions. E.g, a variable term "x" can
be reused in several atoms, or built-in literals.

Predicates can be reused to create several atoms inside the schema they belong to.
In fact, it is expected that, inside the same schema, all atoms with the same predicate name uses the very same Predicate object.

Atoms, and Literals should not be reused in several NormalClauses, neither twice in the same NormalClause.

The current Parser satisfies such rules, and it is expected that future versions of this library provides other
constructors that facilitates the creation of such objects satisfying the previous guidelines.

