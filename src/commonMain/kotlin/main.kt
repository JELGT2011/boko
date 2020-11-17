import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.timesPerSecond
import com.soywiz.korev.Key
import com.soywiz.korev.keys
import com.soywiz.korge.Korge
import com.soywiz.korge.input.mouse
import com.soywiz.korge.scene.debugBmpFont
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.async.launch
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.*


suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
    var line = 0
    fun textLine(text: String) = text(text, textSize = 16.0, font = debugBmpFont).position(2, line++ * 20 + 5)
    fun nowTime() = DateTime.now().local.format(DateFormat("HH:mm:ss.SSS"))

    textLine("Events: ")
    val positionText = textLine("Position:")
    val velocityText = textLine("Velocity:")

    val red = Player.create(
            stage = stage,
            color = "red",
            x = (256 + 100).toDouble(),
            y = (256 + 100).toDouble()
    )

    val teal = Player.create(
            stage = stage,
            color = "teal",
            x = (256 + 100).toDouble(),
            y = (256 - 100).toDouble()
    )

    val killOn = stage.image(resourcesVfs["kill_on.png"].readBitmap()) {
        anchor(.5, .5)
        scale(.25)
        position(457, 457)
    }

    val killOff = stage.image(resourcesVfs["kill_off.png"].readBitmap()) {
        anchor(.5, .5)
        scale(.031)
        position(457, 457)
    }

    val keyDown = mutableMapOf<Key, Boolean>()

    var dx = 0
    var dy = 0

//  val player = container {
//    image(resourcesVfs["red.png"].readBitmap())
//    onCollision() {
//
//    }
//  }

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
                  red.kill(stage, teal)
                }
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

    red.addFixedUpdater(60.timesPerSecond) {
        val magnitude = sqrt(dx.toDouble().pow(2) + dy.toDouble().pow(2))
        val angle = atan2(dy.toDouble(), dx.toDouble())
        this.x += magnitude * cos(angle)
        this.y += magnitude * sin(angle)

        positionText.text = "x = $x, y = $y"
        velocityText.text = "dx = $dx, dy = $dy"
    }
}
