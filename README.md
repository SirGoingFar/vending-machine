# Vending Machine

#### Candidate Name: Akintunde Olanrewaju (<eaolanrewaju@gmail.com>)

## Tradeoffs made between performance, readability, maintainability, etc
### 1. Use of Volatile keyword
As the requirement document clarified (that multiple threads can access a single instance of a vending machine), it is important to ensure vending machine's state data (products & available coins) are kept consistent across all threads. However, this has an impact on the system performance.

The volatile keyword on the state data sends a strong warning to the compiler not to do code re-ordering (an optimization process that expidites code execution) and this will make the application to perform at the barest minimum execution optimization level. However, data consistency is a big deal in this type of application as the performance deficit is not noticeable when compared with the machine speed.

In addition, this keyword forbids the compiler writing vending machine state data into a cache, rather the read and write operations are done directly to the main memory in a bid to ensure data consistency across all threads that are accessing an instance of the vending machine.
According to the microprocessor architecture, it is faster for the CPU to read/write from/into a cache (L1, L2 or L3 - in the order of access speed reduction) than it is to write into its [data] register. Same way it is faster for the CPU to write into its register than to write into the main memory, i.e.

**Cache > Register > Main Memory (RAM) > Persistent Memory (Hard Drive)**

Using the volatile keyword makes the CPU shun reading/writing of the vending machine's state data from/into the cache or register, but directly from/into the main memory. This also poses a longer execution time (performance issue) to the vending machine.

However, the performnance issues are negligible when compared to the execution speed of a typical machine.


### 2. Use of Synchronized keyword
Each state data has a monitor/access object that its read/write operation is synchronized on. This is necessary to ensure that ONLY the thread that has acquired the lock can modify/read the data per time. This also is to ensure data consistency across all threads.
The aim of parallelism/concurrency is to ensure simultaneous execution/operation in a system so as to maximize the system processor cores. However, the synchronized keyword blocks all threads (except the one which has acquired the access lock) attempting to perform an read/write on the state data. This has jeopardized the essence of parallelism/consurrency but the state data access synchronization is necessary for data consistency - a big deal in this system.


## Strategy chosen for providing the “best” set of coins returned (including trade-offs)
I named the strategy **"deplete-highest-denomination-first"** as it focuses on depleting higher coin denomination count first so as to rapidly take the total change amount to zero within few loops/iterations. This, by implication, means the strategy will take fewer number of loop/iteration to get the coin combination list computed for a typical customer change.

Furthermore, the strategy is completed in fewer loops/iterations if the difference between supported coin types are significant and there are more supported coin types. For example, if the total change amount is 2.3 (2 pounds, 30p), for a:
- **supported coin list of {0.1, 1.0, 2.0, 0.2}***, it will take 3 iterations to compute the change coin combination (i.e. {2.0, 0.2, 0.1}),
- **supported coin list of {0.1, 0.5}***, it will take 7 iterations to compute the change coin combination (i.e. {0.5, 0.5, 0.5, 0.5, 0.1, 0.1, 0.1})

See [here](https://drive.google.com/file/d/1Rezz8xPPeYwjIFSbGUoIcXTT0cPwgWpC/view?usp=sharing) for the flowchart of the implemented algorithm for the strategy.

**Tradeoff**"": We could have merged the customer submitted coin(s) to the available coin(s) before the change coin computation. But that doesn't follow a proper accounting process.
The big question is: What if after merging the customer submitted coin(s) to the available coin(s) and another customer purchase request hijack (i.e. the processor assigns resource to another thread for execution) it in the process and we end up settling the other customer with the first customer's money. How do we refund the exact first customer submitted coin(s) if the customer purchase request fail?

It is, however, safe to compute the customer change coin combination ONLY from the vending machine's available coin(s).

The implication of this is that some customer purchase requests will fail, even though we could successfully compute the change coin if the customer's submitted coin(s) was merged to the available coin(s). For instance, if the product cost 2.0 and the customer submitted {2.0, 0.5} coin list and is due to collect 0.5 as the change. Perhaps the count for 0.5 coin in the vending machine's available coin is zero, the customer purchase request will fail as we will be unable to compute the change coin combination, whereas if we had merged the customer submitted coins to the available coins, all we would have done is to return the 0.5 excess the customer submitted and complete the customer purchase request instead.

However, not merging the coin before computation is better as it makes customer refund easy in case of purchase request failure.
