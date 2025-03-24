package com.judahharris.kohack2025.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    @Value("${app.sefaria.filter_version}")
    private String metadataVersion;

    @Autowired
    public AiService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.chatClient = chatClientBuilder.build();
        this.vectorStore = vectorStore;
    }

    public void storeText(String text) {
        Document document = new Document(text);
        vectorStore.add(List.of(document));
    }

    public String runLLMQuery(String query, String userBackground) {
        // Step 1: Find relevant documents using vector search
        List<Document> relevantDocs = vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(10)
                .filterExpression(new FilterExpressionBuilder().eq("format_version", metadataVersion).build())
                .build()
        );

        // Step 2: Build the context string
        StringBuilder contextBuilder = new StringBuilder();
        for (Document doc : relevantDocs) {
            Map<String, Object> metadata = doc.getMetadata();
            List<String> path = (List<String>) metadata.get("pathSegments");
            List<String> keys = (List<String>) metadata.get("keys");
            contextBuilder.append("Location: " + String.join("/", path) + "/" + String.join(":", keys));
            contextBuilder.append("Language: " + metadata.get("actualLanguage"));
            contextBuilder.append("Text: " + doc.getFormattedContent());
            contextBuilder.append("Version: " + metadata.get("versionTitle"));
            contextBuilder.append("\n");
        }
        String prompt = formatPrompt(contextBuilder, userBackground);

        // Step 4: Call ChatGPT
        String response = chatClient.prompt()
            .system(prompt)
            .user(query)
            .call()
            .content();

        // Step 5: Append Sefaria links for the cited sources
        StringBuilder enhancedResponse = new StringBuilder(response);
        enhancedResponse.append("\n\n**References with Links:**\n");
        for (Document doc : relevantDocs) {
            Map<String, Object> metadata = doc.getMetadata();
            List<String> path = (List<String>) metadata.get("pathSegments");
            String location = path.getLast();
            List<String> indices = (List<String>) metadata.get("keys");
            String fullPath = location + "." + String.join(".", indices);
            String sanitizedPath = fullPath.replace(" ", "_");
            String link = "https://www.sefaria.org/" + sanitizedPath;

            enhancedResponse.append(String.join(", ", path) + " " + String.join(":", indices) + ", Link: " + link + " (version: " + metadata.get("versionTitle") + ")\n");
        }

        return enhancedResponse.toString();
    }

    private static String formatPrompt(StringBuilder contextBuilder, String background) {
        String context = contextBuilder.toString();

        // Step 3: Construct the prompt
        String promptTemplate = """
            You are a scholarly assistant specializing in Jewish texts from Sefaria. Below is the user's query, followed by relevant excerpts from Sefaria texts. Each excerpt includes the title, reference (indices), and text content (in English and/or Hebrew). Your task is to answer the query using the provided excerpts, quoting the relevant text where appropriate, and citing the source by title and reference (e.g., "Sefer Etz Chaim 1:1:1"). If the text includes both English and Hebrew, you may quote either or both, depending on what best answers the query. If the context is insufficient, say so and provide a general answer based on your knowledge, but prioritize the provided excerpts. We will also provide a little bit of a background about the user, so you can know what sort of answers they will be comfortable with. Please use their preferred pronunciation for all words based on the inputs, ie if they prefer Hanukkah instead of Chanukah, use Moses instead of Moshe, and Joshua instead of Yehoshua, etc. Please also be more elaborate and explain the basics if they are from Conservative or Reform denominations, or if they go to synagogue around less than 3 times a week. 

            **Context:**
            %s

            **User background:**
            %s

            **Instructions:**
            - Answer the query in a clear, scholarly tone.
            - Quote the relevant text from the excerpts to support your answer.
            - Cite each quote with the title and reference (e.g., "Sefer Etz Chaim 1:1:1").
            - Do not include Sefaria links in your response; they will be added separately.
            - If the context is insufficient, state that and provide a general answer.
            """;
        return String.format(promptTemplate, context, background);
    }

    // New method for manual exact-match search
    public String manualSearch(String input) {
        String searchTerm = input.trim();
        // Retrieve all documents from the VectorStore
        // Note: This assumes the VectorStore implementation supports retrieving all documents.
        // If not, you may need to use a different method depending on your VectorStore (e.g., a custom query).
        List<Document> allDocs = vectorStore.similaritySearch(SearchRequest.builder().query(searchTerm).topK(Integer.MAX_VALUE).build());

        // Filter documents that contain the exact search term (case-insensitive)
        List<Document> matchingDocs = allDocs.stream()
                .filter(doc -> doc.getFormattedContent().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        if (matchingDocs.isEmpty()) {
            return "No documents found containing the term: " + searchTerm;
        }

        // Format the results with metadata (e.g., category, subcategory, textName, indices)
        StringBuilder result = new StringBuilder();
        result.append("Found ").append(matchingDocs.size()).append(" documents containing the term: ").append(searchTerm).append("\n\n");

        for (Document doc : matchingDocs) {
            result.append("Document Content:\n").append(doc.getFormattedContent()).append("\n");
            result.append("Metadata:\n");
            doc.getMetadata().forEach((key, value) ->
                    result.append("  ").append(key).append(": ").append(value).append("\n"));
            result.append("---\n");
        }

        return result.toString();
    }
}