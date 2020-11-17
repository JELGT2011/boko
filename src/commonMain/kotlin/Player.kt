import com.soywiz.korge.view.*
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
        x: Double,
        y: Double,
        var ded: Boolean = false,
        var sprite: Image,
        var killCollider: Circle
) : Container() {

    companion object {
        suspend fun create(stage: Stage, color: String, x: Double, y: Double): Player {
            val sprite = stage.coroutineContext.run { getSprite(stage, "$color.png", x, y) }
            val killCollider = stage.circle(KillDistance.toDouble()) {
                anchor(.5, .5)
                position(x, y)
            }

            val player = Player(
                    color = color,
                    x = x,
                    y = y,
                    sprite = sprite,
                    killCollider = killCollider
            )

            sprite.addUpdater {
                this.x = player.x
                this.y = player.y
            }
            killCollider.addUpdater {
                this.x = player.x
                this.y = player.y
            }

            return player
        }
    }

    suspend fun kill(target: Player) {
        val x = (this.x - target.x).pow(2)
        val y = (this.y - target.y).pow(2)

        val distance = sqrt(x + y)

        if (distance <= KillDistance) {
            target.decease()
        }
    }

    suspend fun decease() {
        this.ded = true
//        TODO: fix
//        this.sprite = getSprite("${this.color}_dead.png", this.x, this.y)
    }
}
