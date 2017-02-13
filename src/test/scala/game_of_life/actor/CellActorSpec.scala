package game_of_life.actor

import akka.actor.Props
import game_of_life.model.Game
import game_of_life.util.SerializationCodec._
import org.scalatest.Matchers._
import org.scalatest.WordSpec
import org.scalatest.mockito.MockitoSugar.mock

class CellActorSpec extends WordSpec {

  "A CellActor" should {

    val game = mock[Game]

    "have serializable props" in {
      val props = CellActor props game
      decode[Props](encode(props)) shouldBe a[Props]
    }
  }
}
