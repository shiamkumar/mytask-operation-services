package com.ghx.api.operations.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Vijayakumar Selvaraj
 *
 * @since 22/JUNE/2022
 * 
 *        DTO for global search feature
 */
@Data
public class GlobalSearchDTO {
    
    /** the search value */
    private String searchText;
    
    /** The field names */
    private List<String> fieldNames;
    
    /** Search Type */
    private String searchType;
}
