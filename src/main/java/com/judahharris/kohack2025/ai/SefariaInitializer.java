package com.judahharris.kohack2025.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.lang.Tuple2;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@Configuration
@Transactional
public class SefariaInitializer {

    @Autowired
    private final VectorStore vectorStore;

    @Value("${app.initialize-with-sefaria:false}")
    private boolean initializeWithSefaria;

    @Value("${app.sefaria.texts.path}")
    private String sefariaTextsPath;

    @Value("${app.sefaria.filter_version}")
    private String metadataVersion;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private long totalProcessed = 0;
    ArrayList<Document> batch = new ArrayList<>();

    private final int batchSize = 100;

    @Autowired
    public SefariaInitializer(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        System.out.println("SefariaInitializer created.");
    }


    @Bean
    public CommandLineRunner initializeVectorStore() {
        System.out.println("Running Initializing VectorStore...");
        return args -> {
            if (initializeWithSefaria) {
                System.out.println("Initializing VectorStore with texts from: " + sefariaTextsPath);
                loadDocumentsInBatches();
            } else {
                System.out.println("Initialization skipped (app.initialize-with-sefaria=false).");
            }
        };
    }

    private void loadDocumentsInBatches() throws IOException {
        Path basePath = Paths.get(sefariaTextsPath, "json");
        System.out.println("Looking for JSON files in: " + basePath);
        if (!Files.isDirectory(basePath)) {
            throw new IllegalArgumentException("Specified path 'json' is not a directory: " + basePath);
        }

        totalProcessed = 0;
        batch.clear();

        // Start recursive traversal from the base path
        traverseDirectory(basePath, new ArrayList<>());

        // Process any remaining documents in the batch
        if (!batch.isEmpty()) {
//            synchronized (vectorStore) {  // Ensure thread-safe adding to vectorStore
                vectorStore.add(batch);
//                vectorStore.add(new ArrayList<>(batch));
                totalProcessed += batch.size();
                System.out.println("Processed " + totalProcessed + " documents so far...");
                batch.clear();
//            }
        }

        System.out.println("Finished loading " + totalProcessed + " documents into VectorStore.");
    }

    private void traverseDirectory(Path currentPath, List<String> pathSegments) throws IOException {
        // Log the current directory being explored
        System.out.println("Exploring directory: " + currentPath + " (Path segments: " + pathSegments + ")");

        // Check if this directory contains English or Hebrew subdirectories
        Path englishDir = null;
        Path hebrewDir = null;

        for (Path subDir : Files.list(currentPath).filter(Files::isDirectory).toList()) {
            String dirName = subDir.getFileName().toString();
            if (dirName.equalsIgnoreCase("English")) {
                englishDir = subDir;
            } else if (dirName.equalsIgnoreCase("Hebrew")) {
                hebrewDir = subDir;
            }
        }

        // If we found English or Hebrew directories, process the JSON files
        if (englishDir != null || hebrewDir != null) {
            processTextDirectory(currentPath, pathSegments, englishDir, hebrewDir);
            return; // Stop recursing deeper once we find English/Hebrew
        }

        // If no English/Hebrew directories, recurse into subdirectories
        Iterator<Path> subDirIterator = Files.list(currentPath).parallel().filter(Files::isDirectory).iterator();
        if (!subDirIterator.hasNext()) {
            System.out.println("  No subdirectories found in: " + currentPath);
        }

        while (subDirIterator.hasNext()) {
            Path subDir = subDirIterator.next();
            String subDirName = subDir.getFileName().toString();

            // Add the current directory name to the path segments
            List<String> newPathSegments = new ArrayList<>(pathSegments);
            newPathSegments.add(subDirName);

            // Recurse into the subdirectory
            traverseDirectory(subDir, newPathSegments);
        }
    }

    private List<Tuple2<String, Map<String, Object>>> convertJsonToTextNodesRecursive(JsonNode node, List<String> pathSegments, Map<String, Object> baseMetadata) {
        return convertJsonToTextNodesRecursive(new ArrayList<>(), node, pathSegments, new ArrayList<>(), baseMetadata);
    }

    private List<Tuple2<String, Map<String, Object>>> convertJsonToTextNodesRecursive(List<Tuple2<String, Map<String, Object>>> existingNodes, JsonNode node, List<String> pathSegments, List<String> previousKeys, Map<String, Object> baseMetadata) {
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                JsonNode childNode = node.get(i);
                List<String> newKeys = new ArrayList<>(previousKeys);
                newKeys.add(String.valueOf(i + 1));
                convertJsonToTextNodesRecursive(existingNodes, childNode, pathSegments, newKeys, baseMetadata);
            }
        } else if (node.isContainerNode()) {
            for (Iterator<Map.Entry<String, JsonNode>> fields = node.fields(); fields.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = fields.next();
                List<String> newKeys = new ArrayList<>(previousKeys);
                newKeys.add(entry.getKey());
                convertJsonToTextNodesRecursive(existingNodes, entry.getValue(), pathSegments, newKeys, baseMetadata);
            }
        } else if (node.isTextual()) {
            String text = node.asText();
            Map<String, Object> metadata = new HashMap<>(baseMetadata);
            metadata.put("pathSegments", pathSegments);
            metadata.put("keys", previousKeys);
            existingNodes.add(new Tuple2<>(text, metadata));
        }
        return existingNodes;
    }

    private void processTextDirectory(Path textPath, List<String> pathSegments, Path englishDir, Path hebrewDir) throws IOException {
        System.out.println("Found text directory: " + textPath + " (Path segments: " + pathSegments + ")");

        Stream<Path> files;
        if (englishDir == null) {
            if (hebrewDir == null) {
                System.out.println("  No English or Hebrew directories found in: " + textPath);
                return;
            }
            files = Files.list(hebrewDir);
        } else if (hebrewDir == null) {
            files = Files.list(englishDir);
        } else {
            files = Stream.concat(Files.list(englishDir), Files.list(hebrewDir));
        }


        files//.parallel()
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".json"))
                .filter(path -> !path.getFileName().toString().equals("merged.json"))
                .map(this::turnFileToJson)
                .filter(Objects::nonNull)
                .filter(jsonNode -> Arrays.asList("en", "he").contains(jsonNode.get("actualLanguage").asText()))
                .peek(jsonNode -> System.out.println("Processing JSON file: " + jsonNode.get("title").asText()))
                .flatMap(jsonNode -> convertJsonToTextNodesRecursive(jsonNode.get("text"), pathSegments, new HashMap<>(Map.of("actualLanguage", jsonNode.get("actualLanguage").asText(), "versionTitle", jsonNode.get("versionTitle").asText(), "format_version", metadataVersion))).stream())
                .filter(tuple -> !Objects.isNull(tuple.getV1()))
                .filter(tuple -> !tuple.getV1().isEmpty())
                .forEach(tuple -> {
                    batch.add(new Document(tuple.getV1(), tuple.getV2()));
                    System.out.println("Added document to batch: " + tuple.getV2().get("pathSegments") + " - " + tuple.getV2().get("keys") + "; batch size " + batch.size());
                    if (batch.size() >= batchSize) {
                        //synchronized (vectorStore) {  // Ensure thread-safe adding to vectorStore
                            vectorStore.add(batch);
//                            vectorStore.add(new ArrayList<>(batch));
                            totalProcessed += batch.size();
                            System.out.println("Processed " + totalProcessed + " documents so far...");
                            batch.clear();
                        //}
                    }
                });
    }


    private JsonNode turnFileToJson(Path path) {
        try {
            return objectMapper.readTree(Files.newBufferedReader(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}