package br.com.alfa11.mspdfmanager.model;

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
    @NotBlank(message = "Nome do arquivo PDF que foi armazenado no storage.")
    private String nomePdf;
    @NotBlank(message = "Agrupamento onde foi colocado o arquivo - corresponde ao bucket.")
    private String grupo;
    @NotBlank(message = "Data em que o arquivo foi armazenado no storage.")
    private String dataExtracao;
    @NotBlank(message = "URL que permite que o arquivo seja acessado diretamento no serviço do storage.")
    private String url;
}
