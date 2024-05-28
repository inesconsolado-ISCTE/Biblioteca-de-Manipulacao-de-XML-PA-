

    //cria documento
    fun document(encode: String, version: String, name: String, build: Document.() -> Unit) {
        Document(encode, version, name).apply {
            build(this)
        }
    }

    fun Document.root(name: String, build: Tag.() -> Unit) =
        this.setRootTag(Tag(name, this, null).apply {
        build(this)
    })


    //extensão para adicionar uma tag a uma tag (como filha)
    fun Tag.tag(name: String, build: Tag.() -> Unit) =
        Tag(name, this.doc, this).apply {
            build(this)
        }


    //extensão para adicionar texto a uma tag
    fun Tag.textInTag(text: String) = Text(text,this)


    //Operadores:

    // sintaxe de acesso […] (get)

    //define que o operador /  resulta na devolução de um filho com dado nome (div)

    //Infix:

    //adicionar atributo a uma tag

    //alterar string de um atributo


    fun main(){

        val doc = document("UTF-8", "1.0", "DSL") {
            this.root("fuc") {
                this.tag("uc") {
                    this.textInTag("Programação Avançada")
                }
                this.tag("ects"){}
            }
        }

        println(doc)
    }

