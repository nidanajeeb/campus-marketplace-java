package com.campus.market.controller;

import com.campus.market.config.DataInitializer;
import com.campus.market.dto.ItemRequestDto;
import com.campus.market.dto.ItemResponseDto;
import com.campus.market.entity.User;
import com.campus.market.service.AuthService;
import com.campus.market.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;
    private final AuthService authService;
    private final DataInitializer dataInitializer;

    private static final List<String> CATEGORIES = Arrays.asList(
            "Books",
            "Electronics",
            "Engineering Tools",
            "Lab Supplies",
            "Stationery",
            "Accessories",
            "Other"
    );

    public ItemController(ItemService itemService, AuthService authService, DataInitializer dataInitializer) {
        this.itemService = itemService;
        this.authService = authService;
        this.dataInitializer = dataInitializer;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(CATEGORIES);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponseDto>> getItems(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "min", required = false) BigDecimal min,
            @RequestParam(value = "max", required = false) BigDecimal max,
            @RequestParam(value = "sort", required = false) String sort,
            HttpSession session
    ) {
        User currentUser = authService.getCurrentUser(session);
        List<ItemResponseDto> items = itemService.searchItems(q, category, status, min, max, sort, currentUser);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItem(@PathVariable("id") Long id, HttpSession session) {
        User currentUser = authService.getCurrentUser(session);
        ItemResponseDto item = itemService.getItemById(id, currentUser);
        if (item == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Listing not found"));
        }
        return ResponseEntity.ok(item);
    }

    @GetMapping("/items/my-listings")
    public ResponseEntity<List<ItemResponseDto>> getMyListings(HttpSession session) {
        User currentUser = authService.requireAuthenticatedUser(session);
        List<ItemResponseDto> myItems = itemService.getMyListings(currentUser);
        return ResponseEntity.ok(myItems);
    }

    @PostMapping(value = "/items", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDto> createItem(
            @RequestParam("itemName") String itemName,
            @RequestParam("category") String category,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("sellerName") String sellerName,
            @RequestParam("sellerContact") String sellerContact,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        User currentUser = authService.requireAuthenticatedUser(session);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setItemName(itemName);
        dto.setCategory(category);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setSellerName(sellerName);
        dto.setSellerContact(sellerContact);

        ItemResponseDto created = itemService.createItem(dto, image, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemResponseDto> updateItem(
            @PathVariable("id") Long id,
            @RequestParam("itemName") String itemName,
            @RequestParam("category") String category,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("sellerName") String sellerName,
            @RequestParam("sellerContact") String sellerContact,
            @RequestParam(value = "image", required = false) MultipartFile image,
            HttpSession session
    ) {
        User currentUser = authService.requireAuthenticatedUser(session);

        ItemRequestDto dto = new ItemRequestDto();
        dto.setItemName(itemName);
        dto.setCategory(category);
        dto.setPrice(price);
        dto.setDescription(description);
        dto.setSellerName(sellerName);
        dto.setSellerContact(sellerContact);

        ItemResponseDto updated = itemService.updateItem(id, dto, image, currentUser);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/items/{id}/status")
    public ResponseEntity<Map<String, String>> setStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status,
            HttpSession session
    ) {
        User currentUser = authService.requireAuthenticatedUser(session);
        itemService.setItemStatus(id, status, currentUser);
        return ResponseEntity.ok(Collections.singletonMap("status", status));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(
            @PathVariable("id") Long id,
            HttpSession session
    ) {
        User currentUser = authService.requireAuthenticatedUser(session);
        itemService.deleteItem(id, currentUser);
        return ResponseEntity.ok(Collections.singletonMap("message", "Listing deleted successfully"));
    }

    @PostMapping("/admin/reset-demo")
    public ResponseEntity<Map<String, String>> resetDemoData(HttpSession session) {
        authService.requireAdmin(session);
        dataInitializer.resetDemoData();
        return ResponseEntity.ok(Collections.singletonMap("message", "Demo listings restored successfully"));
    }
}

