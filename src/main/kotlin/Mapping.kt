import kotlin.reflect.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

class Mapping {

    private lateinit var document: Document

    private lateinit var createdClass: Any  //objeto que dão para criar uma classe e vai ser a tag parent ao lidar com esta classe
    fun setDocument(encode: String, version: String){
        val doc = Document(encode, version)
        this.document = doc
    }

    //value vai ser o nome associado à Tag
    @Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
    annotation class XmlTag(val value: String)  //Classe que não vai ter text como filho

    @Target(AnnotationTarget.PROPERTY)
    annotation class HasTagChildren  //Tag passada como argumento que vai ter filhos (lista de uma tag já criada)

    //o value vai ser o nome associado à Tag. O valor desse atributo tem de se ir buscar quando a classe for instanciada
    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlAttribute(val name: String)

    @Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
    annotation class XmlTagText(val value: String)  //Classe que cujo único filho é text
    //esta anotação e a primeira NÃO podem ser usadas em conjunto

    //@Target(AnnotationTarget.CLASS)
    //annotation class hasText  //Classe que cujo único filho é text

    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlText  //Child de uma tag que só tem text


    //Obter parâmetros pela ordem
    private val KClass<*>.classFields: List<KProperty<*>>
        get() {
            return primaryConstructor!!.parameters.map { p ->
                declaredMemberProperties.find { it.name == p.name }!!
            }
        }

    //fazer função para obter parâmetros que são atributos
    private fun getAttribute(kp: KProperty<*>): Attribute?{
        var attribute: Attribute? = null
        if(kp.hasAnnotation<XmlAttribute>()){
            val atrName = kp.findAnnotation<XmlAttribute>()!!.name
            val value = kp.getter.call(createdClass) as String
            attribute = Attribute(atrName, value)
        }
        return attribute
    }

    private fun setAttribute(field: KProperty<*>, tag: Tag): Array<String> {
        val atr = getAttribute(field)
        tag.addAttribute(atr!!.name, atr.value)
        return arrayOf(tag.value, "${tag.getAttributes()[0].name}=\"${tag.getAttributes()[0].value}\"")
    }

    //funçao que devolve tag que só tem text como filho
    private fun getTagText(clazz: KClass<*>, parent: Tag, kp: KProperty<*>): Tag?{
        var tag: Tag? = null
        if(kp.hasAnnotation<XmlTagText>()) {
                tag = tagWithText(clazz, parent, kp)
            }
        return tag
    }

    //devolve tag child VAZIA
    private fun getTagEmpty(clazz: KClass<*>, parent: Tag, kp: KProperty<*>): Tag?{
        var tag: Tag? = null
        if(kp.hasAnnotation<XmlTag>() && !kp.hasAnnotation<HasTagChildren>()) {
                val value = clazz.findAnnotation<XmlTag>()!!.value
                tag = Tag(value, document, parent)
            }
        return tag
    }

    private fun getTagWithChildren(){

    }


    //fazer função para obter parâmetros que é texto
    //temos de garantir que se for uma tag com texto TEM APENAS ESSE TEXTO NA CLASSFIELDS
    private fun setText(clazz: KClass<*>, tag: Tag, fieldText: KProperty<*>): Text{
        val value = fieldText.getter.call(createdClass) as String
        return Text(value, tag) //torna a tag parent deste text
    }


    //nesta função ler o nome da classe e os parametros dados e criar tags e atributos e texto?
    //esta função devolve coisas para os testes -> Ainda não funciona para escrever num documento
    fun createClass(futureTag: Any): Array<Any>? {
        createdClass = futureTag
        val clazz = createdClass::class
        val annotations = clazz.annotations
        //tipo de anotação da classe e age consoante isso
        var output: Array<Any>? = null
        annotations.forEach{
            when(it) {
                is XmlTag -> complexTag(clazz)
                is XmlTagText -> tagWithText(clazz, tag, clazz.classFields[0])
            }
        }
        return output
    }

    private fun tagWithText(clazz: KClass<*>, parent: Tag, field: KProperty<*>): Tag {
        val value = clazz.findAnnotation<XmlTagText>()!!.value
        val tagWithTextChild = Tag(value, document, parent)
        val text = setText(clazz, tagWithTextChild, field)
        return tagWithTextChild
    }

    //ainda só funciona caso tenha atributos mas não sei se funciona caso tenha outras tags
    private fun complexTag(clazz: KClass<*>){
        val value = clazz.findAnnotation<XmlTag>()!!.value
        //tag da classe que estão a criar
        val tag = Tag(value, document, null)     //ver do pai!!

        for(field in clazz.declaredMemberProperties) {
            val annotations = field.annotations
            annotations.forEach {
                when(it) {
                    is XmlTagText -> getTagText(clazz, tag, field)
                    is XmlAttribute -> setAttribute(field, tag)
                    is XmlText -> setText(clazz, tag, field)
                    is XmlTag -> if(field.hasAnnotation<HasTagChildren>()){

                    } else if (field.annotations.size == 1){
                        getTagEmpty(clazz, tag, field)
                    }
                }
            }
        }

        //tem de ver se tem atributos, se tem outras tags
        //ter uma função que dado uma tag, devolve todos os classFields marcados com essa Tag
        //usar nas funções criadas acima

    }



}

