package com.LaMusic.Mappers;

import org.springframework.stereotype.Component;

import com.LaMusic.dto.CouponDTO;
import com.LaMusic.entity.Coupon;

@Component
public class CouponMapper {

	 public CouponDTO toDTO(Coupon coupon) {
	        return CouponDTO.builder()
	                .id(coupon.getId())
	                .code(coupon.getCode())
	                .type(coupon.getType())
	                .value(coupon.getValue())
	                .minOrderAmount(coupon.getMinOrderAmount())
	                .maxDiscountAmount(coupon.getMaxDiscountAmount())
	                .usageLimit(coupon.getUsageLimit())
	                .usedCount(coupon.getUsedCount())
	                .isActive(coupon.isActive())
	                .validFrom(coupon.getValidFrom())
	                .validUntil(coupon.getValidUntil())
	                .build();
	    }

	    public Coupon toEntity(CouponDTO dto) {
	        return Coupon.builder()
	                .id(dto.getId())
	                .code(dto.getCode())
	                .type(dto.getType())
	                .value(dto.getValue())
	                .minOrderAmount(dto.getMinOrderAmount())
	                .maxDiscountAmount(dto.getMaxDiscountAmount())
	                .usageLimit(dto.getUsageLimit())
	                .usedCount(dto.getUsedCount())
	                .isActive(dto.isActive())
	                .validFrom(dto.getValidFrom())
	                .validUntil(dto.getValidUntil())
	                .build();
	    }
	
	
}
