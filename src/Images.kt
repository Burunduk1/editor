package images

import java.awt.Image
import javax.swing.ImageIcon

fun loadImage(filename: String): Image = ImageIcon("resources/$filename").image
fun loadIcon(filename: String): ImageIcon = ImageIcon(loadImage(filename).getScaledInstance(16, 16, Image.SCALE_SMOOTH))
