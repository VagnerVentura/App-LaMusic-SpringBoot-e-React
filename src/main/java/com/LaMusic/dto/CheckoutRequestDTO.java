package com.LaMusic.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutRequestDTO {

	private UUID shippingAddressId;
	private UUID billingAddressId;

}
