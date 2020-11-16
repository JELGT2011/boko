import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korev.addEventListener
import com.soywiz.korev.keys
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.debugBmpFont
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korge.view.scale
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
  var line = 0
  fun textLine(text: String) = text(text, textSize = 16.0, font = debugBmpFont).position(2, line++ * 20 + 5)
  fun nowTime() = DateTime.now().local.format(DateFormat("HH:mm:ss.SSS"))

  textLine("Events: ")
  val positionText = textLine("Position:")
  val velocityText = textLine("Velocity:")

  val image = image(resourcesVfs["korge.png"].readBitmap()) {
    anchor(.5, .5)
    scale(.5)
    position(256, 256)
  }

  val keyDown = mutableMapOf<Key, Boolean>()

  var dx = 0
  var dy = 0

  keys {
    down {
      if (keyDown[this.key] == true) {
        return@down
      }
      when (this.key) {
        Key.S -> {
          dx += 0
          dy += 1
        }
        Key.D -> {
          dx += 1
          dy += 0
        }
        Key.W -> {
          dx += 0
          dy += -1
        }
        Key.A -> {
          dx += -1
          dy += 0
        }
        else -> {

        }
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
        else -> {

        }
      }
    }
  }

  image.addUpdater {
    // TODO: normalize velocity
    this.x += dx
    this.y += dy
    positionText.text = "x = $x, y = $y"
    velocityText.text = "dx = $dx, dy = $dy"
  }
}
