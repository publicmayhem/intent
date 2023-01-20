# Given

Graph of nodes (Types) and Edges (Paths from type to types)

e.g.

N(DateInstant): -> N(LocalDateTime):    -> N(LocalDate)
                ..                      -> N(LocalTime)
                -> N(ZoneId)

# When

Given list of types #{ZoneId, LocalDate, LocalTime}
And Target type DateInstant

Query for all paths between Target->Each-Element-of{GivenList}
For Each Path such as ZoneId->DateInstant determine what else is needed -> LocalDateTime and repeat expansion with LocalDateTime being the new target

# Then

We have a list of paths between Target -> Each-Element-of{GivenList} fully expanded
Filter path list to most viable options (currently assume exact match only)
Either take first or shortest as solution

# Enhancements

To determine viability we could add loss of precision (String to Double etc) as a cost fn, then rank by cheapests

# Implementation

Try via graph query and via datalog query, datalog preferable as we can store both in memory or servers
