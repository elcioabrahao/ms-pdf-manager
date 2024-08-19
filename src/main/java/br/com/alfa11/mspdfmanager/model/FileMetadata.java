package br.com.alfa11.mspdfmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata implements Serializable {
    @Schema(name="nomePdf", description = "Nome do arquivo PDF que foi armazenado no storage.", example = "prancha01.pdf", required = true)
    private String nomePdf;
    @Schema(name="grupo", description = "Agrupamento onde foi colocado o arquivo - corresponde ao bucket.", example = "pranchas", required = true)
    private String grupo;
    @Schema(name="dataExtracao", description = "Data em que o arquivo foi armazenado no storage.", example = "01/01/2024 07:00:00", required = true)
    private String dataExtracao;
    @Schema(name="url", description = "URL que permite que o arquivo seja acessado diretamento no serviço do storage.", example = "https://servicoS3.com/bucket/arquivo.pdf", required = true)
    private String url;
}
