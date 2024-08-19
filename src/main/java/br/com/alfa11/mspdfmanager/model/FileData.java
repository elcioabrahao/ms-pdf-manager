package br.com.alfa11.mspdfmanager.model;

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
public class FileData implements Serializable {
    @Schema(name="dataId", description = "Identificador único de dados da inserção customizada de dados. Referência para sistema que gera o conteúdo.", example = "dados01", required = true)
    private String dataId;
    @Schema(name="iix", description = "Coordenda X onde será inserida a caixa do canva para inserção dos dados customizados. Conta a partir da esquerda do documento.", example = "50", required = true)
    private int iix;
    @Schema(name="iiy", description = "Coordenda y onde será inserida a caixa do canva para inserção dos dados customizados. Conta a partir da parte de baixo do documento.", example = "60", required = true)
    private int iiy;
    @Schema(name="urx", description = "Comprimento X da caixa do canva para inserção dos dados customizados.", example = "100", required = true)
    private int urx;
    @Schema(name="ury", description = "Comprimento y da caixa do canva para inserção dos dados customizados.", example = "150", required = true)
    private int ury;
    @Schema(name="stroke", description = "True para mostrar a borda da caixa do canva, False para não exibir. Recomendado desenvolvedor poder ver o tamanho da caixa na montagem do documento.", example = "false", required = true)
    private boolean stroke = false;
    @Schema(name="paragraphs", description = "Coleção com os parágrafos (textos) que vai ser incluidos dentro do respectivo canva.", example = "Texto a ser inserido", required = true)
    private List<String> paragraphs;
}
