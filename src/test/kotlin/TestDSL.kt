import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Classe de testes para validar o comportamento do projeto.
 *
 * Esta classe contém casos de teste que verificam o correto funcionamento das funcionalidades implementadas
 * nos ficheiros Document.kt e XML.kt, relativas à DSL interna.
 *
 *
 */

class TestDSL {

    private val document = Document("UTF-8", "1.0", "exemplo")

    val xmlLookAlike = document.tag("plano") {
        this.tag("fuc") {
            this.tag("ects") {
                this.textInTag("6 ects")
            }
        }
    }
    @Test
    fun dsl() {

        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<fuc>\n" +
                "\t\t<ects>6 ects</ects>\n" +
                "\t</fuc>\n" +
                "</plano>"

        assertEquals(document.prettyPrint(), expected)

    }

    @Test
    fun testInfix(){
        val fuc = document.findTag("fuc")
        if (fuc != null) {
            fuc addattribute Attribute("codigo", "1234")
            fuc addattribute Attribute("uc", "PA")
        }
        println("Depois de adicionar atributos: \n" + document.prettyPrint())

        if (fuc != null) {
            fuc deleteattribute Attribute("codigo", "1234")
        }

        println("\nDepois de remover atributos: \n" + document.prettyPrint())

        val expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<fuc uc=\"PA\">\n" +
                "\t\t<ects>6 ects</ects>\n" +
                "\t</fuc>\n" +
                "</plano>"

        assertEquals(document.prettyPrint(), expected)

    }

}