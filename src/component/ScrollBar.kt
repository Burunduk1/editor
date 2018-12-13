package component

import model.ColorTheme
import java.awt.Component
import java.awt.Graphics
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import kotlin.math.roundToInt

class ScrollBar(val parent: Component, private val colors: ColorTheme) {
    var ratio : Double = 1.0 // should be in 0..1
    private var position = 0.0 // should be in 0..1

    private val wheelSpeed = 40

    private val height : Int get() = parent.height - 2
    private val width = 10
    private var barGap = 1

    val y : Int get() = ((parent.height / ratio - parent.height) * position).toInt()

    class Rectangle(private val x: Int, private val y: Int, private val w: Int, private val h: Int) {
        fun isIn(p: Point) = p.x in x .. x + w && p.y in y .. y + h
        fun fill(g: Graphics) = g.fillRect(x, y, w, h)
    }

    private val barRect : Rectangle
        get() = Rectangle(
            parent.width - width - 1 + barGap,
            (1 + barGap + height * (1 - ratio) * position).roundToInt(),
            width - 2 * barGap,
            ((height - 2 * barGap) * ratio).roundToInt()
        )
    private val componentRect : Rectangle
        get() = Rectangle(parent.width - width - 1, 1, width, height)

    private fun move(dy: Double) {
        if (ratio >= 1 - 1e-9)
            return
        position = minOf(1.0, maxOf(0.0, position + dy / parent.height / (1 - ratio)))
    }
    private fun move(dy: Int) {
        move(dy.toDouble())
    }
    fun moveParent(dy: Int) {
        move(dy * ratio)
    }

    fun scroll(wheelRotation: Int) {
        move(wheelRotation * wheelSpeed * ratio)
    }

    fun isInBar(p: Point) = barRect.isIn(p)
    fun isInComponent(p: Point) = componentRect.isIn(p)

    private fun repaint(need: Boolean = true) {
        if (need)
            parent.repaint()
    }

    var barPressed = false
        private set(value) {
            val needRepaint = (field != value)
            field = value
            repaint(needRepaint)
        }

    private var barFocused = false
        private set(value) {
            val needRepaint = (field != value)
            field = value
            repaint(needRepaint)
        }

    private fun updateFocus(p: Point) {
        barFocused = isInBar(p)
    }

    /** draw part */

    fun draw(g: Graphics) {
        g.color = colors.scrollBar
        componentRect.fill(g)
        g.color = if (barFocused) colors.scrollBarFocused else colors.scrollBarButton
        barRect.fill(g)
    }

    /** controller part */

    init {
        val mouseAdapter = object : MouseAdapter() {
            private var mousePrev = Point(0, 0)
            override fun mouseWheelMoved(e: MouseWheelEvent) {
                scroll(e.wheelRotation)
                repaint()
            }
            override fun mouseDragged(e: MouseEvent) {
                if (barPressed) {
                    if (e.point.y in 0..height) {
                        move(e.point.y - mousePrev.y)
                        mousePrev = e.point
                        repaint()
                    }
                } else {
                    updateFocus(e.point)
                }
            }
            override fun mouseMoved(e: MouseEvent) {
                updateFocus(e.point)
            }
            override fun mousePressed(e: MouseEvent) {
                barPressed = isInBar(e.point)
                mousePrev = e.point
            }
            override fun mouseReleased(e: MouseEvent) {
                barPressed = false
                updateFocus(e.point)
            }
        }
        parent.addMouseWheelListener(mouseAdapter)
        parent.addMouseMotionListener(mouseAdapter)
        parent.addMouseListener(mouseAdapter)
    }
}
