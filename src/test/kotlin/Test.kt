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
                val plano = Tag("plano", document)
                //println(document.children)

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
                val teste = Tag("apagar", document)

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
        fun addAttribute() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                fuc.addAttribute("codigo", "M4310")

                val fileName = "outputAddAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun removeAttribute() {
                val plano = Tag("plano", document)
                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                fuc.addAttribute("codigo", "M4310")

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
                        "    <fuc/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun changeAttribute() {
                val plano = Tag("plano", document)
                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\"/>\n" +
                        "</plano>"
                println("Antes de alterar o atributo:")
                println(antes)

                fuc.changeAttribute("codigo", "codigos", "1234")

                val fileName = "outputChangeAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigos=\"1234\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }


        //aceder a uma entidade é suposto ter uma função de teste? Se calhar devolver a lista dos seus filhos

        @Test
        fun accessEntityParent(){

        }

        @Test
        fun accessEntityChildren(){

        }

        @Test
        fun addAttributeGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                document.addAttributeGlobally("fuc", "codigo", "M4310")

                val fileName = "outputAddAttributeGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun renameAttributeGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)

                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\"/>\n" +
                        "</plano>"
                println("Antes de alterar o atributo:")
                println(antes)

                document.renameAttributeGlobally("fuc", "codigo", "codigos", "1234")

                val fileName = "outputRenameAttributeGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigos=\"1234\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun renameEntityGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                document.renameEntityGlobally("fuc", "fuc2")

                val fileName = "outputRenameEntityGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc2/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun removeEntityGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)
                val fuc = Tag("fuc", plano)

                document.removeEntityGlobally("fuc")

                val fileName = "outputRemoveEntityGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun removeAttributeGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", plano)
                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc codigo=\"M4310\">\n" +
                        "    </fuc>\n" +
                        "</plano>"
                println("Antes de apagar o atributo:")
                print(antes)

                document.removeAttributeGlobally("fuc", "codigo")

                val fileName = "outputRemoveAttributeGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "    <curso>Mestrado em Engenharia Informática</curso>\n" +
                        "    <fuc/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }
        @Test
        fun testPrettyPrint() {
                // Criar a estrutura XML conforme o exemplo fornecido
                val documento = Document("UTF-8", "1.0")
                val plano = Tag("plano", documento)

                val curso = Tag("curso", plano)
                Text("Mestrado em Engenharia Informática", curso)

                val fuc1 = Tag("fuc", plano)
                fuc1.addAttribute("codigo", "M4310")
                val nome1 = Tag("nome", fuc1)
                Text("Programação Avançada", nome1)
                val ects1 = Tag("ects", fuc1)
                Text("6.0", ects1)
                val avaliacao1 = Tag("avaliacao", fuc1)
                val componente1 = Tag("componente", avaliacao1)
                componente1.addAttribute("nome", "Quizzes")
                componente1.addAttribute("peso", "20%")
                val componente2 = Tag("componente", avaliacao1)
                componente2.addAttribute("nome", "Projeto")
                componente2.addAttribute("peso", "80%")

                val fuc2 = Tag("fuc", plano)
                fuc2.addAttribute("codigo", "03782")
                val nome2 = Tag("nome", fuc2)
                Text("Dissertação", nome2)
                val ects2 = Tag("ects", fuc2)
                Text("42.0", ects2)
                val avaliacao2 = Tag("avaliacao", fuc2)
                val componente3 = Tag("componente", avaliacao2)
                componente3.addAttribute("nome", "Dissertação")
                componente3.addAttribute("peso", "60%")
                val componente4 = Tag("componente", avaliacao2)
                componente4.addAttribute("nome", "Apresentação")
                componente4.addAttribute("peso", "20%")
                val componente5 = Tag("componente", avaliacao2)
                componente5.addAttribute("nome", "Discussão")
                componente5.addAttribute("peso", "20%")

                val xmlFormatado = documento.prettyPrint()

                val xmlEsperado = """
            <?xml version="1.0" encoding="UTF-8"?>
            <plano>
                <curso>Mestrado em Engenharia Informática</curso>
                <fuc codigo="M4310">
                    <nome>Programação Avançada</nome>
                    <ects>6.0</ects>
                    <avaliacao>
                        <componente nome="Quizzes" peso="20%"/>
                        <componente nome="Projeto" peso="80%"/>
                    </avaliacao>
                </fuc>
                <fuc codigo="03782">
                    <nome>Dissertação</nome>
                    <ects>42.0</ects>
                    <avaliacao>
                        <componente nome="Dissertação" peso="60%"/>
                        <componente nome="Apresentação" peso="20%"/>
                        <componente nome="Discussão" peso="20%"/>
                    </avaliacao>
                </fuc>
            </plano>
        """.trimIndent()
                assertEquals(xmlEsperado, xmlFormatado)


                val resultadoXPath = documento.microXPath("plano/fuc/avaliacao/componente")
                println(resultadoXPath)
                // Verificar se os elementos encontrados correspondem aos esperados
                assertEquals(5, resultadoXPath.size)
                assertEquals("Quizzes", resultadoXPath[0].value)
                assertEquals("Projeto", resultadoXPath[1].value)
                assertEquals("Dissertação", resultadoXPath[2].value)
                assertEquals("Apresentação", resultadoXPath[3].value)
                assertEquals("Discussão", resultadoXPath[4].value)
        }
}