# Definition document

The goal of the project is to create a Java program that is capable of playing the popular card game called Rummy against both itself and human players.

## Algorithms and data structures

### Information Set Monte Carlo tree search
The program will likely be using Information Set Monte Carlo tree search (ISMCTS), which is a variation of Monte Carlo tree search (MCTS). In MCTS, the different states of the game are represented as nodes and the possible moves a player can make that would cause the state of the game to change from A to B are represented as the "lines" between tree nodes A and B. The most promising leaf node is selected, and simulation of random move choices occurs until the game ends. The result is then propagated back the tree to every node so better decisions can be made in the future. Then more simulations happen for as long as time limit is reached.

The problem with MCTS is that in games with imperfect information, the number of possible moves from one state often grows very high (e.g. the AI doesn't know opponent's hand cards). The difference to normal MCTS is that in ISMCTS one node doesn't correspond to a single state, but to a set of states which look the same from AI's perspective. Because different states have different possible moves (e.g. opponent's plays), in ISMCTS a random determinization of hidden information from AI's perspective (e.g. opponents cards) is done in the root node for every simulation. This both reduces the brancing factor of the tree and makes it so that rarely possible moves are searched less often.


#### Time complexity
I believe the time complexity of ISMCTS is the same as in regular MCTS, O(mkl), where
- m = number of children of a node
- k = number of simulations of a child
- l = number of iterations


#### Space complexity
The space complexity is the same as in regular MCTS, O(mk), where 
- m = number of children of a node
- k = number of simulations of a child


## Sources

- https://en.wikipedia.org/wiki/Monte_Carlo_tree_search
- www.aifactory.co.uk/newsletter/2013_01_reduce_burden.htm
- http://stanford.edu/~rezab/dao/projects/montecarlo_search_tree_report.pdf