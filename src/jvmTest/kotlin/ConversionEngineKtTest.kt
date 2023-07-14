//
//import java.io.File
//
//internal class ConversionEngineKtTest {
//    companion object {
//        lateinit var WPSListFile: File
//
//        @BeforeAll
//        @JvmStatic
//        fun setUp() {
//            WPSListFile = File("WpsList.csv")
//        }
//    }
//
//    @org.junit.jupiter.api.Test
//    fun processCSVNumberWPSFile() {
//        val wpsMap = processCSVNumberWPSFile(WPSListFile)
//        assertEquals("WPS0001", wpsMap["1_8''x0,028 1Sektor"])
//    }
//}
