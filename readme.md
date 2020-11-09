# Load-Optimization in Reconfigurable Networks

This project contains an implementation and visualization of graph optimization algorithms discussed in [1].

This project is for a class on computer networks and distributed systems at ELTE, Hungary.

```
[1] Wenkai Dai, Klaus-Tycho Foerster, David Fuchssteiner, Stefan Schmid.
    2020. Load-Optimization in Reconfigurable Networks: Algorithms and Complexity of Flow Routing. 
    In PERFORMANCE ’20, November 02–06, 2020, Milan, Italy. ACM, New York, NY, USA, 14 pages. 
    https://doi.org/10.1145/123456789
```
## Building and running
The project uses Java 11, the [Maven](https://maven.apache.org/) build system, and depends on the [jgrapht](https://jgrapht.org/) library.

To compile the project along with all dependencies:

```shell script
mvn clean compile assembly:single
```

The jar including the dependencies will be generated to the `target/` directory. Run the program: 

``` 
java -jar target/reconfig-network-demo-1.0-SNAPSHOT-jar-with-dependencies.jar
```

The program's current version doesn't have inputs, please modify the source code to set the demands and the edges.
In a future version, these will be read from a file.

The program outputs to stdout in the `dot` format, you can use a [Graphviz tool](https://dreampuf.github.io/GraphvizOnline/)
to visualize the results.

 