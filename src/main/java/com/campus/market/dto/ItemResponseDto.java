package com.campus.market.dto;

import com.campus.market.entity.Item;
import com.campus.market.entity.User;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class ItemResponseDto {

    private Long itemId;
    private String itemName;
    private String category;
    private BigDecimal price;
    private String description;
    private String sellerName;
    private String sellerContact;
    private String imageUrl;
    private String status;
    private boolean isDemo;
    private String createdAt;
    private Long userId;
    private boolean canEdit;

    public ItemResponseDto() {
    }

    public static ItemResponseDto fromEntity(Item item, User currentUser) {
        if (item == null) return null;
        ItemResponseDto dto = new ItemResponseDto();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setCategory(item.getCategory());
        dto.setPrice(item.getPrice());
        dto.setDescription(item.getDescription());
        dto.setSellerName(item.getSellerName());
        dto.setSellerContact(item.getSellerContact());
        dto.setImageUrl(item.getImageUrl());
        dto.setStatus(item.getStatus());
        dto.setDemo(item.isDemo());

        if (item.getCreatedAt() != null) {
            dto.setCreatedAt(item.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        if (item.getUser() != null) {
            dto.setUserId(item.getUser().getUserId());
        }

        // Ownership & permission logic:
        // Admin can edit all listings; normal users can edit only their own listings.
        boolean isOwner = currentUser != null && item.getUser() != null &&
                currentUser.getUserId().equals(item.getUser().getUserId());
        boolean isAdmin = currentUser != null && currentUser.isAdmin();
        dto.setCanEdit(isAdmin || isOwner);

        return dto;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerContact() {
        return sellerContact;
    }

    public void setSellerContact(String sellerContact) {
        this.sellerContact = sellerContact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
}

