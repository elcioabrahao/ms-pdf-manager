package br.com.alfa11.mspdfmanager.model;

import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel
public class ComposedDocument implements Serializable {
    @Schema(name="composedDocumentId", description = "Indentificador único do documento. Vai ser utilizado no nome documento final gerado a partir do merge no formato: YYYYMMDDHHMMSS_XXXXXXXX.pdf onde XXXXXXXX é o identificador.", example = "DOCXXXXX", required = true)
    private String composedDocumentId;
    @Schema(name="grupo", description = "É um ID para o identificador do GRUPO, serve para nomear o bucket onde o PDF vai ser armazanado. Pode ser utilizado para agrupar documentos por empresa ou por departamento.", example = "Grupo01", required = true)
    private String grupo;
    @Schema(name="files", description = "Coleção com a lista dos aquivos que devem ser margeados. Os arquivos devem ser descritos na ordem exata que devem aparecer no documento final.", example = "", required = true)
    private List<FileDescription> files;
}
