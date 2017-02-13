package game_of_life.model

import java.util.concurrent.ThreadLocalRandom

import game_of_life.model.Game._

import scala.collection.mutable

/** The game of life.
  *
  * Like any case class, only the first parameter list is considered for equals comparison.
  * This is required if you want to deploy some of the inner actors to a remote Akka system.
  *
  * @param setup the setup predicate for the cells on the grid.
  * @see <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conway's Game of Life</a>
  */
case class Game(rows: Int, columns: Int)(val setup: SetupPredicate = randomInit) { self =>

  require(rows > 1)
  require(columns > 1)
  require(setup != null)

  def newGrid: Grid = new Grid

  private def setup(grid: Grid): Unit = {
    foreach { case Position(row, column) => grid(row, column) = setup(row, column) }
  }

  def foreach[A](f: Position => A): Unit = {
    for {
      row <- 0 until rows
      column <- 0 until columns
    } {
      f(Position(row, column))
    }
  }

  class Grid private[Game] () extends Equals {

    def setup(): this.type = {
      game setup this
      this
    }

    def game: self.type = self

    private val cells = new mutable.BitSet(rows * columns)

    def update(row: Int, column: Int, value: Boolean): Unit = cells(index(row, column)) = value

    def apply(row: Int, column: Int): Boolean = cells(index(row, column))

    private def index(row: Int, column: Int) = row * columns + column

    def canEqual(that: Any): Boolean = that.isInstanceOf[Grid]

    override def equals(obj: Any): Boolean = {
      obj match {
        case that: Grid =>
          (that canEqual this) && this.game == that.game && this.cells == that.cells
        case _ => false
      }
    }

    override def hashCode: Int = {
      var c = 17
      c = 31 * c + game.hashCode
      c = 31 * c + cells.hashCode
      c
    }

    override def toString: String = {
      val sb = new mutable.StringBuilder((rows + 2) * (columns + 2))

      def appendLine() = 0 until columns foreach (_ => sb.append('-'))

      sb.append("\n+")
      appendLine()
      sb.append("+\n")
      for (row <- 0 until rows) {
        sb.append('+')
        for (column <- 0 until columns) {
          sb.append(if (apply(row, column)) 'O' else ' ')
        }
        sb.append("+\n")
      }
      sb.append('+')
      appendLine()
      sb.append('+')
      sb.toString()
    }
  }
}

object Game {

  type SetupPredicate = (Int, Int) => Boolean

  def randomInit(row: Int, column: Int): Boolean = ThreadLocalRandom.current.nextBoolean()
}
