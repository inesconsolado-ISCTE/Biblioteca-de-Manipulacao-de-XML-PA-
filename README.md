# Biblioteca de Manipulação de XML
### Geração e manipulação de XML 
### Projeto de Inês Consolado, nº 93040 e Mariana Guerreiro, nº 99110
 
  ## Instalação
De modo a conseguir utilizar o projeto terá de ter as bibliotecas:
```sh
org.jetbrains.kotlin:kotlin-reflect:1.9.23
org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22
 ```
No projeto foram consideradas as seguintes 4 data classes:
- **Document**: recebe o encode (String), a versão (String) e o nome do documento (String). Implementa o padrão de desenho Visitor.
- **Tag**: recebe o próprio nome (String), o documento que pertence (Document) e o seu parent, caso o tenha (Tag). Caso a Tag não tenha parent assume-se que será a rootTag do Document (sendo esta única). Implementa o padrão de desenho Visitor e o XMLChild que representa os elemntos que podem ser filhos de Tag's e Document's.
- **Text**: recebe o texto a ser inserido (String) e a sua Tag parent (Tag). Implementa XMLChild.
- **Attribute**: recebe o nome do atributo (String) e o valor do mesmo (String).



## Features da Biblioteca
Objetivo: Desenvolver as classes para representar XML em memória (modelo), operações
de manipulação, e testes.

De modo a exemplificar as funcionalidades implementadas nesta fase tomaremos por exemplo o xml:
```sh
<?xml version="1.0" encoding="UTF-8"?>
<plano>
    <curso>Mestrado em Engenharia Informática</curso>
        <fuc codigo="M4310">
            <nome>Programação Avançada</nome>
            <ects>6.0</ects>
            <avaliacao>
                <componente nome="Quizzes" peso="20%"/>
                <componente nome="Projeto" peso="80%"/>
            </avaliacao>
        </fuc>
        <fuc codigo="03782">
            <nome>Dissertação</nome>
            <ects>42.0</ects>
            <avaliacao>
                <componente nome="Dissertação" peso="60%"/>
                <componente nome="Apresentação" peso="20%"/>
                <componente nome="Discussão" peso="20%"/>
            </avaliacao>
        </fuc>
</plano>
```
Assumindo que o documento e a sua RootTag são criados da seguinte forma:
```sh
val document = Document("UTF-8", "1.0", "exemplo")
-> (encode,versão, nome_do_documento)

val plano = Tag("plano",document)
-> (nome_da_tag, nome_do_documento)
```

- Adicionar entidade
```sh
val curso = Tag("curso", document,plano)
-> (na criação de uma Tag adicionar o parent no último parâmetro)
```
- Remover entidade
```sh
plano.removeChild(curso)
-> (Tag)
```

- Adicionar atributo
```sh
val fuc = Tag("fuc",document, plano)
fuc.addAttribute("codigo", "M4310") 
-> (nome_do_atributo, valor_do_atributo)
```

- Remover atributo
```sh
fuc.removeAttribute("codigo") 
-> (nome_do_atributo)
```

- Alterar atributo
```sh
fuc.changeAttribute("codigo", "codigos", "1234") 
-> (nome_do_atributo_antigo, novo_nome_do_atributo, novo_valor_do_atributo), sendo o último parâmetro opcional
```

- Aceder à entidade mãe
```sh
fuc.parent
```

- Aceder a entidades filhas
```sh
plano.getChildrenOfTag()
```

- Pretty print em formato de String
```sh
document.prettyPrint()
```

- Escrita para ficheiro
```sh
document.writeToFile("fileName")
```

- Adicionar atributos globalmente ao documento
```sh
document.addAttributeGlobally("fuc", "codigo", "M4310")
-> (nome_da_Tag_a_adicionar_o_atributo, nome_do_atributo, valor_do_atributo)
-> Neste caso adiciona o Attribute dado a todas as Tags que tenham o nome "fuc"
```
- Renomear entidades globalmente ao documento
```sh
document.renameEntityGlobally("fuc", "fuc2")
-> (nome_antigo_da_Tag, nome_novo_da_Tag)
-> Neste caso renomeia todas as Tags que tenham "fuc" para "fuc2"
```
- Renomear atributos globalmente ao documento
```sh
document.renameAttributeGlobally("fuc", "codigo", "codigos", "1234")
-> (nome_da_Tag, nome_antigo_do_atributo,nome_novo_do_atributo, novo_valor_do_atributo)
-> Neste caso renomeia todos os Attribute "codigo" para "codigos" em todas as Tags que o possuem. Como inclui o último parâmetro (opcional) também altera o seu valor para "1234"
```
- Remover entidades globalmente ao documento
```sh
 document.removeEntityGlobally("fuc")
 -> Neste caso remove todas as Tags que tenham o nome "fuc"
```
- Remover atributos globalmente ao documento
```sh
document.removeAttributeGlobally("fuc", "codigo")
-> Neste caso remove todos os Attributes "codigo" das Tags que tenham o nome "fuc"
```

- Micro-XPath
```sh
documento.microXPath("fuc/avaliacao/componente")
-> (String_do_caminho)
-> Devolve os fragmentos XML correspondentes ao caminho dado
```

##  DSL
Esta biblioteca inclui uma DSL (Linguagem de Domínio Específico) para a criação e manipulação de documentos XML. A DSL foi projetada para tornar a construção de estruturas XML mais intuitiva e menos propensa a erros.

De modo a exemplificar como utilizar a DSL, vamos assumir que pretendemos criar o documento XML seguinte:
```sh
<?xml version="1.0" encoding="UTF-8"?>
<plano>
    <fuc>
        <ects>6 ects</ects>
    </fuc>
</plano>
```
Para tal faríamos o seguinte:
```sh
private val document = Document("UTF-8", "1.0", "exemplo")

    val xmlLookAlike = document.tag("plano") {
        this.tag("fuc") {
            this.tag("ects") {
                this.textInTag("6 ects")
            }
        }
    }
```

Para além disso a DSL permite a manipulação de atributos utilizando notação infix. Usando o exemplo anterior como base, de modo a atingir :
```sh
<?xml version="1.0" encoding="UTF-8"?>
<plano>
    <fuc codigo="1234" uc="PA">
        <ects>6 ects</ects>
    </fuc>
</plano>
```

Ou seja, para adicionar atributos a uma Tag, faríamos algo como:
```sh
val fuc = document.findTag("fuc")
    if (fuc != null) {
        fuc addattribute Attribute("codigo", "1234")
        fuc addattribute Attribute("uc", "PA")
    }
```
No caso a remoção de atributos seria feita da seguinte forma:
```sh
if (fuc != null) {
            fuc deleteattribute Attribute("codigo", "1234")
        }
```
Que resultava no XML final:
```sh
<?xml version="1.0" encoding="UTF-8"?>
<plano>
	<fuc uc="PA">
		<ects>6 ects</ects>
	</fuc>
</plano>
```

## Mapeamento XML (Com base na biblioteca criada)
De modo a e obter automáticamente  entidades XML a partir de objetos, com base na estrutura das respectivas classes, deverá instanciar um objeto Mapping:
```sh
val map = Mapping()
```

Ao criar uma classe pode-se dar duas anotações:
```sh
@Mapping.XmlTag("nome_da_tag")
ou
@Mapping.XmlTagText("nome_da_tag")
```

Com a segunda opção, apenas se pode ter pârametros que representem ou atributos ou um objeto Text (apenas pode existir um objeto Text por Tag).
- Para definir um pârametro como atributo:
```sh
@Mapping.XmlAttribute("nome_do_atributo")
```
- Para definir como um objeto Text:
```sh
@Mapping.XmlText("texto_a_inserir")
```

Com a primeira opção, é possível usar as seguintes anotações:

- Caso se queira que a Tag que deriva da classe tenha atributos, no pârametro correspondente utiliza-se:
 ```sh
@Mapping.XmlAttribute("nome_do_atributo")
```

- Para ter uma Tag filha que apenas vai ter um objeto Text como filho:
```sh
@Mapping.XmlTagText("nome_da_tag")
-> onde não é necessário usar o @Mapping.XmlText
```

- Para ter uma Tag filha sem filhos até ao momento:
```sh
@Mapping.XmlTag("nome_da_tag")
-> e ao não usar mais anotações sabe-se automática que não vai ter filhos
```

- Para ter uma Tag filha com um ou múltiplos filhos Tag (não Text), usa-se a seguinte combinação:
```sh
@Mapping.XmlTag("nome_da_tag")
@Mapping.HasTagChilldren 
```

- Se se quiser adicionar um atributo a alguma das tags filhas da tag da classe que estamos a criar, usa-se
```sh
@Mapping.ChildWithAttribute("nome_do_atributo", "valor_do_atributo")
```
Por fim, existem mais duas anotações para manipular a tag criada e os seus componentes:

- @Mapping.XmlString(AddPercentage::class)
-> que recebe uma KClass que herda da interface StringTransformer (obrigatoriamente). Esta anotação é usada como forma de personalização do texto que é inserido no XML resultante de valores de atributos dos objetos.

- @Mapping.XmlAdapter(TestAdapter::class), que recebe uma KClass que herda da interface Adapter (obrigatoriamente). Esta anotação é utilizada como forma de personalização pós-mapeamento, que associa um adaptador que faz alterações livres na entidade XML após mapeamento
automático.

- Para fazer o mapeamento, instancia-se a classe criada, como no exemplo:
```sh
 val f = FUC("Programação Avançada", 6.0)
 ```

- e para chamar a função que inicia o processo:
```sh
map.createClass(f, null, doc)
```
-> Cujos pârametros são:  o objeto instanciado, o parent desse objeto (se for null vai ser a Tag raiz do documento), e o documento onde vai ser escrito o XML.


-Caso se use a notação @Mapping.XmlAdapter, assume-se que o objeto da classe que se criou já foi instanciado, e esta alteração é pós-mapeamento. Por isso:
```sh
val adaptedTag = map.processChanges(f, doc)
-> que recebe a instancia da classe onde se usou a anotação, e o documento onde está a tag a ser alterada.
```

> Limitações das anotações:

