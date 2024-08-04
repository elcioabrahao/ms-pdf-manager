package br.com.alfa11.mspdfmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@Tag(name = "Redirect para documentação Swaguer")
public class Redirect {

    @GetMapping(value = "/")
    @Operation(summary = "Obtem documentação das APIs",
            description = "Descrição dos end points no formato OpeAPI")
    public ModelAndView redirectToDocPage() {
        return new ModelAndView("redirect:/swagger-ui/index.html");
    }

    @GetMapping(value = "/apidocs")
    @Operation(summary = "Obtem documentação das APIs",
            description = "Descrição dos end points no formato OpeAPI")
    public ModelAndView redirectToApiPage() {
        return new ModelAndView("redirect:/swagger-ui/index.html");
    }
}
