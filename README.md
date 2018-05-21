# vehicle-booking-seqs-optimizer
### Summary
Implementation of algorithm sorting vehicle bookings in sequential order to minimize relocations of vehcle between bookings.<br><br>
By other words, groups the arrows of a directed multi-arrow graph into a SHORTEST possible list of uninterrupted paths without repeating arrows, i.e. each arrow can be included only into one identified path.<br>

Input JSON file has the following format:<br>

```
[
  {
    "id": 1,
    "start": 3,
    "end": 1
  },
  {
    "id": 2,
    "start": 1,
    "end": 3
  }
]
```

### Terminology
**Vertex/Vertices** is a node (nodes) of a graph.<br>
**Arrow** is a directed edge of a graph.<br>
**Path** is the route in the graph as presented here:<br>
```
   Vi0 -[Ae0]-> Vi1 -[Ae1]-> Vi2 -[Ae2]-> ... Vi(n-1) -[Aen-1]-> Vin   (n - length of the path)
     ...
   Vk0 -[Af0]-> Vk1 -[Af1]-> Vk2 -[Af2]-> ... Vk(m-1) -[Afm-1]-> Vim   (m - length of the path)
```
**Cycle** is a loop path in a graph where each arrow is visited only once.<br>
**Vertex degree** number equal to the difference of number of outbound arrows and number of inbound arrows for the vertex.<br>

### Conditions
**Condition 1:** the graph can contain more than 1 arrow connecting Vi -> Vj.<br>
**Condition 2:** the result paths are not necessarily longest, however deepest possible, i.e. for any path its last vertix doesn't contain any other available outbound arrows connecting it with another vertex. <br>
**Condition 3:** the ids of arrows should be sequential without gaps.<br>

### Algorithm

The algorithm includes the following major steps:

1. For every vertex having positive degree Di, build any Di possible paths Pi which start from that vertex, and on every step exclude arrows composing found paths from the graph. It doesn't matter which path to pick, but important is that the path should be ending on a vertex which doesn't have any more outbound arrows<p>
2. As we excluded all possible paths starting from positive degree vertices, in the result there will only zero degree vertices left.
3. In the reduced graph while possible, find all possible random paths starting from all available vertices with degree=0. All those paths are cycles <p>
4. Attempt inserting the cycles found on step 3 into the identified paths found on step 1, rolling cycles if needed. <p>
5. The new paths with inserted cycles and the cycles which are not inserted into the paths (isolated cycles) form the list of all found paths.<br>
**Additional steps:**
Before the graph algorithm start, convert all bookings into the arrows of the graph and assign sequential numbers to them for fater performance of the algorithm.
The result paths of the graph needs to be converted into the booking sequence.

### Building and execution
To build the project simply call the command:
```
./gradlew clean fatJar
```
To run the project:
```
java -jar /build/libs/vehicle-booking-seqs-optimizer-<version>.jar <input json path> <output json path>
```
Default output file is output.json.
