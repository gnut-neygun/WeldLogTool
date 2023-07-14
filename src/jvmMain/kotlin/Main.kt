import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Swagelok Munich Weld Log Tool",
            resizable = true,
            icon = painterResource("app-icon.png"),
            state = rememberWindowState(width = 800.dp, height = 750.dp)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                MaterialTheme() {
                    CompositionLocalProvider(
                        LocalScrollbarStyle provides ScrollbarStyle(
                            minimalHeight = 16.dp,
                            thickness = 8.dp,
                            shape = MaterialTheme.shapes.small,
                            hoverDurationMillis = 300,
                            unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
                        )
                    ) {
                        ContentRoot()
                    }
                }
            }
        }
    }
}

