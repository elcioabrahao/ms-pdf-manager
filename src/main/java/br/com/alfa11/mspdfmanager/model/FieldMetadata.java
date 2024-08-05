package br.com.alfa11.mspdfmanager.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FieldMetadata implements Serializable {
    private String nomeCampo;
    private String tipoCampo;
    private String valorCampo;
}
