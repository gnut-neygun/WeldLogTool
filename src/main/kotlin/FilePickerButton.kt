import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import java.io.File
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
