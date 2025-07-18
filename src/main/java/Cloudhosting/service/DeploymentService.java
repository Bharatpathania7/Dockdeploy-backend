//package Cloudhosting.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.awt.*;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.Paths;
//
//@Service
//public class DeploymentService {
//
//    private final String BASE_DEPLOYMENT_DIR =  "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\Uploads//" ;//Change as needed
//
//    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file) throws IOException, InterruptedException {
//
//        // Create a directory for this project
//        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
//        File projectDir = new File(projectDirPath);
//        if (!projectDir.exists()) projectDir.mkdirs();
//        // 2. Save the uploaded file WITH A CLEAN NAME
//        String cleanJarName = "FinalBackend.jar"; // Always use this name
//        File backendFile = new File(projectDir, cleanJarName); // Forces the name to "app.jar"
//
//        try (FileOutputStream fos = new FileOutputStream(backendFile)) {
//            fos.write(file.getBytes()); // Writes the uploaded JAR as "app.jar"
//        }
//
//        // Generate Dockerfile and docker-compose.yml
//        generateDockerfile(framework, projectDirPath, backendFile.getName());
//        generateDockerCompose(framework, projectDirPath, projectName, subdomain);
//
//        // Step 1: Build Docker Image
//        buildDockerImage(projectDirPath, projectName);
//
//        // Step 2: Run the Container
//        runDockerContainer(projectName);
//
//        // Step 3: Run docker-compose to deploy
//        deployWithDockerCompose(projectDirPath);
//
//        return "http://" + subdomain + ".localhost"; // Modify for actual domain
//    }
//
//    private void generateDockerfile(String framework, String projectDirPath, String projectName) throws IOException {
//        File dockerfile = new File(projectDirPath, "Dockerfile");
//        String content = "";
//
//        switch (framework.toLowerCase()) {
//            case "springboot":
//                content = "FROM openjdk:17-jdk-slim\n" +
//                        "WORKDIR /FinalBackend\n" +
//                        "COPY " + projectName + " FinalBackend.jar\n" + // Uses the clean name
//                        "EXPOSE 8080\n" +
//                        "CMD [\"java\", \"-jar\", \"FinalBackend.jar\"]";
//                break;
//            case "fastapi":
//                content =
//                    "FROM python:3.11-slim\n" +
//                            "WORKDIR /FinalBackend\n" +
//                            "COPY . .\n" +  // Ensures all files, including requirements.txt, are copied first
//                            "COPY " + projectName + " /uploads/\n" +
//                            "RUN apt-get update && apt-get install -y unzip && unzip " + projectName + " && rm " + projectName + "\n" +
//                            "RUN pip install --no-cache-dir -r requirements.txt\n" +
//                            "CMD [\"uvicorn\", \"main:uploads\", \"--host\", \"0.0.0.0\", \"--port\", \"80\"]";
//
//                break;
//            case "nodejs":
//                content = "FROM node:18\n" +
//                        "WORKDIR /app\n" +
//                        "COPY " + projectName + " /FinalBackend\n" +
//                        "RUN unzip " + projectName + " && npm install\n" +
//                        "EXPOSE 3000\n" +
//                        "CMD [\"npm\", \"start\"]";
//                break;
//            default:
//                throw new IllegalArgumentException("Unsupported framework: " + framework);
//        }
//
//        java.nio.file.Files.write(Paths.get(dockerfile.getAbsolutePath()), content.getBytes());
//    }
//
//    private void generateDockerCompose(String framework, String projectDirPath, String projectName, String subdomain) throws IOException {
//        File composeFile = new File(projectDirPath, "docker-compose.yml");
//        String exposedPort = framework.equals("springboot") ? "8080" :
//                framework.equals("fastapi") ? "8000" :
//                        "3000";
//
//        String content = "version: '3.8'\n" +
//                "services:\n" +
//                "  " + projectName + ":\n" +
//                "    build: .\n" +
//                "    container_name: " + projectName+ "\n" +
//                "    networks:\n" +
//                "      - traefik-net\n" +
//                "    labels:\n" +
//                "      - \"traefik.enable=true\"\n" +
//                "      - \"traefik.http.routers." + projectName + ".rule=Host(`" + subdomain + ".localhost`)\"\n" +
//                "      - \"traefik.http.services." + projectName + ".loadbalancer.server.port=" + exposedPort + "\"\n" +
//                "    restart: always\n" +
//                "networks:\n" +
//                "  traefik-net:\n" +
//                "    external: true\n";
//
//        java.nio.file.Files.write(Paths.get(composeFile.getAbsolutePath()), content.getBytes());
//    }
//
//    private void buildDockerImage(String projectDirPath, String projectName) throws IOException, InterruptedException {
//        ProcessBuilder buildProcess = new ProcessBuilder(
//                "docker", "build", "-t", projectName, ".");
//        buildProcess.directory(new File(projectDirPath));
//        buildProcess.inheritIO();
//        Process process = buildProcess.start();
//        process.waitFor();
//    }
//
//    private void runDockerContainer(String projectName) throws IOException, InterruptedException {
//        ProcessBuilder runProcess = new ProcessBuilder(
//                "docker", "run", "-d", "--name", projectName, projectName);
//        runProcess.inheritIO();
//        Process runContainer = runProcess.start();
//        runContainer.waitFor();
//    }
//
//    private void deployWithDockerCompose(String projectDirPath) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder("docker-compose", "up", "-d");
//        pb.directory(new File(projectDirPath));
//        pb.inheritIO();
//        Process process = pb.start();
//        process.waitFor();
//    }
//}
//package Cloudhosting.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.*;
//import java.nio.file.Files;
//
//@Service
//public class DeploymentService {
//
//    private final String BASE_DEPLOYMENT_DIR = "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\Uploads//";
//
//    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file)
//            throws IOException, InterruptedException {
//
//        // 1. Setup project directory
//        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
//        File projectDir = new File(projectDirPath);
//        if (!projectDir.exists()) projectDir.mkdirs();
//
//        // 2. Save uploaded file with clean name
//        String cleanJarName = "app.jar";
//        File backendFile = new File(projectDir, cleanJarName);
//        try (FileOutputStream fos = new FileOutputStream(backendFile)) {
//            fos.write(file.getBytes());
//        }
//
//        // 3. Generate Docker files with Traefik labels
//        generateDockerfile(framework, projectDirPath, cleanJarName);
//        generateDockerCompose(framework, projectDirPath, projectName, subdomain);
//
//        // 4. Ensure Traefik network exists
//        ensureTraefikNetworkExists();
//
//        // 5. Full deployment with docker-compose
//        deployWithDockerCompose(projectDirPath);
//
//        return "http://" + subdomain + ".localhost";
//    }
//
//    private void ensureTraefikNetworkExists() throws IOException, InterruptedException {
//        ProcessBuilder checkProcess = new ProcessBuilder(
//                "docker", "network", "inspect", "traefik-net"
//        ).redirectErrorStream(true);
//
//        if (checkProcess.start().waitFor() != 0) {
//            ProcessBuilder createProcess = new ProcessBuilder(
//                    "docker", "network", "create", "traefik-net"
//            ).inheritIO();
//            if (createProcess.start().waitFor() != 0) {
//                throw new RuntimeException("Failed to create traefik-net");
//            }
//        }
//    }
//
//    private void generateDockerfile(String framework, String projectDirPath, String jarFilename) throws IOException {
//        File dockerfile = new File(projectDirPath, "Dockerfile");
//        String content = switch (framework.toLowerCase()) {
//            case "springboot" -> """
//                FROM openjdk:17-jdk-slim
//                WORKDIR /app
//                COPY %s app.jar
//                EXPOSE 8080
//                CMD ["java", "-jar", "app.jar"]
//                """.formatted(jarFilename);
//            // Add other frameworks as needed
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//        Files.writeString(dockerfile.toPath(), content);
//    }
//
//    private void generateDockerCompose(String framework, String projectDirPath,
//                                       String projectName, String subdomain) throws IOException {
//        File composeFile = new File(projectDirPath, "docker-compose.yml");
//        int port = switch (framework.toLowerCase()) {
//            case "springboot" -> 8080;
//            case "fastapi" -> 8000;
//            case "nodejs" -> 3000;
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//
//        String content = """
//            services:
//              %s:
//                build: .
//                container_name: %s
//                networks:
//                  - traefik-net
//                labels:
//                  - "traefik.enable=true"
//                  - "traefik.http.routers.%s.rule=Host(`%s.localhost`)"
//                  - "traefik.http.services.%s.loadbalancer.server.port=%d"
//                  - "traefik.http.routers.%s.entrypoints=web"
//                restart: unless-stopped
//
//            networks:
//              traefik-net:
//                external: true
//            """.formatted(projectName, projectName, projectName, subdomain, projectName, port, projectName);
//
//        Files.writeString(composeFile.toPath(), content);
//    }
//
//    private void deployWithDockerCompose(String projectDirPath) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(
//                "docker-compose", "up", "-d", "--build")
//                .directory(new File(projectDirPath))
//                .inheritIO();
//
//        int exitCode = pb.start().waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Deployment failed with exit code " + exitCode);
//        }
//    }
//}
//package Cloudhosting.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.*;
//import java.nio.file.*;
//import java.util.concurrent.*;
//
//@Service
//public class DeploymentService {
//
//    private final String BASE_DEPLOYMENT_DIR = "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\Uploads\\";
//    private final String TRAEFIK_NETWORK = "traefik-net";
//    private final String TRAEFIK_ENTRYPOINT = "web";
//
//    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file)
//            throws IOException, InterruptedException {
//
//        // 1. Setup infrastructure
//        ensureTraefikRunning();
//
//        // 2. Create project directory
//        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
//        File projectDir = new File(projectDirPath);
//        if (!projectDir.exists()) projectDir.mkdirs();
//
//        // 3. Save uploaded file
//        String outputFilename = getOutputFilename(framework);
//        File backendFile = new File(projectDir, outputFilename);
//        try (FileOutputStream fos = new FileOutputStream(backendFile)) {
//            fos.write(file.getBytes());
//        }
//
//        // 4. Generate Docker files
//        generateDockerfile(framework, projectDirPath, outputFilename);
//        generateDockerCompose(framework, projectDirPath, projectName, subdomain);
//
//        // 5. Deploy with compose
//        deployWithDockerCompose(projectDirPath);
//
//        return String.format(
//                "Service deployed at: http://%s.localhost\n" +
//                        "Traefik Dashboard: http://localhost:8080",
//                subdomain
//        );
//    }
//
//    private void ensureTraefikRunning() throws IOException, InterruptedException {
//        // Create network if needed
//        if (!networkExists(TRAEFIK_NETWORK)) {
//            executeCommand("docker", "network", "create", TRAEFIK_NETWORK);
//        }
//
//        // Start Traefik if not running
//        if (!containerIsRunning("traefik")) {
//            executeCommand(
//                    "docker", "run", "-d",
//                    "-p", "80:80",
//                    "-p", "8080:8080",
//                    "-v", "/var/run/docker.sock:/var/run/docker.sock",
//                    "--name", "traefik",
//                    "--network", TRAEFIK_NETWORK,
//                    "traefik:v2.5",
//                    "--providers.docker",
//                    "--api.insecure=true",
//                    "--entrypoints.web.address=:80"
//            );
//        }
//    }
//
//    private String getOutputFilename(String framework) {
//        return switch (framework.toLowerCase()) {
//            case "springboot" -> "app.jar";
//            case "fastapi" -> "app.zip";
//            case "nodejs" -> "app.zip";
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//    }
//
//    private void generateDockerfile(String framework, String projectDirPath, String outputFilename)
//            throws IOException {
//        String content = switch (framework.toLowerCase()) {
//            case "springboot" -> """
//                FROM openjdk:17-jdk-slim
//                WORKDIR /app
//                COPY %s app.jar
//                EXPOSE 8080
//                CMD ["java", "-jar", "app.jar" "--server.address=0.0.0.0"]
//                """.formatted(outputFilename);
//
//            case "fastapi" -> """
//                FROM python:3.9-slim
//                WORKDIR /app
//                COPY %s .
//                RUN apt-get update && apt-get install -y unzip && unzip %s
//                RUN pip install -r requirements.txt
//                EXPOSE 8000
//                CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
//                """.formatted(outputFilename, outputFilename);
//
//            case "nodejs" -> """
//                FROM node:16
//                WORKDIR /app
//                COPY %s .
//                RUN tar -xf %s
//                RUN npm install
//                EXPOSE 3000
//                CMD ["npm", "start"]
//                """.formatted(outputFilename, outputFilename);
//
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//
//        Files.writeString(Path.of(projectDirPath, "Dockerfile"), content);
//    }
//
//    private void generateDockerCompose(String framework, String projectDirPath,
//                                       String projectName, String subdomain) throws IOException {
//        int port = switch (framework.toLowerCase()) {
//            case "springboot" -> 8080;
//            case "fastapi" -> 8000;
//            case "nodejs" -> 3000;
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//
//        String content = """
//            services:
//              %s:
//                build: .
//                container_name: %s
//                networks:
//                  - %s
//                labels:
//                  - "traefik.enable=true"
//                  - "traefik.http.routers.%s.rule=Host(`%s.localhost`)"
//                  - "traefik.http.services.%s.loadbalancer.server.port=%d"
//                  - "traefik.http.routers.%s.entrypoints=%s"
//                restart: unless-stopped
//
//            networks:
//              %s:
//                external: true
//            """.formatted(
//                projectName, projectName,
//                TRAEFIK_NETWORK,
//                projectName, subdomain,
//                projectName, port, projectName, TRAEFIK_ENTRYPOINT,
//                TRAEFIK_NETWORK
//        );
//
//        Files.writeString(Path.of(projectDirPath, "docker-compose.yml"), content);
//    }
//
//    private void deployWithDockerCompose(String projectDirPath) throws IOException, InterruptedException {
//        executeCommand(
//                "docker-compose", "-f",
//                Path.of(projectDirPath, "docker-compose.yml").toString(),
//                "up", "-d", "--build"
//        );
//    }
//
//    // Helper methods
//    private boolean networkExists(String network) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "network", "inspect", network) == 0;
//    }
//
//    private boolean containerIsRunning(String container) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "inspect", "-f", "{{.State.Running}}", container) == 0;
//    }
//
//    private void executeCommand(String... command) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
//        int exitCode = pb.start().waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Command failed: " + String.join(" ", command));
//        }
//    }
//
//    private int executeCommandWithReturn(String... command) throws IOException, InterruptedException {
//        return new ProcessBuilder(command)
//                .redirectErrorStream(true)
//                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
//                .start()
//                .waitFor();
//    }
//}
//

//package Cloudhosting.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.*;
//import java.nio.file.*;
//import java.util.concurrent.*;
//
//@Service
//public class DeploymentService {
//
//    private final String BASE_DEPLOYMENT_DIR = "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\FinalBackend\\uploads\\";
//
//    private final String TRAEFIK_NETWORK = "traefik-net";
//    private final String TRAEFIK_ENTRYPOINT = "web";
//    private final String SHARED_COMPOSE_FILE = "multi-service-compose.yml";
//
//    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file)
//            throws IOException, InterruptedException {
//        ensureTraefikRunning();
//
//        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
//        File projectDir = new File(projectDirPath);
//        if (!projectDir.exists()) projectDir.mkdirs();
//
//        String outputFilename = getOutputFilename(framework);
//        saveUploadedFile(file, projectDir, outputFilename);
//
//        generateDockerfile(framework, projectDirPath, outputFilename);
//        updateSharedDockerCompose(framework, projectDirPath, projectName, subdomain);
//
//        deployWithDockerCompose();
//
//        return String.format(
//                "Service deployed at: http://%s.localhost\nTraefik Dashboard: http://localhost:8080",
//                subdomain
//        );
//    }
//
//    private void saveUploadedFile(MultipartFile file, File projectDir, String outputFilename)
//            throws IOException {
//        File backendFile = new File(projectDir, outputFilename);
//        try (FileOutputStream fos = new FileOutputStream(backendFile)) {
//            fos.write(file.getBytes());
//        }
//    }
//
//    private String getOutputFilename(String framework) {
//        return switch (framework.toLowerCase()) {
//            case "springboot" -> "app.jar";
//            case "fastapi" -> "app.zip";
//            case "nodejs" -> "app.zip";
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//    }
//
//    private void ensureTraefikRunning() throws IOException, InterruptedException {
//        if (!networkExists(TRAEFIK_NETWORK)) {
//            executeCommand("docker", "network", "create", TRAEFIK_NETWORK);
//        }
//
//        if (!containerIsRunning("traefik")) {
//            executeCommand(
//                    "docker", "run", "-d",
//                    "-p", "80:80",
//                    "-p", "8080:8080",
//                    "-v", "/var/run/docker.sock:/var/run/docker.sock",
//                    "--name", "traefik",
//                    "--network", TRAEFIK_NETWORK,
//                    "traefik:v2.5",
//                    "--providers.docker",
//                    "--api.insecure=true",
//                    "--entrypoints.web.address=:80"
//            );
//        }
//    }
//
//    private void generateDockerfile(String framework, String projectDirPath, String outputFilename)
//            throws IOException {
//        String content = switch (framework.toLowerCase()) {
//            case "springboot" -> """
//                FROM openjdk:17-jdk-slim
//                WORKDIR /FinalBackend
//                COPY %s app.jar
//                EXPOSE 8080
//                CMD ["java", "-jar", "app.jar", "--server.address=0.0.0.0"]
//                """.formatted(outputFilename);
//
//            case "fastapi" -> """
//                FROM python:3.9-slim
//                WORKDIR /FinalBackend
//                COPY %s .
//                RUN apt-get update && apt-get install -y unzip && unzip %s
//                RUN pip install -r requirements.txt
//                EXPOSE 8000
//                CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
//                """.formatted(outputFilename, outputFilename);
//
//            case "nodejs" -> """
//                FROM node:16
//                WORKDIR /FinalBackend
//                COPY %s .
//                RUN unzip %s
//                RUN npm install
//                EXPOSE 3000
//                CMD ["npm", "start"]
//                """.formatted(outputFilename, outputFilename);
//
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//
//        Files.writeString(Path.of(projectDirPath, "Dockerfile"), content);
//    }
//
//    private void updateSharedDockerCompose(String framework, String projectDirPath,
//                                           String projectName, String subdomain) throws IOException {
//        Path composePath = Path.of(BASE_DEPLOYMENT_DIR, SHARED_COMPOSE_FILE);
//        String serviceConfig = generateServiceConfig(framework, projectDirPath, projectName, subdomain);
//
//        if (!Files.exists(composePath)) {
//            Files.writeString(composePath, generateBaseComposeContent());
//        }
//
//        String content = Files.readString(composePath);
//        content = content.replaceAll(
//                String.format("\\s+%s:.*?(?=\\n\\s+\\w+:|\nnetworks:)", projectName),
//                ""
//        );
//        content = content.replaceFirst(
//                "(?=\nnetworks:)",
//                "\n" + serviceConfig
//        );
//
//        Files.writeString(composePath, content);
//    }
//
//    private String generateServiceConfig(String framework, String projectDirPath,
//                                         String projectName, String subdomain) {
//        int port = switch (framework.toLowerCase()) {
//            case "springboot" -> 8080;
//            case "fastapi" -> 8000;
//            case "nodejs" -> 3000;
//            default -> throw new IllegalArgumentException("Unsupported framework");
//        };
//
//        Path relativePath = Path.of(BASE_DEPLOYMENT_DIR).relativize(Path.of(projectDirPath));
//
//        return String.format("""
//          %s:
//            build:
//              context: ./%s
//            container_name: %s
//            networks:
//              - %s
//            labels:
//              - "traefik.enable=true"
//              - "traefik.http.routers.%s.rule=Host(`%s.localhost`)"
//              - "traefik.http.services.%s.loadbalancer.server.port=%d"
//              - "traefik.http.routers.%s.entrypoints=%s"
//            restart: unless-stopped
//        """,
//                projectName,
//                relativePath.toString().replace("\\", "/"),
//                projectName,
//                TRAEFIK_NETWORK,
//                projectName, subdomain,
//                projectName, port,
//                projectName, TRAEFIK_ENTRYPOINT);
//    }
//
//    private String generateBaseComposeContent() {
//        return String.format("""
//        version: '3.8'
//
//        services:
//          traefik:
//            image: traefik:v2.5
//            container_name: traefik
//            ports:
//              - "80:80"
//              - "8080:8080"
//            volumes:
//              - /var/run/docker.sock:/var/run/docker.sock
//            command:
//              - "--providers.docker"
//              - "--api.insecure=true"
//              - "--entrypoints.web.address=:80"
//              - "--providers.docker.exposedbydefault=false"
//            networks:
//              - %s
//            restart: unless-stopped
//
//        networks:
//          %s:
//            external: true
//        """, TRAEFIK_NETWORK, TRAEFIK_NETWORK);
//    }
//
//    private void deployWithDockerCompose() throws IOException, InterruptedException {
//        executeCommand(
//                "docker-compose", "-f",
//                Path.of(BASE_DEPLOYMENT_DIR, SHARED_COMPOSE_FILE).toString(),
//                "up", "-d", "--build"
//        );
//    }
//
//    private boolean networkExists(String network) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "network", "inspect", network) == 0;
//    }
//
//    private boolean containerIsRunning(String container) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "inspect", "-f", "{{.State.Running}}", container) == 0;
//    }
//
//    private void executeCommand(String... command) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
//        int exitCode = pb.start().waitFor();
//        if (exitCode != 0) {
//            throw new RuntimeException("Command failed: " + String.join(" ", command));
//        }
//    }
//
//    private int executeCommandWithReturn(String... command) throws IOException, InterruptedException {
//        return new ProcessBuilder(command)
//                .redirectErrorStream(true)
//                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
//                .start()
//                .waitFor();
//    }
//}

//package Cloudhosting.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import java.io.*;
//import java.nio.file.*;
//import java.util.concurrent.*;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Service
//public class DeploymentService {
//
//    private final String BASE_DEPLOYMENT_DIR = "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\uploads\\";
//    private final String TRAEFIK_NETWORK = "traefik-net";
//    private final String TRAEFIK_ENTRYPOINT = "web";
//    private final String COMPOSE_FILE = "docker-compose.yml";
//
//    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file)
//            throws IOException, InterruptedException {
//
//        // 1. Setup infrastructure
//        ensureTraefikRunning();
//
//        // 2. Create project directory with verification
//        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
//        Path projectPath = Paths.get(projectDirPath);
//        if (!Files.exists(projectPath)) {
//            Files.createDirectories(projectPath);
//            System.out.println("Created directory: " + projectPath.toAbsolutePath());
//        }
//
//        // 3. Save uploaded file with verification
//        String outputFilename = getOutputFilename(framework);
//        Path destination = projectPath.resolve(outputFilename);
//        file.transferTo(destination);
//        System.out.println("Saved file to: " + destination);
//
//        // 4. Generate Docker files
//        generateDockerfile(framework, projectDirPath, outputFilename);
//        generateProjectComposeFile(framework, projectDirPath, projectName, subdomain);
//
//        // 5. Deploy with compose
//        deployWithDockerCompose(projectDirPath);
//
//        return String.format(
//                "Service deployed at: http://%s.localhost\n" +
//                        "Traefik Dashboard: http://localhost:8080",
//                subdomain
//        );
//    }
//
//    private String getOutputFilename(String framework) {
//        return switch (framework.toLowerCase()) {
//            case "springboot" -> "app.jar";
//            case "fastapi" -> "app.zip";
//            case "nodejs" -> "app.zip";
//            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
//        };
//    }
//
//    private void ensureTraefikRunning() throws IOException, InterruptedException {
//        if (!networkExists(TRAEFIK_NETWORK)) {
//            executeCommand("docker", "network", "create", TRAEFIK_NETWORK);
//            System.out.println("Created network: " + TRAEFIK_NETWORK);
//        }
//
//        if (!containerIsRunning("traefik")) {
//            executeCommand(
//                    "docker", "run", "-d",
//                    "-p", "80:80",
//                    "-p", "8080:8080",
//                    "-v", "/var/run/docker.sock:/var/run/docker.sock",
//                    "--name", "traefik",
//                    "--network", TRAEFIK_NETWORK,
//                    "traefik:v2.5",
//                    "--providers.docker",
//                    "--api.insecure=true",
//                    "--entrypoints.web.address=:80",
//                    "--providers.docker.exposedbydefault=false"
//            );
//            System.out.println("Started Traefik container");
//        }
//    }
//
//    private void generateDockerfile(String framework, String projectDirPath, String outputFilename)
//            throws IOException {
//        String content = switch (framework.toLowerCase()) {
//            case "springboot" -> String.format("""
//                FROM openjdk:17-jdk-slim
//                WORKDIR /app
//                COPY %s app.jar
//                EXPOSE 8080
//                CMD ["java", "-jar", "app.jar", "--server.address=0.0.0.0"]
//                """, outputFilename);
//
//            case "fastapi" -> String.format("""
//                FROM python:3.9-slim
//                WORKDIR /app
//                COPY %s .
//                RUN apt-get update && apt-get install -y unzip && unzip %s
//                RUN pip install -r requirements.txt
//                EXPOSE 8000
//                CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
//                """, outputFilename, outputFilename);
//
//            case "nodejs" -> String.format("""
//                FROM node:16
//                WORKDIR /app
//                COPY %s .
//                RUN unzip %s && npm install
//                EXPOSE 3000
//                CMD ["npm", "start"]
//                """, outputFilename, outputFilename);
//
//            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
//        };
//
//        Path dockerfilePath = Paths.get(projectDirPath, "Dockerfile");
//        Files.writeString(dockerfilePath, content);
//        System.out.println("Generated Dockerfile at: " + dockerfilePath);
//    }
//
//    private void generateProjectComposeFile(String framework, String projectDirPath,
//                                            String projectName, String subdomain) throws IOException {
//        Path composePath = Paths.get(projectDirPath, COMPOSE_FILE);
//        String content = generateComposeContent(framework, projectName, subdomain);
//        Files.writeString(composePath, content);
//        System.out.println("Generated compose file at: " + composePath);
//    }
//
//    private String generateComposeContent(String framework, String projectName, String subdomain) {
//        int port = switch (framework.toLowerCase()) {
//            case "springboot" -> 8080;
//            case "fastapi" -> 8000;
//            case "nodejs" -> 3000;
//            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
//        };
//
//
//        // Include framework type in container name to ensure uniqueness
//        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
//        String containerName = String.format("%s-%s-%s",
//                framework.toLowerCase(),
//                projectName,
//                timestamp);
//
//
//        return String.format("""
//        services:
//          %s:
//            build:
//              context: .
//            container_name: %s
//            networks:
//              - %s
//            labels:
//              - "traefik.enable=true"
//              - "traefik.http.routers.%s.rule=Host(`%s.localhost`)"
//              - "traefik.http.services.%s.loadbalancer.server.port=%d"
//              - "traefik.http.routers.%s.entrypoints=%s"
//            restart: unless-stopped
//
//        networks:
//          %s:
//            external: true
//        """,
//                projectName,
//                containerName,
//                TRAEFIK_NETWORK,
//                projectName, subdomain,
//                projectName, port,
//                projectName, TRAEFIK_ENTRYPOINT,
//                TRAEFIK_NETWORK);
//    }
//    private void deployWithDockerCompose(String projectDirPath) throws IOException, InterruptedException {
//        // First try to remove any existing containers to prevent conflicts
//        try {
//            executeCommand(
//                    "docker-compose", "-f",
//                    Paths.get(projectDirPath, COMPOSE_FILE).toString(),
//                    "down"
//            );
//        } catch (Exception e) {
//            System.out.println("No existing containers to remove");
//        }
//
//        // Then bring up new containers
//        executeCommand(
//                "docker-compose", "-f",
//                Paths.get(projectDirPath, COMPOSE_FILE).toString(),
//                "up", "-d", "--build"
//        );
//        System.out.println("Deployed service from: " + projectDirPath);
//    }
//
//    private boolean networkExists(String network) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "network", "inspect", network) == 0;
//    }
//
//    private boolean containerIsRunning(String container) throws IOException, InterruptedException {
//        return executeCommandWithReturn("docker", "inspect", "-f", "{{.State.Running}}", container) == 0;
//    }
//
//    private void executeCommand(String... command) throws IOException, InterruptedException {
//        System.out.println("Executing: " + String.join(" ", command));
//        ProcessBuilder pb = new ProcessBuilder(command)
//                .redirectErrorStream(true)
//                .inheritIO();
//
//        Process process = pb.start();
//        int exitCode = process.waitFor();
//
//        if (exitCode != 0) {
//            throw new RuntimeException("Command failed with exit code " + exitCode + ": " + String.join(" ", command));
//        }
//    }
//
//    private int executeCommandWithReturn(String... command) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(command)
//                .redirectError(ProcessBuilder.Redirect.DISCARD)
//                .redirectOutput(ProcessBuilder.Redirect.DISCARD);
//
//        return pb.start().waitFor();
//    }
//}


package Cloudhosting.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class DeploymentService {

    private final String BASE_DEPLOYMENT_DIR = "C:\\Users\\PATHANIA\\Desktop\\Deployersfinal\\uploads\\";
    private final String TRAEFIK_NETWORK = "traefik-net";
    private final String TRAEFIK_ENTRYPOINT = "web";
    private final String COMPOSE_FILE = "docker-compose.yml";

    public String deployBackend(String framework, String projectName, String subdomain, MultipartFile file)
            throws IOException, InterruptedException {

        // Clean up old containers first
        cleanOldContainers(projectName);

        // Setup infrastructure
        ensureTraefikRunning();

        // Create project directory
        String projectDirPath = BASE_DEPLOYMENT_DIR + projectName;
        Path projectPath = Paths.get(projectDirPath);
        if (!Files.exists(projectPath)) {
            Files.createDirectories(projectPath);
            System.out.println("Created directory: " + projectPath.toAbsolutePath());
        }

        // Save uploaded file
        String outputFilename = getOutputFilename(framework);
        Path destination = projectPath.resolve(outputFilename);
        file.transferTo(destination);
        System.out.println("Saved file to: " + destination);

        // Generate Docker files
        generateDockerfile(framework, projectDirPath, outputFilename);
        generateProjectComposeFile(framework, projectDirPath, projectName, subdomain);

        // Deploy with compose
        deployWithDockerCompose(projectDirPath);

        return String.format(
                "Service deployed at: http://%s.localhost\n" +
                        "Traefik Dashboard: http://localhost:8080",
                subdomain
        );
    }

    private void cleanOldContainers(String projectName) throws IOException, InterruptedException {
        try {
            System.out.println("[Cleanup] Starting cleanup for project: " + projectName);

            // Find all containers related to this project
            Process listProcess = new ProcessBuilder(
                    "docker", "ps", "-a",
                    "--filter", "name=" + projectName,
                    "--format", "{{.ID}}"
            ).start();

            // Read container IDs
            List<String> containerIds = new BufferedReader(new InputStreamReader(listProcess.getInputStream()))
                    .lines()
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.toList());

            if (containerIds.isEmpty()) {
                System.out.println("[Cleanup] No containers found for project: " + projectName);
                return;
            }

            System.out.println("[Cleanup] Found containers to remove: " + containerIds);

            // Build and execute removal command
            String[] removeCommand = new String[containerIds.size() + 3];
            removeCommand[0] = "docker";
            removeCommand[1] = "rm";
            removeCommand[2] = "-f";
            System.arraycopy(containerIds.toArray(new String[0]), 0, removeCommand, 3, containerIds.size());

            executeCommand(removeCommand);
            System.out.println("[Cleanup] Successfully removed containers");

            // Clean up unused networks
            try {
                executeCommand("docker", "network", "prune", "-f");
                System.out.println("[Cleanup] Cleaned up unused networks");
            } catch (Exception e) {
                System.out.println("[Cleanup] Network prune failed: " + e.getMessage());
            }

        } catch (Exception e) {
            throw new IOException("Failed to clean old containers for project: " + projectName, e);
        }
    }

    private String getOutputFilename(String framework) {
        return switch (framework.toLowerCase()) {
            case "springboot" -> "app.jar";
            case "fastapi" -> "app.zip";
            case "nodejs" -> "app.zip";
            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
        };
    }

    private void ensureTraefikRunning() throws IOException, InterruptedException {
        if (!networkExists(TRAEFIK_NETWORK)) {
            executeCommand("docker", "network", "create", TRAEFIK_NETWORK);
            System.out.println("Created network: " + TRAEFIK_NETWORK);
        }

        if (!containerIsRunning("traefik")) {
            executeCommand(
                    "docker", "run", "-d",
                    "-p", "80:80",
                    "-p", "8080:8080",
                    "-v", "/var/run/docker.sock:/var/run/docker.sock",
                    "--name", "traefik",
                    "--network", TRAEFIK_NETWORK,
                    "traefik:v2.5",
                    "--providers.docker",
                    "--api.insecure=true",
                    "--entrypoints.web.address=:80",
                    "--providers.docker.exposedbydefault=false"
            );
            System.out.println("Started Traefik container");
        }
    }

    private void generateDockerfile(String framework, String projectDirPath, String outputFilename)
            throws IOException {
        String content = switch (framework.toLowerCase()) {
            case "springboot" -> String.format("""
                FROM eclipse-temurin:17-jdk
                WORKDIR /app
                COPY %s app.jar
                EXPOSE 8080
                CMD ["java", "-jar", "app.jar"]
                """, outputFilename);

            case "fastapi" -> String.format("""
                FROM python:3.12-slim
                WORKDIR /app
                COPY %s .
                RUN apt-get update && apt-get install -y unzip && unzip %s
                RUN  pip install --upgrade pip
                RUN pip install -r requirements.txt
                EXPOSE 8000
                CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
                """, outputFilename, outputFilename);

            case "nodejs" -> String.format("""
                FROM node:22
                WORKDIR /app
                COPY %s .
                RUN unzip %s && npm install
                EXPOSE 3000
                CMD ["npm", "start"]
                """, outputFilename, outputFilename);

            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
        };

        Path dockerfilePath = Paths.get(projectDirPath, "Dockerfile");
        Files.writeString(dockerfilePath, content);
        System.out.println("Generated Dockerfile at: " + dockerfilePath);
    }

    private void generateProjectComposeFile(String framework, String projectDirPath,
                                            String projectName, String subdomain) throws IOException {
        Path composePath = Paths.get(projectDirPath, COMPOSE_FILE);
        String content = generateComposeContent(framework, projectName, subdomain);
        Files.writeString(composePath, content);
        System.out.println("Generated compose file at: " + composePath);
    }

    private String generateComposeContent(String framework, String projectName, String subdomain) {
        int port = switch (framework.toLowerCase()) {
            case "springboot" -> 8080;
            case "fastapi" -> 8000;
            case "nodejs" -> 3000;
            default -> throw new IllegalArgumentException("Unsupported framework: " + framework);
        };

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String containerName = String.format("%s-%s-%s",
                framework.toLowerCase(),
                projectName,
                timestamp);

        return String.format("""
        version: '3.8'
        services:
          %s:
            build:
              context: .
            container_name: "%s"
            networks:
              - %s
            labels:
              - "traefik.enable=true"
              - "traefik.http.routers.%s.rule=Host(`%s.localhost`)"
              - "traefik.http.services.%s.loadbalancer.server.port=%d"
              - "traefik.http.routers.%s.entrypoints=%s"
            restart: unless-stopped

        networks:
          %s:
            external: true
        """,
                projectName,
                containerName,
                TRAEFIK_NETWORK,
                projectName, subdomain,
                projectName, port,
                projectName, TRAEFIK_ENTRYPOINT,
                TRAEFIK_NETWORK);
    }

    private void deployWithDockerCompose(String projectDirPath) throws IOException, InterruptedException {
        try {
            executeCommand(
                    "docker-compose", "-f",
                    Paths.get(projectDirPath, COMPOSE_FILE).toString(),
                    "down"
            );
        } catch (Exception e) {
            System.out.println("No existing containers to remove");
        }

        executeCommand(
                "docker-compose", "-f",
                Paths.get(projectDirPath, COMPOSE_FILE).toString(),
                "up", "-d", "--build"
        );
        System.out.println("Deployed service from: " + projectDirPath);
    }

    private boolean networkExists(String network) throws IOException, InterruptedException {
        return executeCommandWithReturn("docker", "network", "inspect", network) == 0;
    }

    private boolean containerIsRunning(String container) throws IOException, InterruptedException {
        return executeCommandWithReturn("docker", "inspect", "-f", "{{.State.Running}}", container) == 0;
    }

    private void executeCommand(String... command) throws IOException, InterruptedException {
        System.out.println("Executing: " + String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .inheritIO();

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code " + exitCode + ": " + String.join(" ", command));
        }
    }

    private int executeCommandWithReturn(String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command)
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD);

        return pb.start().waitFor();
    }
}