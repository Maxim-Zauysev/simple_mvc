package org.example.web.controllers;

import org.apache.log4j.Logger;

import org.example.web.app.service.BookService;
import org.example.web.dto.Book;
import org.example.web.dto.BookIdToRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

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
        logger.info("got book shelf");
        model.addAttribute("book", new Book());
        model.addAttribute("bookIdToRemove", new BookIdToRemove());
        model.addAttribute("bookList", bookService.getAllBooks());
        return "book_shelf";
    }

    @PostMapping("/save")
    public String saveBook(@Valid Book book, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("book", book);
            model.addAttribute("bookIdToRemove", new BookIdToRemove());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        }
        else {
            if((!book.getTitle().isEmpty() || !book.getSize().isEmpty() ||  !book.getAuthor().isEmpty())){
                bookService.saveBook(book);
                logger.info("current repository size: "+bookService.getAllBooks().size());
            }
            return "redirect:/books/shelf";
        }
    }

    @PostMapping("/remove")
    public String removeBook(@Valid BookIdToRemove bookIdToRemove, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("book", new Book());
            model.addAttribute("bookList", bookService.getAllBooks());
            return "book_shelf";
        }else {
            bookService.removeBookById(bookIdToRemove.getId());
            return "redirect:/books/shelf";
        }

    }

    @PostMapping("/removeByRegex")
    public String removeBookByRegex(@RequestParam(value = "queryRegex") String queryRegex){
        bookService.removeBoolByRegex(queryRegex);
        return "redirect:/books/shelf";
    }
}

