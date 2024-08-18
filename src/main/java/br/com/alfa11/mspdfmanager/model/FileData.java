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
public class FileData implements Serializable {
    private String dataId;
    private int iix;
    private int iiy;
    private int urx;
    private int ury;
    private boolean stroke = false;
    private List<String> paragraphs;
}
