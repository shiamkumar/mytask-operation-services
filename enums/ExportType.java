package com.ghx.api.operations.enums;

/**
 * 
 * @author Mari Muthu Muthukrishnan
 * @since 08/29/2021
 *
 * Supported Export Types
 */
public enum ExportType {
    CSV("CSV"), PDF("PDF"), XLS("XLS");

    /** Export Type*/
    private String type;

    /**
     * Returns the Export Type value
     * @return
     */
    public String getType() {
        return this.type;
    }

    ExportType(String type) {
        this.type = type;
    }
}
