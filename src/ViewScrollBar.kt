import java.awt.Component
import java.awt.Graphics

data class ScrollBar(val parent: Component) {
    var scrollBarWidth = 10
    var scrollBarGap = 1

    fun draw(g: Graphics) {
        with (parent) {
            g.color = colors.scrollBar
            g.fillRect(width - scrollBarWidth - 1, 1, scrollBarWidth, height - 2)
            g.color = colors.scrollBarButton
            g.fillRect(
                width - scrollBarWidth - 1 + scrollBarGap,
                1 + scrollBarGap,
                scrollBarWidth - 2 * scrollBarGap,
                30
            )
        }
    }
}
