import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class Setting {
    private val settingFileName = "settings.txt"
    var savePath: Path
        get() {
            return File(settingFileName).let { settingFile ->

                if (settingFile.exists()) {
                    settingFile.bufferedReader().readLines()[0].let { Paths.get(it) }
                } else {
                    val currentDir = System.getProperty("user.dir").let { Paths.get(it) }
                    currentDir
                }
            }
        }
        set(value) {
            File(settingFileName).writeText(value.toString())
        }
}
