package fr.afpa.choral_riff.controllers;

import fr.afpa.choral_riff.dto.DescriptifRequestDto;
import fr.afpa.choral_riff.services.IaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaService iaService;

    public IaController(IaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/generer-descriptif")
    public ResponseEntity<Map<String, String>> genererDescriptif(
            @RequestBody DescriptifRequestDto dto) {

        String descriptif = iaService.genererDescriptif(
            dto.titre(),
            dto.compositeur(),
            dto.genre()
        );

        return ResponseEntity.ok(Map.of("descriptif", descriptif));
    }
}