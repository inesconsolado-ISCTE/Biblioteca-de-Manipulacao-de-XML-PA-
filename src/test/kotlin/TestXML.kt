import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Classe de testes para validar o comportamento do projeto.
 *
 * Esta classe contém casos de teste que verificam o correto funcionamento das funcionalidades implementadas
 * no ficheiro XML.kt.
 *
 *
 */
class TestXML {

        val document = Document("UTF-8", "1.0", "exemplo")

        @Test
        fun addEntity() {
                val plano = Tag("plano",document)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<plano/>"
                //println("Resultado expectado")
                //println(expectedXml)

                val actualXml = document.prettyPrint()

                assertEquals(expectedXml, actualXml)

                val curso = Tag("curso", document,plano)

                Text("Mestrado em Engenharia Informática", curso)

                val actualXml2 = document.prettyPrint()

                val expectedXml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "</plano>"

                assertEquals(expectedXml2, actualXml2)
        }

        @Test
        fun removeEntity() {
                val plano = Tag("plano",document)
                val teste = Tag("apagar", document,plano)

                //println("Com tag por apagar:")
                //println(document.prettyPrint())

                plano.removeChild(teste)

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<plano/>"
                //println("Resultado expectado")
                //println(expectedXml)

                assertEquals(expectedXml, document.prettyPrint())
        }

        @Test
        fun addAttribute() {
                val plano = Tag("plano", document)

                val curso = Tag("curso", document,plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc", document, plano)
                fuc.addAttribute("codigo", "M4310")

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                //println("Resultado expectado")
                //println(expectedXml)

                assertEquals(expectedXml, document.prettyPrint())

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
                //println("Antes de apagar o atributo:")
                //print(antes)

                fuc.removeAttribute("codigo")

                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc/>\n" +
                        "</plano>"

                //println("Resultado expectado")
                //println(expectedXml)

                assertEquals(expectedXml, document.prettyPrint())
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
                //println("Antes de alterar o atributo:")
                //println(antes)

                fuc.changeAttribute("codigo", "codigos", "1234")


                val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigos=\"1234\"/>\n" +
                        "</plano>"

                //println("Resultado expectado")
                //println(expectedXml)


                assertEquals(expectedXml, document.prettyPrint())
        }

        @Test
        fun accessEntityParent(){
                val plano = Tag("plano", document)
                val curso = Tag("curso",document, plano)

                Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                fuc.addAttribute("codigo", "M4310")

                val xmlTotal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                //println("Xml completo:")
                //println(xmlTotal)

                val expectedParent = curso.parent
                val expectedParent2 = fuc.parent

                assertEquals(expectedParent, plano)
                assertEquals(expectedParent2, plano)
                assertEquals(null,plano.parent)


        }

        @Test
        fun accessEntityChildren(){
                val plano = Tag("plano", document)
                val curso = Tag("curso",document, plano)

                val text = Text("Mestrado em Engenharia Informática", curso)

                val fuc = Tag("fuc",document, plano)
                fuc.addAttribute("codigo", "M4310")

                val xmlTotal = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                        "\t<fuc codigo=\"M4310\"/>\n" +
                        "</plano>"

                //println("Xml completo:")
                //println(xmlTotal)

                val planoChild = plano.getChildrenOfTag()
                val cursoChild = curso.getChildrenOfTag()
                val fucChild = fuc.getChildrenOfTag()

                val planoChildExpected :MutableList<XMLChild> = mutableListOf(curso, fuc)
                val cursoChildExpected :MutableList<XMLChild> = mutableListOf(text)
                val fucChildExpected :MutableList<XMLChild> = mutableListOf()

                assertEquals(planoChildExpected,planoChild)
                assertEquals(cursoChildExpected,cursoChild)
                assertEquals(fucChildExpected,fucChild)

        }

}