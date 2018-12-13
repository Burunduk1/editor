package tests

import model.EditorWindow
import org.assertj.swing.fixture.FrameFixture
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.awt.EventQueue
import java.awt.event.KeyEvent

class GuiTest {

    var window: EditorWindow? = null
    var frame: FrameFixture? = null

    @Before
    fun setUp() {
        EventQueue.invokeAndWait {
            window = EditorWindow()
            window?.revalidate()
            window?.isVisible = true
        }
        frame = FrameFixture(window!!)
    }

    @Test
    fun test1() {
        println("test1")
        window!!.tabbedPane.newTab()
        frame!!.pressKey(KeyEvent.VK_A)
        frame!!.pressKey(KeyEvent.VK_B)
        frame!!.pressAndReleaseKeys(KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3)
        frame!!.pressKey(KeyEvent.VK_SHIFT).pressKey(KeyEvent.VK_A)
        frame!!.pressKey(KeyEvent.VK_SHIFT).pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_LEFT)
        frame!!.pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_C)
        frame!!.pressKey(KeyEvent.VK_CONTROL).pressKey(KeyEvent.VK_V)
        assertEquals(window!!.tabbedPane.currentTab?.editor?.text, "ab123Aab123A")
    }
}