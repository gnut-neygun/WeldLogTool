
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Paths


@Composable
fun ContentRoot() {
    val scope = rememberCoroutineScope()
    var fileChoosen: File? by remember { mutableStateOf(null) }
    var isLoading: Boolean by remember { mutableStateOf(false) }
    val settings = Setting()
    val settingDefaultText = "The current output folder is ${settings.savePath}"
    val defaultText = "You have not chosen any file to convert."
    var statusTextState by remember { mutableStateOf(defaultText) }
    var currentSettingTextState by remember { mutableStateOf(settingDefaultText) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(title = {
            Text(
                "Swagelok Munich Weld Log Tool"
            )
        })
        Image(
            painter = painterResource("logo.png"), // ImageBitmap
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
                    scope.launch(Dispatchers.IO) {
                        isLoading = true
                        statusTextState = try {
                            convertToPDF(fileChoosen, settings.savePath)
                            "Conversion succeeded. The result is in ${settings.savePath}"
                        } catch (e: Exception) {
                            "Conversion failed. Make sure you are not opening any PDFs in the output folder while " +
                                    "converting. Error detail: ${e.message}"
                        }
                        isLoading = false
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text(
                    text = "Convert to PDF   ",
                )
                Icon(
                    painter = painterResource("start.svg"),
                    modifier = Modifier.size(24.dp),
                    contentDescription = null
                )
            }
        }
        Text(modifier = Modifier.padding(16.dp), text = statusTextState)
        Text(modifier = Modifier.padding(16.dp), text = currentSettingTextState)
        if (isLoading)
            CircularProgressIndicator()
    }
}
