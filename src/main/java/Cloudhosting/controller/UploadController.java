package Cloudhosting.controller;

import Cloudhosting.service.DeploymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final DeploymentService deploymentService;

    public UploadController(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @PostMapping("/deploy")
    public ResponseEntity<Map<String, String>> deployBackend(
            @RequestParam("framework") String framework,
            @RequestParam("projectName") String projectName,
            @RequestParam("subdomain") String subdomain,
            @RequestParam("file") MultipartFile file) {

        try {
            String deployedSubdomain = deploymentService.deployBackend(framework, projectName, subdomain, file);
            return ResponseEntity.ok(Map.of("message", "Deployment successful", "subdomain", deployedSubdomain));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/abc")
    public String home() {
        return "CloudHosting Backend is Running!";
    }
}
