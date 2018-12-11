import java.awt.Component
import java.awt.Graphics
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import kotlin.math.roundToInt

class ScrollBar(val parent: Component) {
    var ratio : Double = 1.0 // should be in 0..1
    private var position = 0.0 // should be in 0..1

    private val wheelSpeed = 40

    private val h : Int get() = parent.height
    private val w : Int get() = parent.width
    private val barHeight : Int get() = h - 2
    val barWidth = 10
    private var barGap = 1
    private val buttonHeight : Int get() = barHeight - 2 * barGap
    val y : Int get() = ((h / ratio - h) * position).toInt()

    class Rectangle(val x: Int, val y: Int, val w: Int, val h: Int)

    val barRect : Rectangle
        get() = Rectangle(w - barWidth - 1 + barGap,
            (1 + barGap + barHeight * (1 - ratio) * position).roundToInt(),
            barWidth - 2 * barGap,
            (buttonHeight * ratio).roundToInt()
        )

    fun draw(g: Graphics) {
        g.color = colors.scrollBar
        g.fillRect(w - barWidth - 1, 1, barWidth, barHeight)
        g.color = if (barFocused) colors.scrollBarFocused else colors.scrollBarButton
        g.fillRect(barRect.x, barRect.y, barRect.w, barRect.h)
    }

    fun move(dy: Double) {
        if (ratio >= 1 - 1e-9) return
        position = minOf(1.0, maxOf(0.0, position + dy / h / (1 - ratio)))
    }

    fun scroll(wheelRotation: Int) {
        this.move(wheelRotation * wheelSpeed * ratio)
    }

    fun isIn(point: Point): Boolean {
        return barRect.x <= point.x && point.x <= barRect.x + barRect.w &&
                barRect.y <= point.y && point.y <= barRect.y + barRect.h
    }

    private var barPressed = false
    private var barFocused = false
    private var mousePrev = Point(0, 0)

    init {
        parent.addMouseWheelListener(object : MouseAdapter() {
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                scroll(e.wheelRotation)
                parent.repaint()
            }
        })
        parent.addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                if (barPressed) {
                    move((e.point.y - mousePrev.y).toDouble())
                    mousePrev = e.point
                    parent.repaint()
                }
            }
            override fun mouseMoved(e: MouseEvent) {
                val old = barFocused
                barFocused = isIn(e.point)
                if (old != barFocused)
                    parent.repaint()
            }
        })
        parent.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                barPressed = isIn(e.point)
                mousePrev = e.point
            }
        })
    }
}
