package org.example.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class BookRegexToRemove {

    @Pattern(regexp = "(author|title|size)\\s+(.+)*(\\s+)*")
    private String queryRegex;

    public String getQueryRegex() {
        return queryRegex;
    }

    public void setQueryRegex(String queryRegex) {
        this.queryRegex = queryRegex;
    }
}