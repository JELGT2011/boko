import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.math.pow
import kotlin.math.sqrt


const val KillDistance = 100

suspend fun getSprite(stage: Stage, name: String, x: Double, y: Double): Image {
    return stage.image(resourcesVfs[name].readBitmap()) {
        anchor(.5, .5)
        scale(.25)
        position(x, y)
    }
}

open class Player(
        var color: String,
        var ded: Boolean = false,
        var sprite: Image,
        var killCollider: Circle
) : Container() {

    companion object {
        suspend fun create(stage: Stage, color: String, x: Double, y: Double): Player {
            val sprite = stage.coroutineContext.run { getSprite(stage, "$color.png", x, y) }
            val killCollider = stage.circle(KillDistance.toDouble(), color = Colors.TRANSPARENT_WHITE) {
                anchor(.5, .5)
                position(x, y)
            }

            val player = Player(
                    color = color,
                    sprite = sprite,
                    killCollider = killCollider
            ).apply { this.xy(x, y) }

          stage.addChild(player)

//          TODO: figure out why this doesn't work
//          player.addChild(sprite)
//          player.addChild(killCollider)

            player.sprite.addUpdater {
                this.x = player.x
                this.y = player.y
            }
            player.killCollider.addUpdater {
                this.x = player.x
                this.y = player.y
            }

            return player
        }
    }

    suspend fun kill(stage: Stage, target: Player) {
        val x = (this.x - target.x).pow(2)
        val y = (this.y - target.y).pow(2)

        val distance = sqrt(x + y)

        if (distance <= KillDistance) {
            target.decease(stage)
        }
    }

    suspend fun decease(stage: Stage) {
        this.ded = true
        this.sprite = getSprite(stage, "${this.color}_dead.png", this.x, this.y)
    }
}
