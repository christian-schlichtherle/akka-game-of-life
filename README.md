# Akka Game of Life

This is simple implementation of [Conway's Game of Life](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life) 
using (Akka)[http://akka.io].

This is a fun project for comparison with another 
[implementation in Ruby](https://github.com/christian-schlichtherle/ruby-game-of-life).

## How to Use

First, start sbt:

    sbt

Now, for simulating a random pattern of cells on a grid with 80 rows and 240 columns:

    > run 80 240
    
The console output can be paused or resumed anytime by pressing ENTER - any characters typed on the same line get 
discarded.
To stop the program, signal end-of-file, usually by pressing Ctrl-D - the actual character depends on your terminal 
settings.

To simulate a simple test pattern of blinkers, use:

    > run 80 240 blinkers
    
To simulate a custom pattern, you can provide a boolean expression like this:

    > run 80 240 ($r + $c) % 40 == 0 || ($r - $c) % 40 == 0
    
In the expression, `$r` refers to the current row and `$c` to the current column.
The Scala API can be used in the expression, so you can also write this:

    > run 80 240 scala.math.sqrt(scala.math.pow($r - 40, 2) + scala.math.pow($c - 120, 2)).toInt % 4 < 2
