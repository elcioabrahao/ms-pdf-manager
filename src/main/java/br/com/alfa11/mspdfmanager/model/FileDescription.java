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
public class FileDescription implements Serializable {
    private String fileName;
    private String grupo;
    private int pageNumber;
    private List<FileData> fileData;
}
