import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
/**
 * Classe de testes para validar o comportamento do projeto.
 *
 * Esta classe contém casos de teste que verificam o correto funcionamento das funcionalidades implementadas
 * no ficheiro Document.kt.
 *
 *
 */
class TestDocument {

    val document = Document("UTF-8", "1.0", "exemplo")

    @Test
    fun addAttributeGlobally() {
        val plano = Tag("plano", document)

        val curso = Tag("curso",document, plano)

        Text("Mestrado em Engenharia Informática", curso)

        val fuc = Tag("fuc",document, plano)
        document.addAttributeGlobally("fuc", "codigo", "M4310")

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc codigo=\"M4310\"/>\n" +
                "</plano>"

        //println("Resultado expectado")
        //println(expectedXml)

        Assertions.assertEquals(expectedXml, document.prettyPrint())
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
        //println("Antes de alterar o atributo:")
        //println(antes)

        document.renameAttributeGlobally("fuc", "codigo", "codigos", "1234")

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc codigos=\"1234\"/>\n" +
                "</plano>"

        println("Resultado expectado")
        println(expectedXml)

        Assertions.assertEquals(expectedXml, document.prettyPrint())

    }

    @Test
    fun renameEntityGlobally() {
        val plano = Tag("plano", document)

        val curso = Tag("curso",document, plano)

        Text("Mestrado em Engenharia Informática", curso)

        val fuc = Tag("fuc",document, plano)
        document.renameEntityGlobally("fuc", "fuc2")

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc2/>\n" +
                "</plano>"

        //println("Resultado expectado")
        //println(expectedXml)

        Assertions.assertEquals(expectedXml, document.prettyPrint())
    }

    @Test
    fun removeEntityGlobally() {
        val plano = Tag("plano", document)

        val curso = Tag("curso",document, plano)

        Text("Mestrado em Engenharia Informática", curso)
        val fuc = Tag("fuc",document, plano)

        document.removeEntityGlobally("fuc")

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "</plano>"

        //println("Resultado expectado")
        //println(expectedXml)

        Assertions.assertEquals(expectedXml, document.prettyPrint())

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
        //println("Antes de apagar o atributo:")
        //print(antes)

        document.removeAttributeGlobally("fuc", "codigo")

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc/>\n" +
                "</plano>"

        //println("Resultado expectado")
        //println(expectedXml)

        Assertions.assertEquals(expectedXml, document.prettyPrint())
    }

    @Test
    fun testPrettyPrint() {
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

        Assertions.assertEquals(xmlEsperado, xmlFormatado)
    }

    @Test
    fun testMicroXPath() {

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

        val resultadoXPath = documento.microXPath("fuc/avaliacao/componente")

        val resultadoXPathesperado = mutableListOf<XMLChild>(componente1,componente2,componente3,componente4,componente5)

        Assertions.assertEquals(resultadoXPath, resultadoXPathesperado)
    }
}