package game_of_life.actor

import akka.actor.{Actor, ActorIdentity, ActorRef, Identify, Props, Stash}
import game_of_life.actor.CellActor._
import game_of_life.model.{Game, Position}

class CellActor private(game: Game)  extends Actor with Stash {

  import context._
  import game._

  private lazy val Position(row, column) = Position(self.path.name)

  private var _alive: Boolean = false

  private var _neighbors = Seq.empty[ActorRef]

  private var _neighborsToIdentify = 8

  private var _neighborsAlive: Int = 0

  def receive: Receive = {

    case _ =>
      for {
        rowOffset <- -1 to 1
        columnOffset <- -1 to 1
        if rowOffset != 0 || columnOffset != 0
      } {
        val neighborRow = (rows + row + rowOffset) % rows
        val neighborColumn = (columns + column + columnOffset) % columns
        val path = "../" + Position(neighborRow, neighborColumn)
        actorSelection(path) ! Identify(path)
      }
      stash()
      unstashAll()
      become(identifying)
  }

  private def identifying: Receive = {

    case ActorIdentity(_, maybeRef) =>
      maybeRef foreach (_neighbors :+= _)
      _neighborsToIdentify -= 1
      if (_neighborsToIdentify == 0) {
        unstashAll()
        become(active)
      }

    case _ =>
      stash()
  }

  private def active: Receive = {

    case SetAndConfirmState(state@State(alive)) =>
      if (_alive != alive) {
        _alive = alive
        _neighbors foreach (_ ! state)
      }
      sender ! state

    case GetNextState =>
      sender ! State(_neighborsAlive match {
        case 2 => _alive
        case 3 => true
        case _ => false
      })

    case State(alive) =>
      if (alive) {
        _neighborsAlive += 1
      } else {
        _neighborsAlive -= 1
      }
  }
}

object CellActor {

  def props(game: Game): Props = Props(classOf[CellActor], game)

  case class State(alive: Boolean)

  case class SetAndConfirmState(state: State)

  case object GetNextState
}
