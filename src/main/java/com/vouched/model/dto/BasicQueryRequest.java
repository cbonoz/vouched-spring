package com.vouched.model.dto;

public record BasicQueryRequest(
        String query,
        int page,
        int size,
        boolean sortAsc,
        String sortBy
) {
    // Defaults
    public BasicQueryRequest() {
        this("", 0, 10, false, "");
    }

    public int getOffset() {
        return page*size;
    }
}
