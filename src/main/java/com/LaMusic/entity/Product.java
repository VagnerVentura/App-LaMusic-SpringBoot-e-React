package com.LaMusic.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String slug;
    
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    private BigDecimal price;

    @Column(name = "compare_price")
    private BigDecimal comparePrice;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    private String sku;
    private String barcode;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "min_stock_alert")
    private Integer minStockAlert;

    private BigDecimal weight;

    @Column(name = "dimensions_length")
    private BigDecimal dimensionsLength;

    @Column(name = "dimensions_width")
    private BigDecimal dimensionsWidth;

    @Column(name = "dimensions_height")
    private BigDecimal dimensionsHeight;

    private String status;
    private boolean featured;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime deletedAt;

    // ===== RELACIONAMENTOS =====

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductLog> logs;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images;

    @JsonBackReference
    @ManyToMany
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
