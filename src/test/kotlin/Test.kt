import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

class Test {


        val document = Document("UTF-8", "1.0", "exemplo")

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

        @Test
        fun removeAttribute() {
                val plano = Tag("plano", document)
                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\">\n" +
                        " \t</fuc>\n" +
                        "</plano>"
                println("Antes de apagar o atributo:")
                print(antes)

                fuc.removeAttribute("codigo")

                val fileName = "outputRemoveAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun changeAttribute() {
                val plano = Tag("plano", document)
                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"
                println("Antes de alterar o atributo:")
                println(antes)

                fuc.changeAttribute("codigo", "codigos", "1234")

                val fileName = "outputChangeAttribute.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigos=\"1234\"/>\n" +
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

                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                document.addAttributeGlobally("fuc", "codigo", "M4310")

                val fileName = "outputAddAttributeGlobally.xml"
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

        @Test
        fun renameAttributeGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)

                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"
                println("Antes de alterar o atributo:")
                println(antes)

                document.renameAttributeGlobally("fuc", "codigo", "codigos", "1234")

                val fileName = "outputRenameAttributeGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigos=\"1234\"/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun renameEntityGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                document.renameEntityGlobally("fuc", "fuc2")

                val fileName = "outputRenameEntityGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc2/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }

        @Test
        fun removeEntityGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)
                val fuc = Tag("fuc",document, plano)

                document.removeEntityGlobally("fuc")

                val fileName = "outputRemoveEntityGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)

        }

        @Test
        fun removeAttributeGlobally() {
                val plano = Tag("plano", document)

                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                fuc.addAttribute("codigo", "M4310")

                val antes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\">\n" +
                        "\t</fuc>\n" +
                        "</plano>"
                println("Antes de apagar o atributo:")
                print(antes)

                document.removeAttributeGlobally("fuc", "codigo")

                val fileName = "outputRemoveAttributeGlobally.xml"
                document.writeToFile(fileName)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc/>\n" +
                        "</plano>"

                println("Resultado expectado")
                println(expectedXml)

                val actualXml = File(fileName).readText()

                assertEquals(expectedXml, actualXml)
        }
        @Test
        fun testPrettyPrint() {
                // Criar a estrutura XML conforme o exemplo fornecido
                val documento = Document("UTF-8", "1.0","exemplo2")
                val plano = Tag("plano", documento)

                val curso = Tag("curso",document, plano)
                Text("Mestrado em Engenharia Informática", curso)

                val fuc1 = Tag("fuc",document, plano)
                fuc1.addAttribute("codigo", "M4310")
                val nome1 = Tag("nome",document, fuc1)
                Text("Programação Avançada", nome1)
                val ects1 = Tag("ects",document, fuc1)
                Text("6.0", ects1)
                val avaliacao1 = Tag("avaliacao",document, fuc1)
                val componente1 = Tag("componente",document, avaliacao1)
                componente1.addAttribute("nome", "Quizzes")
                componente1.addAttribute("peso", "20%")
                val componente2 = Tag("componente",document, avaliacao1)
                componente2.addAttribute("nome", "Projeto")
                componente2.addAttribute("peso", "80%")

                val fuc2 = Tag("fuc",document, plano)
                fuc2.addAttribute("codigo", "03782")
                val nome2 = Tag("nome",document, fuc2)
                Text("Dissertação", nome2)
                val ects2 = Tag("ects",document, fuc2)
                Text("42.0", ects2)
                val avaliacao2 = Tag("avaliacao",document, fuc2)
                val componente3 = Tag("componente",document,avaliacao2)
                componente3.addAttribute("nome", "Dissertação")
                componente3.addAttribute("peso", "60%")
                val componente4 = Tag("componente",document, avaliacao2)
                componente4.addAttribute("nome", "Apresentação")
                componente4.addAttribute("peso", "20%")
                val componente5 = Tag("componente",document,avaliacao2)
                componente5.addAttribute("nome", "Discussão")
                componente5.addAttribute("peso", "20%")

                val xmlFormatado = documento.prettyPrint()

                val xmlEsperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\">\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<avaliacao>\n" +
                        "\t\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t\t</avaliacao>\n" +
                        "\t</fuc>\n" +
                        "\t<fuc codigo=\"03782\">\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<avaliacao>\n" +
                        "\t\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t\t</avaliacao>\n" +
                        "\t</fuc>\n" +
                        "</plano>"

                assertEquals(xmlEsperado, xmlFormatado)


                val resultadoXPath = documento.microXPath("plano/fuc/avaliacao/componente")

                val resultadoXPathesperado = "<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "<componente nome=\"Discussão\" peso=\"20%\"/>\n"


                assertEquals(resultadoXPath,resultadoXPathesperado)

        }

}