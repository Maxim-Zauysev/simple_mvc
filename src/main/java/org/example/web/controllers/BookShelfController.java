package org.example.web.controllers;

import org.apache.log4j.Logger;

import org.example.app.exeptions.BookShelfLoginException;
import org.example.app.exeptions.FIleToUploadException;
import org.example.app.services.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.example.web.dto.BookRegexToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.*;

@Controller
@RequestMapping(value = "books")
public class BookShelfController {

    private Logger logger = Logger.getLogger(BookShelfController.class);
    private BookService bookService;

    @Autowired
    public BookShelfController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/shelf")
    public String books(Model model) {
        return getBookShelf(new Book(), new BookIdToRemove(), new BookRegexToRemove(), model);
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return getBookShelf(book, new BookIdToRemove(), new BookRegexToRemove(), model);
        }
        else {
                bookService.saveBook(book);
                logger.info("current repository size: "+bookService.getAllBooks().size());
                return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            return getBookShelf(new Book(), bookIdToRemove, new BookRegexToRemove(), model);
        }else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }

    }

    @PostMapping("/removeByRegex")
    public String removeBookByRegex(@Valid BookRegexToRemove bookRegexToRemove, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return getBookShelf(new Book(), new BookIdToRemove(), bookRegexToRemove, model);
        }

        try {
            bookService.removeBoolByRegex(bookRegexToRemove.getQueryRegex());
        } catch (Exception e) {
            logger.error("Books with regex: " + bookRegexToRemove.getQueryRegex() + " not deleted");
            bindingResult.addError(new FieldError("bookRegexToRemove", "queryRegex", "Regex not valid"));
            return getBookShelf(new Book(), new BookIdToRemove(), bookRegexToRemove, model);
        }

        return "redirect:/books/shelf";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file")MultipartFile file) throws FIleToUploadException, IOException {
        if(file.isEmpty()){
            logger.info("Empty");
            throw new FIleToUploadException("Empty File");
        }
        String name = file.getOriginalFilename();
        byte[] bytes = file.getBytes();

        //create dir
        String rootPath = System.getProperty("catalina.home");
        File dir = new File(rootPath + File.separator + "external_uploads");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //create File
        File serverFile = new File(dir.getAbsolutePath() + File.separator + name);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();

        logger.info("new file saved at:" +serverFile.getAbsolutePath());
        return "redirect:/books/shelf";
    }
    @ExceptionHandler(FIleToUploadException.class)
    public String handleError(Model model, FIleToUploadException exception){
        model.addAttribute("errorMessage", exception.getMessage());
        return "errors/404";
    }
    private String getBookShelf(Book book, BookIdToRemove bookIdToRemove, BookRegexToRemove bookRegexToRemove, Model model) {
        model.addAttribute("book", book);
        model.addAttribute("bookIdToRemove", bookIdToRemove);
        model.addAttribute("bookRegexToRemove", bookRegexToRemove);
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }
}

