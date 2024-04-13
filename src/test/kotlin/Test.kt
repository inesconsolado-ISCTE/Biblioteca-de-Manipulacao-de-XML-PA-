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
        fun addEntity(){
                val plano = Tag("plano")
                document.addChild(plano)

                val fileName = "outputAddEntity.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<plano>\n</plano>"
                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun removeEntity(){
                val teste = Tag("apagar")
                document.addChild(teste)

                println("Com tag por apagar:")
                println(document.prettyPrint())

                document.removeChild(teste)

                val fileName = "outputRemoveEntity.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun addAttribute(){
                val plano = Tag("plano")
                document.addChild(plano)

                val curso = Tag("curso")
                plano.addChild(curso)

                Text("Mestrado em Engenharia Informática",curso)

                val fuc = Tag("fuc")
                fuc.addAttribute("codigo","M4310")
                plano.addChild(fuc)

                val fileName = "outputAddAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\">\n" +
                        "    </fuc>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun removeAttribute(){
                val plano = Tag("plano")
                document.addChild(plano)

                val curso = Tag("curso")
                plano.addChild(curso)

                Text("Mestrado em Engenharia Informática",curso)

                val fuc = Tag("fuc")
                fuc.addAttribute("codigo","M4310")
                plano.addChild(fuc)

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\">\n" +
                        "    </fuc>\n" +
                        "</plano>"
                println("Antes de apagar o atributo:")
                print(antes)

                fuc.removeAttribute("codigo")

                val fileName = "outputRemoveAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc>\n" +
                        "    </fuc>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun changeAttribute(){
                val plano = Tag("plano")
                document.addChild(plano)

                val curso = Tag("curso")
                plano.addChild(curso)

                Text("Mestrado em Engenharia Informática",curso)

                val fuc = Tag("fuc")
                fuc.addAttribute("codigo","M4310")
                plano.addChild(fuc)

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\">\n" +
                        "    </fuc>\n" +
                        "</plano>"
                println("Antes de alterar o atributo:")
                print(antes)

                fuc.changeAttribute("codigo","666")

                val fileName = "outputChangeAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"666\">\n" +
                        "    </fuc>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }


        //aceder a uma entidade é suposto ter uma função de teste? Se calhar devolver a lista dos seus filhos

        @Test
        fun addAttributeGlobally(){}

        @Test
        fun renameAttributeGlobally(){}

        @Test
        fun renameEntityGlobally(){}

        @Test
        fun removeEntityGlobally(){}

        @Test
        fun removeAttributeGlobally(){}


}