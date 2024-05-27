import java.io.File
import kotlin.reflect.*
import kotlin.reflect.full.*

interface StringTransformer{
    fun changeValue(original: String): String   //para receber uma String então tenho de implementar isto no sitio em que os valores já tenham sido convertidos para String
}

interface Adapter{
    fun adaptValue(tag: Tag)
}

class Mapping {

    private lateinit var document: Document

    private lateinit var createdClass: Any  //objeto que dão para criar uma classe e vai ser a tag parent ao lidar com esta classe

    private fun setDocument(doc: Document){
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

    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlText  //Child de uma tag que só tem text

    @Target(AnnotationTarget.PROPERTY)
    annotation class ChildWithAttribute(val name: String, val value: String)    //caso uma tag child que está a ser criada vá ter atributos. Se quiserem 2 atributos usar 2x?

    //pretende-se uma anotação para indicar uma classe que
    //implementa a transformação a fazer à string por omissão (p.e. acrescentar “%”).
    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlString(val transformerC: KClass<out StringTransformer>)

    @Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
    annotation class XmlAdapter(val adapterC: KClass<out Adapter>)

    private val KClass<*>.getTextField: List<KProperty<*>>  //tentar sem lista
        get() {
            return declaredMemberProperties.filter { property ->
                property.annotations.any { it is XmlText }
            }
        }

    private val KClass<*>.getAttrFields: List<KProperty<*>>
        get() {
            return declaredMemberProperties.filter { property ->
                property.annotations.any { it is XmlAttribute }
            }
        }

    val KClass<*>.classFields: List<KProperty<*>>
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
            val value = typeToString(kp.getter.call(createdClass))
            attribute = Attribute(atrName, value)
        } else
            if(kp.hasAnnotation<ChildWithAttribute>()){     //adiciona tbm os atributos das tags filhas da principal
            kp.annotations.filterIsInstance<ChildWithAttribute>().map {
                attribute = Attribute(it.name, it.value)
            }
        }
        return attribute
    }

    private fun setAttribute(field: KProperty<*>, tag: Tag): Array<String> {
        val atr = getAttribute(field)
        if(field.hasAnnotation<XmlString>()){
            val clazz = field.findAnnotation<XmlString>()!!.transformerC
            val valueToChange = atr!!.value
            val transformerInstance = clazz.java.getDeclaredConstructor().newInstance() as StringTransformer
            val newValue = transformerInstance.changeValue(valueToChange)
            atr.value = newValue
        }
        tag.addAttribute(atr!!.name, atr.value)
        return arrayOf(tag.value, "${tag.getAttributes()[0].name}=\"${tag.getAttributes()[0].value}\"")
    }

    private fun addAttributeToTag(clazz: KClass<*>, tag: Tag) {
        if(clazz.getAttrFields.isNotEmpty()){
            clazz.getAttrFields.forEach {setAttribute(it, tag)}
        }
    }

    //devolve tag child VAZIA
    private fun getTagEmpty(parent: Tag, kp: KProperty<*>): Tag?{
        val value = kp.findAnnotation<XmlTag>()!!.value
        var tag = Tag(value, document, parent)
        if(kp.hasAnnotation<ChildWithAttribute>()){
            setAttribute(kp, tag)
        }
        return tag
    }

    private fun getTagWithChildren(parent: Tag, kp: KProperty<*>){
        //cria-se tag com getTagEmpty
        var tag = getTagEmpty(parent, kp)
        //vai se buscar com um getter o type do parâmetro, se for um iterable percorre-se, se for outra coisa, cria-se só um
        if(kp.returnType.classifier.let { it is KClass<*> && it.isSubclassOf(Iterable::class) }){   //ver se é uma lista ou algo que se tem de percorrer e criar vários
            val items = kp.getter.call(createdClass) as? Iterable<*>
            items?.forEach { item ->
                if (item != null) {
                    println(createClass(item, tag, document))  // Processa cada item da coleção
                }
            }
        } else if(kp.returnType.classifier.let { it is KClass<*>}){      //ver se é apenas um, criar só um objeto desse tipo
            var newTag = kp.getter.call(createdClass)
            //var newClass = kp.returnType
            if (newTag != null) {
                println(createClass(newTag, tag, document))  //vai processar a classe que se está a instanciar aqui
            }
        }
    }

    //funçao que devolve tag que só tem text como filho
    private fun tagChildWithText(clazz: KClass<*>, parent: Tag?, kp: KProperty<*>): Tag?{
        val tagWithText: Tag? = null
        if(kp.hasAnnotation<XmlTagText>()) {
            val value = kp.findAnnotation<XmlTagText>()!!.value
            val tagWithText = Tag(value, document, parent)
            setText(clazz, tagWithText, kp)
            if(kp.hasAnnotation<ChildWithAttribute>()) {
                setAttribute(kp, tagWithText)
            }
        }
        return tagWithText
    }

    //fazer função para obter parâmetros que é texto
    //temos de garantir que se for uma tag com texto TEM APENAS ESSE TEXTO NA CLASSFIELDS
    //Adiciona texto posteriormente
    private fun setText(clazz: KClass<*>, tag: Tag, fieldText: KProperty<*>): Text{
        val value = typeToString(fieldText.getter.call(createdClass))
        return Text(value, tag) //torna a tag parent deste text
    }

    private fun simpleTagText(clazz: KClass<*>, parent: Tag?, field: KProperty<*>): Tag {
        val value = clazz.findAnnotation<XmlTagText>()!!.value
        val tagWithTextChild = Tag(value, document, parent)
        setText(clazz, tagWithTextChild, field)
        addAttributeToTag(clazz, tagWithTextChild)
        return tagWithTextChild
    }


    //nesta função ler o nome da classe e os parametros dados e criar tags e atributos e texto?
    //esta função devolve coisas para os testes -> Ainda não funciona para escrever num documento
    fun createClass(futureTag: Any, parent: Tag?, doc: Document): String {
        setDocument(doc)
        createdClass = futureTag
        val clazz = createdClass::class
        val annotations = clazz.annotations
        //tipo de anotação da classe e age consoante isso

        if (parent == null && doc.isRootTagInitialized()) {
            throw IllegalStateException("A root tag já foi definida para este documento.")
        }

        // Criar a nova tag
        annotations.forEach { annotation ->
            when (annotation) {
                is XmlTag -> complexTag(clazz, parent)
                is XmlTagText -> simpleTagText(clazz, parent, clazz.getTextField[0])
            }
        }
        return writeInDoc()
    }


    //é suposto chamar isto quando se usa o XmlAdapter
    fun processChanges(actualTag: Any, doc: Document): String{
        val clazz = actualTag::class
        if(clazz.hasAnnotation<XmlTag>() || clazz.hasAnnotation<XmlTagText>()) {
            val annotations = clazz.annotations
            val tagName = annotations.mapNotNull {
                when(it) {
                    is XmlTagText -> it.value
                    is XmlTag -> it.value
                    else -> null
                }
            }
            if(clazz.hasAnnotation<XmlAdapter>()) {
                if (doc.findTag(tagName.get(0)) != null) {
                    val newTag = doc.findTag(tagName.get(0))
                    val adapterInstance = clazz.java.getDeclaredConstructor().newInstance() as Adapter
                    newTag?.let { adapterInstance.adaptValue(it) }
                }
            }
        }
        return writeInDoc()
    }


    //ainda só funciona caso tenha atributos mas não sei se funciona caso tenha outras tags
    private fun complexTag(clazz: KClass<*>, parent: Tag?): Tag{
        val value = clazz.findAnnotation<XmlTag>()!!.value
        //tag da classe que estão a criar
        val tag = Tag(value, document, parent)


        for(field in clazz.classFields) {
            val annotations = field.annotations
            annotations.forEach {
                when(it) {
                    is XmlTagText -> tagChildWithText(clazz, tag, field)
                    is XmlAttribute -> setAttribute(field, tag)
                    is XmlText -> setText(clazz, tag, field)
                    is XmlTag -> if(field.hasAnnotation<HasTagChildren>()){
                        getTagWithChildren(tag, field)
                    } else if (field.annotations.size == 1){    //mudar para ter childwithAttr
                        getTagEmpty(tag, field)
                    }
                }
            }
        }
        return tag
    }

    private fun writeInDoc(): String {
        val fileName = document.getDocName()
        document.writeToFile(fileName)
        val actualXml = File(fileName).readText()
        return actualXml
    }

    //TODO: possibilidade de adicionar e alterar coisas (atributos, textos, filhos) e remover sem usar diretamente a biblioteca; limitar uso FAZER REGRAS

    private fun typeToString(value: Any?): String {
        return when (value) {
            null -> "null"
            is String -> value
            is Char -> "'$value'"
            is Number, is Boolean -> value.toString()
            is Array<*> -> value.joinToString(prefix = "[", postfix = "]", transform = this::typeToString)
            is List<*> -> value.joinToString(prefix = "[", postfix = "]", transform = this::typeToString)
            is Set<*> -> value.joinToString(prefix = "{", postfix = "}", transform = this::typeToString)
            is Map<*, *> -> value.entries.joinToString(prefix = "{", postfix = "}") { (k, v) -> "${typeToString(k)}: ${typeToString(v)}" }
            else -> value.toString()
        }
    }
}