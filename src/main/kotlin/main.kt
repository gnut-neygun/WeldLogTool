import androidx.compose.desktop.DesktopTheme
import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import javax.swing.UIManager

fun configureStdErr() {
    val logFolderName = "logs"
    val logFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM-dd HH'h'mm"))
    val logFolder = File(logFolderName)
    if (!logFolder.exists())
        logFolder.mkdir()
    if (logFolder.list().size >= 20) {
        logFolder.deleteRecursively()
        logFolder.mkdir()
    }
    val logFile = File("$logFolderName/$logFileName.log")
    if (logFile.createNewFile()) {
        System.setErr(PrintStream(logFile))
    }
}


fun main() {
    val amIinAJar = {}::class.java.getResource("logo.png")!!.file.endsWith(".jar!/logo.png")
    if (amIinAJar)
        configureStdErr() // for logging
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) //Set the theme for the file picker
    Window(
        title = "Swagelok Munich Weld Log Tool",
        icon = ImageIO.read({}::class.java.getResource("app-icon.png")),
        resizable = true,
        size = IntSize(800, 750)
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

