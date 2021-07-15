import CSVHeader.Procedure_Name
import CSVHeader.Project_Name
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.*

//This enum class only serves as readable way to get the ordinal for the list entry in the CSV file
@Suppress("unused", "EnumEntryName")
enum class CSVHeader {
    Date, Time, Weld, Acceptability, Weld_Status, Procedure_Name, Procedure_Created_Date, Adjusted, Procedure_Description, Weld_Log_Description, PS_serial, PS_model, Programmer_Name, Welder_Name, Joint_Type_1, Material_Side_1, Heat_Code_Side_1, Diameter_Side_1, Wall_Side_1, Joint_Type_2, Material_Side_2, Heat_Code_Side_2, Diameter_Side_2, Wall_Side_2, Project_Name, Drawing_Name, Head_Type, Head_Serial, Electrode, Arc_Gap, Arc_Gap_Gauge, ID_Gas_Type, ID_Gas_Number, ID_Gas_Pressure, ID_Target_Flow, ID_Minimum_Flow, ID_Blast_Flow, ID_Purge_Method, Shield_Gas_Type, Shield_Gas_Number, Purge_OD_Flow, OD_Blast_Flow, Open_Field_1, Open_Field_2, Tack_Method_Setting, Button_pushed, AC_Input, Reserved1, Reserved2, Reserved3, Reserved4, Custom_field_value1, Interne_nummer, Custom_field_value3, Auftragsnummer
}
typealias CSVEntry = List<String>
typealias CSVGroupKey = Pair<String, String> // in this case we want to group CSV by project name and procedure name

fun processCSVDataFile(csvFile: File): Map<CSVGroupKey, List<CSVEntry>> {
    val csvStringWithoutPreamble = "\"Date" + csvFile.readText().substringAfter("Date")
    val csvData = csvReader().readAll(csvStringWithoutPreamble).let {
        it.subList(1, it.size) // Skip the first line as it is the header line}
    }
    return csvData.groupBy {
        Pair(it[Project_Name.ordinal], it[Procedure_Name.ordinal])
    }
}

fun processCSVNumberWPSFile(csvFile: File): Map<String, String> {
    val wpsMap = mutableMapOf<String, String>()
    csvFile.readLines().forEach { entry ->
        val contents = entry.split(";")
        wpsMap[contents[1]] = contents[0]
    }
    return wpsMap
}

fun generateKeyValueMap(groupKey: CSVGroupKey, eintrage: List<CSVEntry>): MutableMap<String, Any> {
    val (projectName, weldProgram) = groupKey
    val randomEintrag =
        eintrage[0] //Get some random eintrag to get the other field info, fields like auftragsnummer should be the same accross eintrage of the same project
    val keyValueMap = mutableMapOf<String, Any>()
    keyValueMap["project_name"] = projectName
    keyValueMap["auftragsnummer"] = randomEintrag[CSVHeader.Auftragsnummer.ordinal]
    keyValueMap["interne_nummer"] = randomEintrag[CSVHeader.Interne_nummer.ordinal]
    keyValueMap["mfst_ref"] = randomEintrag[CSVHeader.Welder_Name.ordinal]
    keyValueMap["zeichnung"] = randomEintrag[CSVHeader.Drawing_Name.ordinal]
    keyValueMap["maschinentyp"] = randomEintrag[CSVHeader.PS_model.ordinal]
    keyValueMap["schweissprogram"] = weldProgram
    keyValueMap["od_schutzgas"] = randomEintrag[CSVHeader.Shield_Gas_Type.ordinal]
    keyValueMap["serien_nr"] = randomEintrag[CSVHeader.PS_serial.ordinal]
    keyValueMap["schweisskopf"] = randomEintrag[CSVHeader.Head_Type.ordinal]
    keyValueMap["id_shutzgas"] = randomEintrag[CSVHeader.ID_Gas_Type.ordinal]
    keyValueMap["kalibrierdatum"] = randomEintrag[96] //Correspond to the last calibration column.
    keyValueMap["entries"] = eintrage
    return keyValueMap
}

fun convertToPDF(csvFile: File?, outputFolder: Path): Boolean {
    if (csvFile == null)
        return false
    //Initialize the ThymeleafEngine
    val templateResolver = ClassLoaderTemplateResolver()
    //Thymeleaf uses ClassLoader.getResourceAsStream method to locate the template file. This methods expects just a filename, so prefix / is enough. (and necessary)
    templateResolver.prefix = "/"
    templateResolver.suffix = ".html"
    val templateEngine = TemplateEngine()
    templateEngine.setTemplateResolver(templateResolver)
    val csvGrouped = processCSVDataFile(csvFile)
    val wpsMap = processCSVNumberWPSFile(File("WpsList.csv"))
    for ((groupKey, eintrage) in csvGrouped) {
        val (projectName, weldProgram) = groupKey
        val keyValueMap = generateKeyValueMap(groupKey, eintrage)
        //add wps number to the keyvalueMap
        keyValueMap["wpsNumber"] = wpsMap[groupKey.second] ?: "" //Processing will be done in HTML template
        val templateContext = Context(Locale.GERMAN, keyValueMap as Map<String, Any>?)
        templateContext.setVariable("standardDate", Date())
        val outputHTML = templateEngine.process("_base", templateContext)
        //The project name needs to be cleaned so that it doesn't contain special character for the pdf filename.
        val cleanedProjectName = projectName.replace("""\s|\\|/""".toRegex(), "_")
        val auftragsnummer = keyValueMap["auftragsnummer"]
        val interneNummer = keyValueMap["interne_nummer"]
        val pdfName = if (wpsMap[groupKey.second] != null) {
            "${cleanedProjectName}_${auftragsnummer}_${interneNummer}_${weldProgram}.pdf"
        } else {
            "WPS NICHT GEFUNDEN - ${cleanedProjectName}_${auftragsnummer}_${interneNummer}_${weldProgram}.pdf"
        }
        try {
            FileOutputStream("$outputFolder/$pdfName").use { os ->
                val builder = PdfRendererBuilder()
                builder.useFastMode()
                val baseURI = "file:" + {}::class.java.getResource("logo.png").file
                builder.withHtmlContent(outputHTML, baseURI) //Second arg just means path to resource folder
                builder.toStream(os)
                builder.run()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            return false
        }
    }
    return true
}
