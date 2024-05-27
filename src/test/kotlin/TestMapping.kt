import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class TestMapping {

    val map = Mapping()

    //se uma classe tem texto como filho: não pode ter mais filhos nenhuns

    @Test
    fun createClassWithAttribute(){     //tag só com atributos
        val doc = Document("UTF-8", "1.0", "testAttribute")

        @Mapping.XmlTag("fuc")
        class Fuc(
            @Mapping.XmlAttribute("codigo")
            val codigo: Int
        )

        val f = Fuc(1234)
        val createdXml = map.createClass(f, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc codigo=\"1234\"/>"

        println("Resultado expectado")
        println(expectedXml + "\n")

        assertEquals(expectedXml, createdXml)
        //assertEquals(expected.contentToString(), created.contentToString(), "Coisas erradas: ${created.contentToString()}")
    }

    @Test
    fun createClassWithChildTag(){

        val doc = Document("UTF-8", "1.0", "testChilds")

        @Mapping.XmlTag("componente")
        class ComponenteAvaliacao(
            @Mapping.XmlAttribute("nome")
            val nome: String,
            @Mapping.XmlAttribute("peso")
            val peso: Int
        )

        @Mapping.XmlTag("fuc")
        class FUC(
            @Mapping.XmlTagText("nome")
            val nome: String,

            @Mapping.XmlTagText("ects")
            val ects: Double,

            @Mapping.XmlTag("avaliacao")
            @Mapping.HasTagChildren
            val avaliacao: ComponenteAvaliacao
        )

        val f = FUC("Programação Avançada", 6.0, ComponenteAvaliacao("Quizzes", 20))
        var createdXml = map.createClass(f, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>"


        println("Resultado expectado")
        println(expectedXml)

        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun createClassWithListChildTag(){

        val doc = Document("UTF-8", "1.0", "testChilds")

        class AddPercentage: StringTransformer {
            override fun changeValue(original: String): String {
                return "$original%"
            }
        }

        class TestAdapter: Adapter{
            override fun adaptValue(tag: Tag) {
                tag.addAttribute("codigo", "1234")

            }
        }

        @Mapping.XmlAdapter(TestAdapter::class)
        @Mapping.XmlTag("componente")
        class ComponenteAvaliacao(
            @Mapping.XmlAttribute("nome")
            val nome: String,

            @Mapping.XmlString(AddPercentage::class)
            @Mapping.XmlAttribute("peso")
            val peso: Int
        )

        @Mapping.XmlTag("fuc")
        class FUC(
            @Mapping.XmlTagText("nome")
            val nome: String,

            @Mapping.XmlTagText("ects")
            val ects: Double,

            @Mapping.XmlTag("avaliacao")
            @Mapping.HasTagChildren
            val avaliacao: List<ComponenteAvaliacao>
        )

        val f = FUC("Programação Avançada", 6.0, listOf(ComponenteAvaliacao("Quizzes", 20),
            ComponenteAvaliacao("Testes", 80)))
        var createdXml = map.createClass(f, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Testes\" peso=\"80%\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>"


        println("Resultado expectado")
        println(expectedXml)

        println("Com adapter\n" + map.processChanges(f, doc))
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun createClassWithNestedTags(){

        val doc = Document("UTF-8", "1.0", "testChilds")

        @Mapping.XmlTag("Peso")
        class Pesos(
            @Mapping.XmlAttribute("peso")
            val peso: Int
        )


        @Mapping.XmlTag("Testes")
        class Testes(
            @Mapping.XmlAttribute("nome")
            val nome: String,

            @Mapping.XmlTag("pesos")
            @Mapping.HasTagChildren
            val pesos: List<Pesos>
        )

        @Mapping.XmlTag("componente")
        class ComponenteAvaliacao(
            @Mapping.XmlAttribute("nome")
            val nome: String,

            @Mapping.XmlTag("testes")
            @Mapping.HasTagChildren
            val t: List<Testes>
        )

        @Mapping.XmlTag("fuc")
        class FUC(
            @Mapping.XmlTagText("nome")
            val nome: String,

            @Mapping.XmlTagText("ects")
            val ects: Double,

            @Mapping.XmlTag("avaliacao")
            @Mapping.HasTagChildren
            val avaliacao: List<ComponenteAvaliacao>
        )

        val f = FUC("Programação Avançada", 6.0, listOf(ComponenteAvaliacao("Quizzes", listOf(Testes("teste1", listOf(Pesos(1))))),
            ComponenteAvaliacao("Testes", listOf(Testes("teste2", listOf(Pesos(2))))),
        ))
        var createdXml = map.createClass(f, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente nome=\"Quizzes\">\n" +
                "\t\t\t<testes>\n" +
                "\t\t\t\t<Testes nome=\"teste1\">\n" +
                "\t\t\t\t\t<pesos>\n" +
                "\t\t\t\t\t\t<Peso peso=\"1\"/>\n" +
                "\t\t\t\t\t</pesos>\n" +
                "\t\t\t\t</Testes>\n" +
                "\t\t\t</testes>\n" +
                "\t\t</componente>\n" +
                "\t\t<componente nome=\"Testes\">\n" +
                "\t\t\t<testes>\n" +
                "\t\t\t\t<Testes nome=\"teste2\">\n" +
                "\t\t\t\t\t<pesos>\n" +
                "\t\t\t\t\t\t<Peso peso=\"2\"/>\n" +
                "\t\t\t\t\t</pesos>\n" +
                "\t\t\t\t</Testes>\n" +
                "\t\t\t</testes>\n" +
                "\t\t</componente>\n" +
                "\t</avaliacao>\n" +
                "</fuc>"


        println("Resultado expectado")
        println(expectedXml)

        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun createClassWithText(){  //tag que só vai ter texto como filho
        val doc = Document("UTF-8", "1.0", "testTagText")

        @Mapping.XmlTagText("nome")
        class Nome(
            @Mapping.XmlText
            val uc: String,

            @Mapping.XmlAttribute("codigo")
            val codigo: Int
        )

        val nome = Nome("Programação Avançada", 1234)
        var createdXml = map.createClass(nome, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<nome codigo=\"1234\">Programação Avançada</nome>"

        println("Resultado expectado")
        println(expectedXml)

        assertEquals(expectedXml, createdXml)

    }

    //tag com atributos e com tags vazias ou com texto
    @Test
    fun classWithSimpleTags(){
        val doc = Document("UTF-8", "1.0", "testSimpleTags")

        @Mapping.XmlTag("fuc")
        class Fuc(
            @Mapping.XmlAttribute("codigo")
            val codigo: String,

            @Mapping.XmlTagText("nome")
            val uc: String,

            @Mapping.XmlTag("ects")
            @Mapping.ChildWithAttribute("codigo", "2345")
            val ects: Any,
        )

        val fuc = Fuc("1234","Programação Avançada", 0)
        var createdXml = map.createClass(fuc, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc codigo=\"1234\">\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects/>\n" +
                "</fuc>"

        println("Resultado expectado")
        println(expectedXml)

        assertEquals(expectedXml, createdXml)
    }

}