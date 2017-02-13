package game_of_life.actor

import akka.actor.Props
import game_of_life.model.Game
import org.scalatest.WordSpec
import game_of_life.util.SerializationCodec._
import org.scalatest.Matchers._
import org.scalatest.mockito.MockitoSugar.mock

class GridActorSpec extends WordSpec {

  "A GridActor" should {

    val game = mock[Game]

    "have serializable props" in {
      val props = (GridActor props game)(_ => ())
      decode[Props](encode(props)) shouldBe a[Props]
    }
  }
}
