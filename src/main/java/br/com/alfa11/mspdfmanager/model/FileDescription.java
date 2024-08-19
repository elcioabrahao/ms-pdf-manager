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
public class FileDescription implements Serializable {
    @Schema(name="fileName", description = "Nome do arquivo PDF a ser mergeado, identico ao nome que foi informado para ser feito o upload. Uitlize somente o nome do arquivo.", example = "prancha01.pdf", required = true)
    private String fileName;
    @Schema(name="grupo", description = "Grupo onde o arquivo que vai ser mergeado está armazenado. Corresponde ao nome do Bucket. Pode ser diferente do ID do grupo de destino do arquivo já mergeado.", example = "pranchas", required = true)
    private String grupo;
    @Schema(name="pageNumber", description = "Número da página do PDF onde serão inseridas informações customizadas. Esta versão do MS só permite que sejam inseridas informações em uma página de cada vez. Se for precisar de múltiplas páginas recomenda-se que repita a operação em um PDF de página única.", example = "1", required = true)
    private int pageNumber;
    @Schema(name="fileData", description = "Coleção de dados sobre cada inserção de dados customizados no PDF.", example = "", required = true)
    private List<FileData> fileData;
}
