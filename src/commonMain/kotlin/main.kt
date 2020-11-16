import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.korev.Key
import com.soywiz.korev.keys
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.debugBmpFont
import com.soywiz.korge.view.Image
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korge.view.scale
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
  var line = 0
  fun textLine(text: String) = text(text, textSize = 16.0, font = debugBmpFont).position(2, line++ * 20 + 5)
  fun nowTime() = DateTime.now().local.format(DateFormat("HH:mm:ss.SSS"))

  suspend fun getSprite(name: String, positionX: Float, positionY: Float): Image {
    return stage.image(resourcesVfs[name].readBitmap()) {
      anchor(.5, .5)
      scale(.25)
      position(positionX, positionY)
    }
  }
  textLine("Events: ")
  val positionText = textLine("Position:")
  val velocityText = textLine("Velocity:")

  data class Character(
    var ded: Boolean = false,
    var sprite: Image,
    var positionX: Float,
    var positionY: Float
  )

  data class Player(
    val character: Character,
    val color: String
  ) {

    suspend fun kill(target: Player) {
      target.decease()
    }

    suspend fun decease() {
      this.character.ded = true
      this.character.sprite = getSprite("${this.color}_dead.png", this.character.positionX, this.character.positionY)
    }
  }

  val red = Player(
    character = Character(
      sprite = getSprite("red.png", (256 + 100).toFloat(), (256 + 100).toFloat()),
      positionX = (256 + 100).toFloat(),
      positionY = (256 + 100).toFloat()
    ),
    color = "red"
  )

  val teal = Player(
    character = Character(
      sprite = getSprite("teal.png", (256 + 100).toFloat(), (256 + 100).toFloat()),
      positionX = (256 + 100).toFloat(),
      positionY = (256).toFloat()
    ),
    color = "teal"
  )

  val keyDown = mutableMapOf<Key, Boolean>()

  var dx = 0
  var dy = 0

  keys {
    down {
      if (keyDown[this.key] == true) {
        return@down
      }
      when (this.key) {
        Key.W -> {
          dx += 0
          dy += -1
        }
        Key.A -> {
          dx += -1
          dy += 0
        }
        Key.S -> {
          dx += 0
          dy += 1
        }
        Key.D -> {
          dx += 1
          dy += 0
        }
        Key.K -> {
          launch {
            red.kill(teal)
          }
        }
        else -> {}
      }

      keyDown[this.key] = true
    }

    up {
      keyDown[this.key] = false

      when (this.key) {
        Key.W -> {
          dx -= 0
          dy -= -1
        }
        Key.A -> {
          dx -= -1
          dy -= 0
        }
        Key.S -> {
          dx -= 0
          dy -= 1
        }
        Key.D -> {
          dx -= 1
          dy -= 0
        }
        else -> {}
      }
    }
  }

  red.character.sprite.addUpdater {
    val magnitude = sqrt(dx.toDouble().pow(2) + dy.toDouble().pow(2))
    val angle = atan2(dy.toDouble(), dx.toDouble())
    this.x += magnitude * cos(angle)
    this.y += magnitude * sin(angle)
    positionText.text = "x = $x, y = $y"
    velocityText.text = "dx = $dx, dy = $dy"
  }
}
