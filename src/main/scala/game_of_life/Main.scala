package game_of_life

import game_of_life.controller.GameController
import game_of_life.model.Game
import game_of_life.model.Game.SetupPredicate

import scala.io.StdIn
import scala.reflect.runtime.currentMirror
import scala.tools.reflect.ToolBox

object Main extends App {

  val controller = new GameController(
    Game(rows = args(0).toInt, columns = args(1).toInt) {
      args drop 2 match {
        case Array() => Game.randomInit
        case Array(word) if word equalsIgnoreCase "blinkers" => (row, column) => row % 4 == 1 && column % 4 < 3
        case array => eval[SetupPredicate]("($r: Int, $c: Int) => { " + (array mkString " ") + " }: Boolean")
      }
    }
  )()

  do {
    controller toggle ()
  } while ((StdIn readLine ()) != null)
  controller stop ()

  private def eval[A](string: String): A = {
    val tb = currentMirror mkToolBox ()
    val tree = tb parse string
    (tb eval tree).asInstanceOf[A]
  }
}
