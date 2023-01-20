# Need

Local-date + local-time + local-tz -> Java-Date

# Given

X {A, B, C} where A:local-date, B:local-time, C:local-tz

Y is a java-date (what we want)

# Soln

Get all paths between X->Y

P1: X->A->D(A,B)->E(D,C)->F->Y
P2: X->B->D(A,B)->E(D,C)->F->Y
P3: X->C->E(D,C)->F->Y

Then check each path to determine best route and whether route is valid

So each type will be a node
i.e. foo.bar.int

Each edge will represent a fn that can convert from one node to another, listing other nodes needed?

or 

N{type:local-date} -E{fn:f1, position:0, length:2}-> N{type:local-datetime}
N{type:local-time} -E{fn:f1, position:1, length:2}-> N{type:local-datetime}
