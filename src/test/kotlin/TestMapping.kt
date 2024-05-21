import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class TestMapping {

    val map = Mapping()
    val doc = Document("UTF-8", "1.0")

    //Ainda não há documentos, nem parents e child Tags (a menos que se criem diretamente ao criar uma claa(Tag))

    //se uma classe tem texto como filho: não pode ter mais filhos nenhuns

    @Test
    fun createClassWithAttribute(){
        @Mapping.XmlTag("fuc")
        class Fuc(
            @Mapping.XmlAttribute("codigo")
            val codigo: String
        )

        val f = Fuc("1234")
        var created = map.createClass(f, doc)
        var expected = arrayOf("fuc",  "codigo=\"1234\"")
        assertEquals(expected.contentToString(), created.contentToString(), "Coisas erradas: ${created.contentToString()}")
    }

    @Test
    fun createClassWithChildTag(){
        @Mapping.XmlTag("fuc")
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


    }

    @Test
    fun createClassWithText(){
        @Mapping.XmlTagText("nome")
        class FUC(
            @Mapping.XmlText
            val uc: String,
        )

        val f = FUC("Programação Avançada")
        var created = map.createClass(f, doc)
        var expected = arrayOf("nome",  "Programação Avançada")
        assertEquals(expected.contentToString(), created.contentToString(), "Coisas erradas: ${created.contentToString()}")

    }

}
