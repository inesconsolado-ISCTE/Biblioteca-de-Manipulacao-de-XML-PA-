import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class TestMapping {

    val map = Mapping()

    //se uma classe tem texto como filho: não pode ter mais filhos nenhuns

    @Test
    fun tagWithAttribute(){
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

        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun tagWithText(){  //tag que só vai ter texto como filho
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

        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)

    }

    @Test
    fun tagWithSimpleChildren(){ //tag com atributos e com tags vazias ou com texto
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

        val fuc = Fuc("1234","Programação Avançada", "ects")
        val createdXml = map.createClass(fuc, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc codigo=\"1234\">\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects codigo=\"2345\"/>\n" +
                "</fuc>"

        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun tagWithChildrenThatHasChildren(){
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
                "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                "\t\t<componente nome=\"Testes\" peso=\"80\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>"

        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun tagWithNestedTags(){

        val doc = Document("UTF-8", "1.0", "testRecursion")

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


        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun transformStrings(){
        val doc = Document("UTF-8", "1.0", "testStringTransformer")

        class AddPercentage: StringTransformer {
            override fun changeValue(original: String): String {
                return "$original%"
            }
        }

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

        println("Resultado expectado:\n$expectedXml\n")
        assertEquals(expectedXml, createdXml)
    }

    @Test
    fun adaptEntityParent(){
        val doc = Document("UTF-8", "1.0", "testAdaptEntity")

        class TestAdapter: Adapter{
            override fun adaptValue(tag: Tag) {
                tag.addAttribute("codigo", "1234")
            }
        }

        @Mapping.XmlTag("fuc")
        @Mapping.XmlAdapter(TestAdapter::class)
        class FUC(
            @Mapping.XmlTagText("nome")
            val nome: String,

            @Mapping.XmlTagText("ects")
            val ects: Double,
        )

        val f = FUC("Programação Avançada", 6.0)
        map.createClass(f, null, doc)

        val expectedAdaptedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc codigo=\"1234\">\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "</fuc>"

        val adaptedTag = map.processChanges(f, doc)
        assertEquals(expectedAdaptedXml, adaptedTag)

    }

    @Test
    fun adaptEntityParentV2(){
        val doc = Document("UTF-8", "1.0", "testAdaptEntity2")

        class TestAdapter: Adapter{
            override fun adaptValue(tag: Tag) {
                val atrs = tag.getAttributes().toMutableList()
                if (atrs.size > 1) {
                    val temp1 = atrs[0]
                    val temp2 = atrs[1]

                    tag.removeAttribute(temp1.name)
                    tag.removeAttribute(temp2.name)
                    tag.addAttribute(temp2.name, temp2.value)
                    tag.addAttribute(temp1.name, temp1.value)
                }
            }
        }

        @Mapping.XmlTag("fuc")
        @Mapping.XmlAdapter(TestAdapter::class)
        class FUC(
            @Mapping.XmlAttribute("nome")
            val nome: String,

            @Mapping.XmlAttribute("ects")
            val ects: String,
        )

        val f = FUC("Programação Avançada", "6.0")
        map.createClass(f, null, doc)

        val expectedAdaptedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc ects=\"6.0\" nome=\"Programação Avançada\"/>"

        val adaptedTag = map.processChanges(f, doc)
        assertEquals(expectedAdaptedXml, adaptedTag)
    }


    @Test
    fun adaptEntityChild(){

        val doc = Document("UTF-8", "1.0", "testAdaptChild")

        class TestAdapter: Adapter{
            override fun adaptValue(tag: Tag) {
                tag.addAttribute("codigo", "1234")
            }
        }

        @Mapping.XmlTag("componente")
        @Mapping.XmlAdapter(TestAdapter::class)
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
            val avaliacao: List<ComponenteAvaliacao>
        )

        val f = FUC("Programação Avançada", 6.0, listOf(ComponenteAvaliacao("Quizzes", 20),
            ComponenteAvaliacao("Testes", 80)))
        map.createClass(f, null, doc)

        val expectedAdaptedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<fuc>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20%\" codigo=\"1234\"/>\n" +
                "\t\t<componente nome=\"Testes\" peso=\"80%\" codigo=\"1234\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>"


        val adaptedTag = map.processChanges(f, doc)
        //assertEquals(expectedAdaptedXml, adaptedTag)
    }

}