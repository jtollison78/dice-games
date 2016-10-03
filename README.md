# dice-games

## King of Tokyo

King of Tokyo Analysis (clojure)

I wanted to find the optimal strategy for King of Tokyo. I didn't figure there would be easily exploitable game balance problems as Richard Garfield, the designer, teaches a course in Game Theory. But that doesn't mean that there won't be exploitable holes in individuals play.

To this end, I first wanted to find exact values for the dice rolls that drive the game. You roll 6 dice, then chose any of the 6 to roll again, then chose any of the 6 to roll yet again. To complicate matters, Victory Points are only awarded if you get 3 of a kind (1s, 2s, or 3s) at the end of your rerolls, and then you get the face value for the set of 3 and 1 point each for additional dice of that type -> [1 1 3 3 3 3] = 4 points. For all other rolls, you simply get the number that you roll of [attack, energy, life].

I didn't use simulation to find these expected values. I found exact values, using (memoize), a pre packaged clojure function to build lookup tables on the fly. The benefit of not using simulations, and instead, exploring all routes, is that you can be sure you're finding the best strategy, not just guessing which path to take and simulating to find the outcome of that guessed-at strategy.

Note that I have 2 mutually recursive functions building the lookup tables, and that one of these functions calls itself to build out individual rolls, even though the roll would generally occur at once. This is helpful when you are rerolling varying numbers of dice where the new rolls become a simple lookup. If you remove the memoization from the first function, the program's run time increases approximately 5 orders of magnitude.

If you run the code above, you will find that:

Optimal Victory Point Strategy averages: 2.2357416

Optimal Attack (or other singleton) strategy average: 2.5277777

From here, the next step is to build these functions into a full min-max game tree for a HU game, taking position(in-tokyo?) into account. This will produce a better feel for how long a game should run, but my suspicion is that games are short enough that cards aren't really meant to be a good strategy outside of opportunistic use. Instead, the focus is on nicely balanced design centered on the dice rolls.

Alternatively, you may find that there are other interesting games to use this technique with, such as Zombie Dice.

## Strategy Tables

Here are the correct rerolls for every combination of Victory Point dice rolled. Non-VP dice were simply set to 6s, which represent any combinations of 4s, 5s, and 6s. The second column shows which dice to keep. The last column shows the average number of dice held after both rerolls. Many of the results here are obvious, but some aren't obvious and may be downright non-intuitive. For instance, if you roll 2 1s and no other victory dice, and you have 2 rolls left, don't keep the 1s.

With 2 ReRolls:
(1 1 1 1 1 1)  -  (1 1 1 1 1 1)  -  4.0

(1 1 1 1 1 2)  -  (1 1 1 1 1)  -  3.4212964

(1 1 1 1 1 3)  -  (1 1 1 1 1)  -  3.4212964

(1 1 1 1 2 2)  -  (1 1 1 1)  -  2.8479972

(1 1 1 1 2 3)  -  (1 1 1 1)  -  2.8479972

(1 1 1 1 3 3)  -  (3 3)  -  3.4995008

(1 1 1 2 2 2)  -  (2 2 2)  -  3.3045945

(1 1 1 2 2 3)  -  (2 2 3)  -  2.8486297

(1 1 1 2 3 3)  -  (3 3)  -  3.4995008

(1 1 1 3 3 3)  -  (3 3 3)  -  4.2790666

(1 1 2 2 2 2)  -  (2 2 2 2)  -  3.8425925

(1 1 2 2 2 3)  -  (2 2 2)  -  3.3045945

(1 1 2 2 3 3)  -  (3 3)  -  3.4995008

(1 1 2 3 3 3)  -  (3 3 3)  -  4.2790666

(1 1 3 3 3 3)  -  (3 3 3 3)  -  4.8425927

(1 2 2 2 2 2)  -  (2 2 2 2 2)  -  4.421296

(1 2 2 2 2 3)  -  (2 2 2 2)  -  3.8425925

(1 2 2 2 3 3)  -  (2 2 2 3 3)  -  3.5462964

(1 2 2 3 3 3)  -  (3 3 3)  -  4.2790666

(1 2 3 3 3 3)  -  (3 3 3 3)  -  4.8425927

(1 3 3 3 3 3)  -  (3 3 3 3 3)  -  5.421296

(2 2 2 2 2 2)  -  (2 2 2 2 2 2)  -  5.0

(2 2 2 2 2 3)  -  (2 2 2 2 2)  -  4.421296

(2 2 2 2 3 3)  -  (2 2 2 2)  -  3.8425925

(2 2 2 3 3 3)  -  (2 2 2 3 3 3)  -  5.0

(2 2 3 3 3 3)  -  (3 3 3 3)  -  4.8425927

(2 3 3 3 3 3)  -  (3 3 3 3 3)  -  5.421296

(3 3 3 3 3 3)  -  (3 3 3 3 3 3)  -  6.0

(1 1 1 1 1 6)  -  (1 1 1 1 1)  -  3.4212964

(1 1 1 1 2 6)  -  (1 1 1 1)  -  2.8479972

(1 1 1 1 3 6)  -  (1 1 1 1)  -  2.8479972

(1 1 1 2 2 6)  -  (2 2)  -  2.74401

(1 1 1 2 3 6)  -  (3)  -  2.729889

(1 1 1 3 3 6)  -  (3 3)  -  3.4995008

(1 1 2 2 2 6)  -  (2 2 2)  -  3.3045945

(1 1 2 2 3 6)  -  (2 2 3)  -  2.8486297

(1 1 2 3 3 6)  -  (3 3)  -  3.4995008

(1 1 3 3 3 6)  -  (3 3 3)  -  4.2790666

(1 2 2 2 2 6)  -  (2 2 2 2)  -  3.8425925

(1 2 2 2 3 6)  -  (2 2 2)  -  3.3045945

(1 2 2 3 3 6)  -  (3 3)  -  3.4995008

(1 2 3 3 3 6)  -  (3 3 3)  -  4.2790666

(1 3 3 3 3 6)  -  (3 3 3 3)  -  4.8425927

(2 2 2 2 2 6)  -  (2 2 2 2 2)  -  4.421296

(2 2 2 2 3 6)  -  (2 2 2 2)  -  3.8425925

(2 2 2 3 3 6)  -  (2 2 2 3 3)  -  3.5462964

(2 2 3 3 3 6)  -  (3 3 3)  -  4.2790666

(2 3 3 3 3 6)  -  (3 3 3 3)  -  4.8425927

(3 3 3 3 3 6)  -  (3 3 3 3 3)  -  5.421296

(1 1 1 1 6 6)  -  (1 1 1 1)  -  2.8479972

(1 1 1 2 6 6)  -  (2)  -  2.3704402

(1 1 1 3 6 6)  -  (3)  -  2.729889

(1 1 2 2 6 6)  -  (2 2)  -  2.74401

(1 1 2 3 6 6)  -  (3)  -  2.729889

(1 1 3 3 6 6)  -  (3 3)  -  3.4995008

(1 2 2 2 6 6)  -  (2 2 2)  -  3.3045945

(1 2 2 3 6 6)  -  (2 2 3)  -  2.8486297

(1 2 3 3 6 6)  -  (3 3)  -  3.4995008

(1 3 3 3 6 6)  -  (3 3 3)  -  4.2790666

(2 2 2 2 6 6)  -  (2 2 2 2)  -  3.8425925

(2 2 2 3 6 6)  -  (2 2 2)  -  3.3045945

(2 2 3 3 6 6)  -  (3 3)  -  3.4995008

(2 3 3 3 6 6)  -  (3 3 3)  -  4.2790666

(3 3 3 3 6 6)  -  (3 3 3 3)  -  4.8425927

(1 1 1 6 6 6)  -  (1 1 1)  -  2.3625522

(1 1 2 6 6 6)  -  (2)  -  2.3704402

(1 1 3 6 6 6)  -  (3)  -  2.729889

(1 2 2 6 6 6)  -  (2 2)  -  2.74401

(1 2 3 6 6 6)  -  (3)  -  2.729889

(1 3 3 6 6 6)  -  (3 3)  -  3.4995008

(2 2 2 6 6 6)  -  (2 2 2)  -  3.3045945

(2 2 3 6 6 6)  -  (2 2 3)  -  2.8486297

(2 3 3 6 6 6)  -  (3 3)  -  3.4995008

(3 3 3 6 6 6)  -  (3 3 3)  -  4.2790666

(1 1 6 6 6 6)  -  ()  -  2.2357416

(1 2 6 6 6 6)  -  (2)  -  2.3704402

(1 3 6 6 6 6)  -  (3)  -  2.729889

(2 2 6 6 6 6)  -  (2 2)  -  2.74401

(2 3 6 6 6 6)  -  (3)  -  2.729889

(3 3 6 6 6 6)  -  (3 3)  -  3.4995008

(1 6 6 6 6 6)  -  ()  -  2.2357416

(2 6 6 6 6 6)  -  (2)  -  2.3704402

(3 6 6 6 6 6)  -  (3)  -  2.729889

(6 6 6 6 6 6)  -  ()  -  2.2357416


With 1 ReRoll:


(1 1 1 1 1 1)  -  (1 1 1 1 1 1)  -  4.0

(1 1 1 1 1 2)  -  (1 1 1 1 1)  -  3.3055556

(1 1 1 1 1 3)  -  (1 1 1 1 1)  -  3.3055556

(1 1 1 1 2 2)  -  (1 1 1 1)  -  2.6111112

(1 1 1 1 2 3)  -  (1 1 1 1)  -  2.6111112

(1 1 1 1 3 3)  -  (3 3)  -  2.8056753

(1 1 1 2 2 2)  -  (1 1 1 2 2 2)  -  3.0

(1 1 1 2 2 3)  -  (2 2 3)  -  2.1527243

(1 1 1 2 3 3)  -  (3 3)  -  2.8056753

(1 1 1 3 3 3)  -  (1 1 1 3 3 3)  -  4.0

(1 1 2 2 2 2)  -  (2 2 2 2)  -  3.6111112

(1 1 2 2 2 3)  -  (2 2 2)  -  2.9493313

(1 1 2 2 3 3)  -  (3 3)  -  2.8056753

(1 1 2 3 3 3)  -  (3 3 3)  -  3.9338348

(1 1 3 3 3 3)  -  (3 3 3 3)  -  4.611111

(1 2 2 2 2 2)  -  (2 2 2 2 2)  -  4.3055553

(1 2 2 2 2 3)  -  (2 2 2 2)  -  3.6111112

(1 2 2 2 3 3)  -  (2 2 2 3 3)  -  3.1666667

(1 2 2 3 3 3)  -  (3 3 3)  -  3.9338348

(1 2 3 3 3 3)  -  (3 3 3 3)  -  4.611111

(1 3 3 3 3 3)  -  (3 3 3 3 3)  -  5.3055553

(2 2 2 2 2 2)  -  (2 2 2 2 2 2)  -  5.0

(2 2 2 2 2 3)  -  (2 2 2 2 2)  -  4.3055553

(2 2 2 2 3 3)  -  (2 2 2 2)  -  3.6111112

(2 2 2 3 3 3)  -  (2 2 2 3 3 3)  -  5.0

(2 2 3 3 3 3)  -  (3 3 3 3)  -  4.611111

(2 3 3 3 3 3)  -  (3 3 3 3 3)  -  5.3055553

(3 3 3 3 3 3)  -  (3 3 3 3 3 3)  -  6.0

(1 1 1 1 1 6)  -  (1 1 1 1 1)  -  3.3055556

(1 1 1 1 2 6)  -  (1 1 1 1)  -  2.6111112

(1 1 1 1 3 6)  -  (1 1 1 1)  -  2.6111112

(1 1 1 2 2 6)  -  (2 2)  -  2.1150994

(1 1 1 2 3 6)  -  (1 1 1)  -  1.9610982

(1 1 1 3 3 6)  -  (3 3)  -  2.8056753

(1 1 2 2 2 6)  -  (2 2 2)  -  2.9493313

(1 1 2 2 3 6)  -  (2 2 3)  -  2.1527243

(1 1 2 3 3 6)  -  (3 3)  -  2.8056753

(1 1 3 3 3 6)  -  (3 3 3)  -  3.9338348

(1 2 2 2 2 6)  -  (2 2 2 2)  -  3.6111112

(1 2 2 2 3 6)  -  (2 2 2)  -  2.9493313

(1 2 2 3 3 6)  -  (3 3)  -  2.8056753

(1 2 3 3 3 6)  -  (3 3 3)  -  3.9338348

(1 3 3 3 3 6)  -  (3 3 3 3)  -  4.611111

(2 2 2 2 2 6)  -  (2 2 2 2 2)  -  4.3055553

(2 2 2 2 3 6)  -  (2 2 2 2)  -  3.6111112

(2 2 2 3 3 6)  -  (2 2 2 3 3)  -  3.1666667

(2 2 3 3 3 6)  -  (3 3 3)  -  3.9338348

(2 3 3 3 3 6)  -  (3 3 3 3)  -  4.611111

(3 3 3 3 3 6)  -  (3 3 3 3 3)  -  5.3055553

(1 1 1 1 6 6)  -  (1 1 1 1)  -  2.6111112

(1 1 1 2 6 6)  -  (1 1 1)  -  1.9610982

(1 1 1 3 6 6)  -  (1 1 1)  -  1.9610982

(1 1 2 2 6 6)  -  (2 2)  -  2.1150994

(1 1 2 3 6 6)  -  (3)  -  1.842984

(1 1 3 3 6 6)  -  (3 3)  -  2.8056753

(1 2 2 2 6 6)  -  (2 2 2)  -  2.9493313

(1 2 2 3 6 6)  -  (2 2 3)  -  2.1527243

(1 2 3 3 6 6)  -  (3 3)  -  2.8056753

(1 3 3 3 6 6)  -  (3 3 3)  -  3.9338348

(2 2 2 2 6 6)  -  (2 2 2 2)  -  3.6111112

(2 2 2 3 6 6)  -  (2 2 2)  -  2.9493313

(2 2 3 3 6 6)  -  (3 3)  -  2.8056753

(2 3 3 3 6 6)  -  (3 3 3)  -  3.9338348

(3 3 3 3 6 6)  -  (3 3 3 3)  -  4.611111

(1 1 1 6 6 6)  -  (1 1 1)  -  1.9610982

(1 1 2 6 6 6)  -  (2)  -  1.5491251

(1 1 3 6 6 6)  -  (3)  -  1.842984

(1 2 2 6 6 6)  -  (2 2)  -  2.1150994

(1 2 3 6 6 6)  -  (3)  -  1.842984

(1 3 3 6 6 6)  -  (3 3)  -  2.8056753

(2 2 2 6 6 6)  -  (2 2 2)  -  2.9493313

(2 2 3 6 6 6)  -  (2 2 3)  -  2.1527243

(2 3 3 6 6 6)  -  (3 3)  -  2.8056753

(3 3 3 6 6 6)  -  (3 3 3)  -  3.9338348

(1 1 6 6 6 6)  -  (1 1)  -  1.4615251

(1 2 6 6 6 6)  -  (2)  -  1.5491251

(1 3 6 6 6 6)  -  (3)  -  1.842984

(2 2 6 6 6 6)  -  (2 2)  -  2.1150994

(2 3 6 6 6 6)  -  (3)  -  1.842984

(3 3 6 6 6 6)  -  (3 3)  -  2.8056753

(1 6 6 6 6 6)  -  ()  -  1.3433428

(2 6 6 6 6 6)  -  (2)  -  1.5491251

(3 6 6 6 6 6)  -  (3)  -  1.842984

(6 6 6 6 6 6)  -  ()  -  1.3433428


## Next Steps

1) Simulations should be written as a sanity check on these numbers.

2) A min-max game tree could be produced using the final VP and claw values to get a feel for how a HU game should play out

3) A min-max game tree that plays out rerolls and therefore take outlying rolls into account.


### Bugs

The Lazy version doesn't work. My stack overflows. I'm not a laziness expert, so you might see a way to fix it.


## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
