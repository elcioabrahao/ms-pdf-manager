package br.com.alfa11.mspdfmanager.model;

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
public class FileMetadata implements Serializable {
    private String nomePdf;
    private String grupo;
    private String dataExtracao;
    private String url;
}
