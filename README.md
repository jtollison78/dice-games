# dice-games

## King of Tokyo

King of Tokyo Analysis (clojure)

I wanted to find the optimal strategy for King of Tokyo. I didn't figure there would be easily exploitable game balance problems as Richard Garfield, the designer, teaches a course in Game Theory. But that doesn't mean that there won't be exploitable holes in individuals play.

To this end, I first wanted to find exact values for the dice rolls that drive the game. You roll 6 dice, then chose any of the 6 to roll again, then chose any of the 6 to roll yet again. To complicate matters, Victory Points are only awarded if you get 3 of a kind (1s, 2s, or 3s) at the end of your rerolls, and then you get the face value for the set of 3 and 1 point each for additional dice of that type -> [1 1 3 3 3 3] = 4 points. For all other rolls, you simply get the number that you roll of [attack, energy, life].

I didn't use simulation to find these expected values. I found exact values, using (memoize), a pre packaged clojure function to build lookup tables on the fly. The benefit of not using simulations, and instead, exploring all routes, is that you can be sure you're finding the best strategy, not just guessing which path to take and simulating to find the outcome of that guessed-at strategy.

Note that I have 2 mutually recursive functions building the lookup tables, and that one of these functions calls itself to build out individual rolls, even though the roll would generally occur at once. This is helpful when you are rerolling varying numbers of dice where the new rolls become a simple lookup. If you remove the memoization from the first function, the program's run time increases approximately 5 order of magnitude, from .2s to 5.9 hrs!!

If you run the code above, you will find that:

Optimal Victory Point Stategy averages: 2.2267575

Optimal Attack (or other singlton) strategy average: 2.5270996

(fill-dice) is run 1386 times

(rerolls) is run 924 times

From here, the next step is to build these functions into a full min-max game tree for a HU game, taking position(in-tokyo?) into account. This will produce a better feel for how long a game should run, but my suspicion is that games are short enough that cards aren't really meant to be a good strategy outside of opportunistic use. Instead, the focus is on nicely balanced design centered on the dice rolls.

Alternatively, you may find that there are other interesting games to use this technique with, such as Zombie Dice.



### Bugs

The Lazy version doesn't work. My stack overflows. I'm not a laziness expert, so you might see a way to fix it.


## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
