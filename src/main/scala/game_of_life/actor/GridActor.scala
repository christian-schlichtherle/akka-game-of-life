package game_of_life.actor

import akka.actor.{Actor, ActorRef, Props, Stash}
import game_of_life.actor.CellActor._
import game_of_life.actor.GridActor._
import game_of_life.model.{Game, Position}

import scala.concurrent.Future

class GridActor private(game: Game)(view: Game#Grid => Unit) extends Actor with Stash {

  import context._
  import game._

  private val gridDispatcher = context.system.dispatchers lookup "grid-dispatcher"

  private var _grid = game.newGrid.setup()

  private var _todo = rows * columns

  override def preStart(): Unit = {
    for (position <- game) {
      actorOf(CellActor props game, position.toString)
    }
    setAndConfirmStatesFromGrid()
  }

  def receive: Receive = paused

  private def paused: Receive = {

    case Play =>
      unstashAll()
      become(playing)

    case Pause =>

    case State(_) =>
      stash()
  }

  private def playing: Receive = {

    case Play =>

    case Pause =>
      become(paused)

    case State(_) =>
      whenDone {
        requestNextStates()
        become(updating)
        // Dispatch grid processing to another thread-pool so the current thread doesn't block.
        val grid = _grid
        Future(view(grid))(gridDispatcher)
        _grid = game.newGrid
      }
  }

  private def updating: Receive = {

    case Play | Pause =>
      stash()

    case State(alive) =>
      val Position(row, column) = Position(sender.path.name)
      _grid(row, column) = alive
      whenDone {
        setAndConfirmStatesFromGrid()
        unstashAll()
        become(playing)
      }
  }

  private def whenDone(block: => Unit): Unit = {
    _todo -= 1
    if (_todo == 0) {
      _todo = rows * columns
      block
    }
  }

  private def setAndConfirmStatesFromGrid(): Unit = {
    foreach { case (Position(row, column), cell)  => cell ! SetAndConfirmState(State(_grid(row, column))) }
  }

  private def requestNextStates(): Unit = {
    foreach { case (_, cell) => cell ! GetNextState }
  }

  def foreach[A](f: ((Position, ActorRef)) => A): Unit = {
    game foreach (position => f(position, cellRef(position)))
  }

  private def cellRef(position: Position) = child(position.toString).get
}

object GridActor {

  def props(game: Game)(view: Game#Grid => Unit): Props = Props(classOf[GridActor], game, view)

  case object Play

  case object Pause
}
