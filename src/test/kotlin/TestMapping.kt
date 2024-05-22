import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.io.File

internal class TestMapping {

    val map = Mapping()

    //se uma classe tem texto como filho: não pode ter mais filhos nenhuns

    @Test
    fun createClassWithAttribute(){     //tag só com atributos
        val doc = Document("UTF-8", "1.0", "testAttribute")

        @Mapping.XmlTag("fuc")
        class Fuc(
            @Mapping.XmlAttribute("codigo")
            val codigo: String
        )

        val f = Fuc("1234")
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
    /*    @Mapping.XmlTag("fuc")
        class FUC(
            @Mapping.XmlTagText("nome")
            val nome: String,

            @Mapping.XmlTagText("ects")
            val ects: Double,

            @Mapping.XmlTag("avaliação")
            @Mapping.HasTagChildren
            val avaliacao: List<componentes>
        )

        val f = FUC("Programação Avançada", 6.0, )

*/
    }

    @Test
    fun createClassWithText(){  //tag que só vai ter texto como filho
        val doc = Document("UTF-8", "1.0", "testTagText")

        @Mapping.XmlTagText("nome")
        class Nome(
            @Mapping.XmlText
            val uc: String,
        )

        val nome = Nome("Programação Avançada")
        var createdXml = map.createClass(nome, null, doc)

        val expectedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<nome>Programação Avançada</nome>"

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
