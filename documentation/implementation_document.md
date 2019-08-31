
# Performance

During performance tests, I selected three different skill levels for the AIs, each given a different thinking time to find the optimal move. This is the only difference between them. The thinking times are given in iterations. For example, an AI with 100 thinking time does 100 game simulations every time before doing a move. I used the following AI levels:

- Level 1: 250 iterations
- Level 2: 1000 iterations
- Level 3: 5000 iterations

All data have been gathered from the exact same games (everything was measured at the same time, not seperately).

Below are the average times consumed in making a single move:

- Level 1: 64,24 ms
- Level 2: 254,88 ms
- Level 3: 1154,80 ms

Looking at the times, it seems that the time used to make a single move increases linearly with the iteration count. This makes sense considering the theoretical time complexity of the Monte Carlo tree search.

Below is a table that shows the average **round** win percentages of different AI matchups. The win percentages are from the perspective of the levels in the rows. For example, on the first row are the win percentages of Level 1 AI vs the AI levels in the columns.

||Level 1|Level 2|Level 3|
|---|---|---|---|
|**Level 1**|-|44,66%|32,20%|
|**Level 2**|55,34%|-|42,21%|
|**Level 3**|67,80%|57,79%|-|

From this table we can see that a higher iteration count results in better play per round.

Below is a table that shows the average **game** win percentages of different AI matchups. Note that the win percentages here are not very accurate due to the somewhat low amount of completed games. They are accurate enough to give a decent estimate, though.

||Level 1|Level 2|Level 3|
|---|---|---|---|
|**Level 1**|-|12,12%|0%|
|**Level 2**|87,88%|-|26,67%|
|**Level 3**|100%|73,33%|-|

Here we can see perhaps interestingly that while the single round win percentages were somewhat close between the different AI levels, the better AIs dominate their lesser counterparts when looking at the game win percentages. The level 3 AI did not lose once to the level 1 AI in 20 games. Apparently the AI doesn't really care about losing individual rounds as long as it doesn't give the enemy too many points while doing so. This makes sense because obviously winning the whole game matters in the end.