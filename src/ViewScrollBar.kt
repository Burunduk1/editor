import java.awt.Component
import java.awt.Graphics
import kotlin.math.roundToInt

data class ScrollBar(val parent: Component) {
    private var barWidth = 10
    private var barGap = 1
    private val wheelSpeed = 40

    var ratio : Double = 1.0 // should be in 0..1
    private var position = 1.0 // should be in 0..1

    private val h : Int get() = parent.height
    private val barHeight : Int get() = h - 2
    private val buttonHeight : Int get() = barHeight - 2 * barGap
    val y : Int get() = (h * (1 - ratio) * position).toInt()

    fun draw(g: Graphics) {
        with (parent) {
            g.color = colors.scrollBar
            g.fillRect(width - barWidth - 1, 1, barWidth, barHeight)
            g.color = colors.scrollBarButton
            g.fillRect(
                width - barWidth - 1 + barGap,
                (1 + barGap + barHeight * (1 - ratio) * position).roundToInt(),
                barWidth - 2 * barGap,
                (buttonHeight * ratio).roundToInt()
            )
        }
    }

    fun scroll(wheelRotation: Int) {
        if (ratio >= 1 - 1e-9) return
        val dy = wheelRotation * wheelSpeed
        println("dy=$dy ratio=$ratio h=$h size=${h/ratio} nonview_size=${h/ratio*(1 - ratio)} scrolled part of non view part = ${dy/(h/ratio*(1 - ratio))}")
        position = minOf(1.0, maxOf(0.0, position + dy * ratio / h / (1 - ratio)))
    }
}
