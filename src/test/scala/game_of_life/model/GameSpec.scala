package game_of_life.model

import game_of_life.util.SerializationCodec._
import org.scalatest.Matchers._
import org.scalatest.WordSpec

class GameSpec extends WordSpec {

  private def be = afterWord("be")

  "A Game" should be {

    val game = Game(rows = 2, columns = 2)()

    "cloneable via serialization" in {
      val clone = decode[Game](encode(game))
      clone shouldBe game
    }

    "traversable using foreach" in {
      var r = Seq.empty[Position]
      game foreach (r :+= _)
      r shouldBe Seq(Position(0, 0), Position(0, 1), Position(1, 0), Position(1, 1))
    }
  }
}
