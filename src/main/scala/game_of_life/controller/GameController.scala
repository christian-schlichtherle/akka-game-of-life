package game_of_life.controller

import akka.actor.{ActorSystem, Terminated}
import game_of_life.actor.GridActor
import game_of_life.actor.GridActor.{Pause, Play}
import game_of_life.controller.GameController._
import game_of_life.model.Game

import scala.concurrent.Future

/**
  * An asynchronous controller for the game of life.
  *
  * @param view called once every cycle when the state of all cells on the grid has been updated.
  */
class GameController(game: Game)(view: Game#Grid => Unit = printGrid) {

  require(view != null)

  private val system = ActorSystem("game-of-life")

  private val grid = system.actorOf(GridActor.props(game)(view), "grid")

  private var playing = false

  def play(): Unit = {
    grid ! Play
    playing = true
  }

  def pause(): Unit = {
    grid ! Pause
    playing = false
  }

  def toggle(): Unit = {
    if (playing) {
      pause()
    } else {
      play()
    }
  }

  def stop(): Future[Terminated] = system terminate ()
}

object GameController {

  def printGrid(grid: Game#Grid): Unit = {
    import Console._
    print(grid)
    flush()
  }
}
