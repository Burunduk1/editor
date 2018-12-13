package utility

import java.awt.event.KeyEvent
import java.awt.event.KeyListener

val keyEventHandler = mapOf(
    KeyEvent.KEY_TYPED to KeyListener::keyTyped,
    KeyEvent.KEY_PRESSED to KeyListener::keyPressed,
    KeyEvent.KEY_RELEASED to KeyListener::keyReleased
)

fun handleKeyEvent(listener: KeyListener, e: KeyEvent) {
    keyEventHandler[e.id]?.call(listener, e)
}