
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import java.io.File
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter


/**
 * fileExtensionFilter null means directory mode
 */
@OptIn(ExperimentalStdlibApi::class)
@Composable
fun FilePickerButton(
    onFileChoosenChange: (File?) -> Unit,
    buttonText: String,
    iconFile: String,
    filterExtension: String? = null,
    color: Color = Color.Yellow
) {
    val filePicker = JFileChooser()
    if (filterExtension != null) {
        val filter = FileNameExtensionFilter("${filterExtension.uppercase()} files", "csv")
        filePicker.fileFilter = filter
    } else {
        filePicker.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    }

    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            when (filePicker.showOpenDialog(null)) {
                JFileChooser.APPROVE_OPTION -> {
                    onFileChoosenChange(filePicker.selectedFile)
                }
                else                        -> onFileChoosenChange(null)
            }
        }, colors = ButtonDefaults.buttonColors(backgroundColor = color)) {
            Text(
                text = buttonText,
            )
            Icon(
                painter = svgResource(iconFile),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun ContentRoot() {
    var fileChoosen: File? by remember { mutableStateOf(null) }
    val settings = Setting()
    val settingDefaultText = "The current output folder is ${settings.savePath}"
    val defaultText = "You have not choosen any file to convert."
    var statusTextState by remember { mutableStateOf(defaultText) }
    var currentSettingTextState by remember { mutableStateOf(settingDefaultText) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(title = {
            Text(
                "Swagelok Munich Weld Log Tool"
            )
        })
        Image(
            bitmap = imageResource("logo.png"), // ImageBitmap
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )

        FilePickerButton(onFileChoosenChange = {
            fileChoosen = it
            statusTextState =
                if (fileChoosen == null) defaultText else "You have choosen ${fileChoosen ?: "no file"} to convert."
        }, buttonText = "Select CSV  ", iconFile = "csv_file.svg", filterExtension = "csv")

        FilePickerButton(onFileChoosenChange = {
            settings.savePath = it?.toPath() ?: System.getProperty("user.dir").let { Paths.get(it) }
            currentSettingTextState = "The current output folder is ${settings.savePath}"
        }, buttonText = "Change Output Folder  ", iconFile = "folder.svg", filterExtension = null, color = Color.Green)

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    statusTextState = if (convertToPDF(fileChoosen, settings.savePath)) {
                        "You have successfully converted. The result is in ${settings.savePath}"
                    } else {
                        "Conversion failed. Make sure you are not opening any PDFs in the output folder while converting."
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Convert to PDF   ",
                )
                Icon(
                    painter = svgResource("start.svg"),
                    contentDescription = null
                )
            }
        }
        Text(modifier = Modifier.padding(16.dp), text = statusTextState)
        Text(modifier = Modifier.padding(16.dp), text = currentSettingTextState)
    }
}
