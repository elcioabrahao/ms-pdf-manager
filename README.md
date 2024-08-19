
# MS-PDF-MANAGER

Está é uma implementação de referência para um geranciador de arquivos PDF que permite:




## Funcionalidades

- Fazer upload de arquivo PDF e armazená-lo em um serviço compatível com AWS S3;
- Fazer download de arquivo PDF armazenado no serviço compatível com S3;
- Fazer o merge de vários arquivos armazenados no serviço compatível com S3 obtendo como resposta o arquivo PDF para download.
- Fazer o merge de vários arquivos armazenados no serviço compatível com S3 obtendo como resposta a URL de referência do próprio serviço de armazenamento.
- Inserir informações customizadas em qualquer página de quaisquer dos arquivos que estão sendo mergeados.
- Permitir que o usuário informe as coordenadas da caixa (canva) onde os dados serão inseridos.
- Permitir que sejam colocadas quantas linhas de texto forem necessárias em cada canva (não há controle de limites);
- Permitir que seja exibida a borda da caixa (recoemndado somente para desenvolvimento dos doscumentos).







## Documentação da API

#### Faz upload de 1 ou mais arquivos PDF para Storage S3

```http
  POST /v1/pdf/upload
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `grupo` | `string` | **Obrigatório**. Grupo ou bucket |
| `pdf` | `file` | **Obrigatório**. Multipart files arquivos PDF |

#### Faz download de um PDF do Storage S3

```http
  GET /v1/pdf/download/${grupo}/${nome_arquivo.pdf}
```

| Parâmetro   | Tipo       | Descrição                                   |
| :---------- | :--------- | :------------------------------------------ |
| `grupo`      | `string` | **Obrigatório**. Nome do grupo ou bucket |
| `nome_arquivo.pdf`      | `string` | **Obrigatório**. Nome do arquivo a ser feito download do Storge |

#### Faz o merge de 2 ou mais arquivos PDF e insere dados customizados retornando o PDF resultante do merge como arquivo para download

```http
  GET /v1/pdf/merge
```

Request (JSON):

```javascript
{
  "composedDocumentId": "SJDHFGSVESFDH",
  "grupo": "alfa20",
  "files": [
    {
      "fileName": "prancha01.pdf",
      "grupo": "alfa16",
      "pageNumber": 1,
      "fileData": [
        {
          "dataId": "text01",
          "iix": 150,
          "iiy": 100,
          "urx": 500,
          "ury": 100,
          "stroke": false,
          "paragraphs": [
            "Empresa: Alfa11 Consultoria e Serviços Ltda",
            "CNPJ: XX.XXX.XXX/0001-XX"
          ]
        },
        {
          "dataId": "text02",
          "iix": 150,
          "iiy": 20,
          "urx": 500,
          "ury": 100,
          "stroke": false,
          "paragraphs": [
            "outro texto para na mesma página",
            "Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu "
          ]
        }
      ]
    },
    {
      "fileName": "prancha03.pdf",
      "grupo": "alfa16",
      "pageNumber": 1,
      "fileData": []
    }
  ]
}
```

Response (Arquivo):

Arquivo para download (Application Octect)

#### Faz o merge de 2 ou mais arquivos PDF e insere dados customizados retornando um JSON com as informações e URL para download direto do storage S3

```http
  GET /v1/pdf/merge/store
```

Request (JSON):

```javascript
{
  "composedDocumentId": "SJDHFGSVESFDH",
  "grupo": "alfa20",
  "files": [
    {
      "fileName": "prancha01.pdf",
      "grupo": "alfa16",
      "pageNumber": 1,
      "fileData": [
        {
          "dataId": "text01",
          "iix": 150,
          "iiy": 100,
          "urx": 500,
          "ury": 100,
          "stroke": false,
          "paragraphs": [
            "Empresa: Alfa11 Consultoria e Serviços Ltda",
            "CNPJ: XX.XXX.XXX/0001-XX"
          ]
        },
        {
          "dataId": "text02",
          "iix": 150,
          "iiy": 20,
          "urx": 500,
          "ury": 100,
          "stroke": false,
          "paragraphs": [
            "outro texto para na mesma página",
            "Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu Lorem ipsu "
          ]
        }
      ]
    },
    {
      "fileName": "prancha03.pdf",
      "grupo": "alfa16",
      "pageNumber": 1,
      "fileData": []
    }
  ]
}
```

Response (JSON):

```javascript
{
    "nomePdf": "20240818071403_SJDHFGSVESFDH.pdf",
    "grupo": "alfa20",
    "dataExtracao": "20240818071403",
    "url": "http://localhost:9000/docs-alfa20/20240818071403_SJDHFGSVESFDH.pdf?response-content-disposition=attachment%253B%2Bfilename%253D%2522null%2522&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=minio%2F20240818%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20240818T221403Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Signature=f5404cb6cdbdc98e6059325f52c956e673892a2891e92526d112f744d5231292"
}
```
## Documentação

Arquivos PDF para serem utilizados nos testes da API pode ser encontrados na pasta: resources/static. São 4 pranchas com figuras de fundo que possuem uma área em branco própria para adicionar conteúdo customizado.

Segue a colletion do POSTMAN com os exemplos:

```javascript
<div class="postman-run-button"
data-postman-action="collection/fork"
data-postman-visibility="public"
data-postman-var-1="33720813-306fb589-0788-4e6d-8cf2-0ac72ad57f30"
data-postman-collection-url="entityId=33720813-306fb589-0788-4e6d-8cf2-0ac72ad57f30&entityType=collection&workspaceId=1795e8cb-8d2e-42fb-bf61-4a91350a1139"></div>
<script type="text/javascript">
  (function (p,o,s,t,m,a,n) {
    !p[s] && (p[s] = function () { (p[t] || (p[t] = [])).push(arguments); });
    !o.getElementById(s+t) && o.getElementsByTagName("head")[0].appendChild((
      (n = o.createElement("script")),
      (n.id = s+t), (n.async = 1), (n.src = m), n
    ));
  }(window, document, "_pm", "PostmanRunObject", "https://run.pstmn.io/button.js"));
</script>
```

Swagguer:

Para acessar o Swagguer basta acessar a URL:
http://localhost:8080


Docker Compose do Min.IO (compose.yaml):

```javascript
services:
  redis:
    image: 'redis:latest'
    ports:
      - '6379'
  minio:
    image: docker.io/bitnami/minio:latest
    ports:
      - '9000:9000'
      - '9001:9001'
    networks:
      - minio_net
    volumes:
      - 'minio_data:/data'
    environment:
      - MINIO_ROOT_USER=minio
      - MINIO_ROOT_PASSWORD=minio123
      - MINIO_DEFAULT_BUCKETS=documents

networks:
    minio_net:
      driver: bridge

volumes:
    minio_data:
      driver: local

```
## Stack utilizada

**Front-end:** Thymeleaf

**Back-end:** Java 21, Spring Boot 3.2, ITextPDF 8, Swagger, Min.io (S3 compatível), Docker


## Autores

- [@elcioabrahao](https://www.github.com/elcioabrahao)

