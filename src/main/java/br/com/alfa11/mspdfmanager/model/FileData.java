package br.com.alfa11.mspdfmanager.model;

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
    @NotBlank(message = "Identificador único de dados da inserção customizada de dados. Referência para sistema que gera o conteúdo.")
    private String dataId;
    @NotBlank(message = "Coordenda X onde será inserida a caixa do canva para inserção dos dados customizados. Conta a partir da esquerda do documento.")
    private int iix;
    @NotBlank(message = "Coordenda y onde será inserida a caixa do canva para inserção dos dados customizados. Conta a partir da parte de baixo do documento.")
    private int iiy;
    @NotBlank(message = "Comprimento X da caixa do canva para inserção dos dados customizados.")
    private int urx;
    @NotBlank(message = "Comprimento y da caixa do canva para inserção dos dados customizados.")
    private int ury;
    @NotBlank(message = "True para mostrar a borda da caixa do canva, False para não exibir. Recomendado desenvolvedor poder ver o tamanho da caixa na montagem do documento.")
    private boolean stroke = false;
    @NotBlank(message = "Coleção com os parágrafos (textos) que vai ser incluidos dentro do respectivo canva.")
    private List<String> paragraphs;
}
