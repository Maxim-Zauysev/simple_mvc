package org.example.web.app.service;
import org.example.web.dto.Book;

import java.util.List;

import java.util.List;

public interface ProjectRepository<T> {
    List<T> retreiveAll();

    void store(T book);

    boolean removeItemById(String bookIdToRemove);

    void removeItemByRegex(String queryRegex);
}
