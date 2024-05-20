import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class Test {

        //criar documento XML para testar
        //tecnicamente não é um ficheiro temporário porque nao apago depois de usar mas dps n dava para confirmar
        //as teorias do porque nao estar a funcionar, quando revermos isto em conjunto meto para apagar mas por
        //enquanto deixamos os ficheiros xml para confirmar as coisas
        val document = Document("UTF-8", "1.0")

        @Test
        fun addEntity() {
                val plano = Tag("plano",document)
                //document.setRootTag(plano)

                val fileName = "outputAddEntity.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<plano/>"
                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun removeEntity() {
                val plano = Tag("plano",document)
                val teste = Tag("apagar", document,plano)

                println("Com tag por apagar:")
                println(document.prettyPrint())

                plano.removeChild(teste)

                val fileName = "outputRemoveEntity.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<plano/>"
                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun addAttribute() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", document,plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", document, plano)
                fuc.addAttribute("codigo", "M4310")

                val fileName = "outputAddAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }
}