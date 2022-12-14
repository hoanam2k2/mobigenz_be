package com.api.mobigenz_be.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductVariantCombinationDto {
    private Integer id;
    private OptionDto optionDto;
    private OptionValueDto optionValueDto;
}
