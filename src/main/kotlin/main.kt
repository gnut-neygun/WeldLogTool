import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import javax.imageio.ImageIO
import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel") //Set the theme for the file picker
    Window(
        title = "CSV-PDF-App",
        icon = ImageIO.read({}::class.java.getResource("app-icon.png")),
        resizable = true,
        size = IntSize(700, 700)
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MaterialTheme() {
                DesktopTheme {
                    ContentRoot()
                }
            }
        }
    }
}

