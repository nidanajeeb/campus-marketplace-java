package com.campus.market.service;

import com.campus.market.dto.ItemRequestDto;
import com.campus.market.dto.ItemResponseDto;
import com.campus.market.entity.Item;
import com.campus.market.entity.User;
import com.campus.market.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final FileStorageService fileStorageService;

    public ItemService(ItemRepository itemRepository, FileStorageService fileStorageService) {
        this.itemRepository = itemRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItems(
            String keyword,
            String category,
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort,
            User currentUser
    ) {
        // Default to "AVAILABLE" if status is empty/null, or "all" to view everything
        String queryStatus = status;
        if ("all".equalsIgnoreCase(status)) {
            queryStatus = null;
        } else if (status == null || status.isBlank()) {
            queryStatus = "AVAILABLE";
        }

        List<Item> items = itemRepository.searchItems(keyword, category, queryStatus, minPrice, maxPrice);

        // Sorting
        Comparator<Item> comparator;
        if ("price-asc".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(Item::getPrice);
        } else if ("price-desc".equalsIgnoreCase(sort)) {
            comparator = Comparator.comparing(Item::getPrice).reversed();
        } else {
            // Default: newest first
            comparator = Comparator.comparing(Item::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed();
        }

        return items.stream()
                .sorted(comparator)
                .map(item -> ItemResponseDto.fromEntity(item, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponseDto getItemById(Long id, User currentUser) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) return null;
        return ItemResponseDto.fromEntity(item, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> getMyListings(User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Authentication required.");
        }

        List<Item> items;
        if (currentUser.isAdmin()) {
            // Admin can see all listings in their management view
            items = itemRepository.findAll();
            items.sort(Comparator.comparing(Item::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        } else {
            items = itemRepository.findByUserOrderByCreatedAtDesc(currentUser);
        }

        return items.stream()
                .map(item -> ItemResponseDto.fromEntity(item, currentUser))
                .collect(Collectors.toList());
    }

    public ItemResponseDto createItem(ItemRequestDto dto, MultipartFile image, User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Authentication required to create a listing.");
        }

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = fileStorageService.storeFile(image);
        }

        Item item = new Item(
                dto.getItemName().trim(),
                dto.getCategory().trim(),
                dto.getPrice(),
                dto.getDescription().trim(),
                dto.getSellerName().trim(),
                dto.getSellerContact().trim(),
                imageUrl,
                currentUser,
                false
        );

        Item saved = itemRepository.save(item);
        return ItemResponseDto.fromEntity(saved, currentUser);
    }

    public ItemResponseDto updateItem(Long id, ItemRequestDto dto, MultipartFile image, User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Authentication required to edit a listing.");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found with ID: " + id));

        // Check ownership or admin
        boolean isOwner = item.getUser() != null && item.getUser().getUserId().equals(currentUser.getUserId());
        boolean isAdmin = currentUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You do not have permission to edit this listing.");
        }

        item.setItemName(dto.getItemName().trim());
        item.setCategory(dto.getCategory().trim());
        item.setPrice(dto.getPrice());
        item.setDescription(dto.getDescription().trim());
        item.setSellerName(dto.getSellerName().trim());
        item.setSellerContact(dto.getSellerContact().trim());

        if (image != null && !image.isEmpty()) {
            String oldImage = item.getImageUrl();
            String newImage = fileStorageService.storeFile(image);
            item.setImageUrl(newImage);
            if (oldImage != null) {
                fileStorageService.deleteFile(oldImage);
            }
        }

        Item updated = itemRepository.save(item);
        return ItemResponseDto.fromEntity(updated, currentUser);
    }

    public void setItemStatus(Long id, String status, User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Authentication required.");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found with ID: " + id));

        boolean isOwner = item.getUser() != null && item.getUser().getUserId().equals(currentUser.getUserId());
        boolean isAdmin = currentUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You do not have permission to change this listing's status.");
        }

        item.setStatus("SOLD".equalsIgnoreCase(status) ? "SOLD" : "AVAILABLE");
        itemRepository.save(item);
    }

    public void deleteItem(Long id, User currentUser) {
        if (currentUser == null) {
            throw new IllegalStateException("Authentication required to delete a listing.");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found with ID: " + id));

        boolean isOwner = item.getUser() != null && item.getUser().getUserId().equals(currentUser.getUserId());
        boolean isAdmin = currentUser.isAdmin();

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You do not have permission to delete this listing.");
        }

        if (item.getImageUrl() != null) {
            fileStorageService.deleteFile(item.getImageUrl());
        }

        itemRepository.delete(item);
    }
}

