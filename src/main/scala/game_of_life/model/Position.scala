package game_of_life.model

case class Position(row: Int, column: Int) {

  override def toString = s"R${row}C$column"
}

object Position {

  private[this] val positionRegex = """R(\d+)C(\d+)""".r

  def apply(s: String): Position = {
    val positionRegex(row, column) = s
    Position(row.toInt, column.toInt)
  }
}
