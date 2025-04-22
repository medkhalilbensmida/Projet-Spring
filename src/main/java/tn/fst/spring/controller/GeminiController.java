package tn.fst.spring.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.projet_spring.dto.products.ProductResponse; // Assuming ProductResponse has a meaningful toString() or relevant fields
import tn.fst.spring.projet_spring.services.interfaces.IProductService; // Adjusted import path
import tn.fst.spring.service.GeminiService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private IProductService productService; // Using the interface found

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithGemini(@RequestBody ChatRequest chatRequest) {
        try {
            // Fetch all products
            List<ProductResponse> products = productService.getAllProducts();
            
            // Format product data as a string (adjust formatting as needed)
            String productDataString = products.stream()
                    .map(ProductResponse::toString) // Make sure ProductResponse.toString() provides useful info
                    .collect(Collectors.joining("\n---\n")); // Separate products clearly

            // Get response from Gemini service
            String response = geminiService.getGeminiResponse(chatRequest.getQuery(), productDataString);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Catch other potential exceptions (e.g., from productService or WebClient call)
            // log.error("An unexpected error occurred", e);
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // Simple DTO for the request body
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChatRequest {
        private String query;
    }
} 