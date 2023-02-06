package org.example.app.services;

import org.apache.log4j.Logger;

import org.example.web.dto.Book;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class BookRepository<T> implements ProjectRepository<Book>, ApplicationContextAware {

    private final Logger logger = Logger.getLogger(BookRepository.class);
    //private final List<Book> repo = new ArrayList<>();
    private ApplicationContext context;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    public BookRepository( NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public List<Book> retreiveAll() {
        List<Book> books = jdbcTemplate.query("SELECT * FROM books", (ResultSet rs, int rowNum)->{
            Book book = new Book();
            book.setId(rs.getInt("id"));
            book.setAuthor(rs.getString("author"));
            book.setTitle(rs.getString("title"));
            book.setSize(rs.getInt("size"));
            return book;

        });
        return new ArrayList<>(books);
    }

    @Override
    public void store(Book book) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("author",book.getAuthor());
        parameterSource.addValue("title",book.getTitle());
        parameterSource.addValue("size",book.getSize());
        jdbcTemplate.update("INSERT INTO books(author,title,size) VALUES(:author, :title, :size)",parameterSource);
        logger.info("store new book: " + book);
     //   book.setId(context.getBean(IdProvider.class).provideId(book));
       // repo.add(book);
    }

    @Override
    public boolean removeItemById(Integer bookIdToRemove) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue("id",bookIdToRemove);
        jdbcTemplate.update("DELETE FROM books WHERE id = :id",parameterSource);
        logger.info("remove book completed");
        return true;
    }

    @Override
    public void removeItemByRegex(String queryRegex) {
        Pattern p = Pattern.compile("(author|title|size)\\s+(.+)*");
        Matcher m = p.matcher(queryRegex.trim());
        if (m.matches()) {
            String field = m.group(1);
            String value = m.group(2);

            for (Book book : retreiveAll()) {
                //String getParam =field.equals("author") ? book.getAuthor()
                //        : field.equals("title") ? book.getTitle() :
                        //book.getSize();
                //Pattern pattern = Pattern.compile(value);
                //Matcher matcher = pattern.matcher(getParam);

                //if (matcher.find()) {
                  //  logger.info("remove book completed: " + book);
                   // repo.remove(book);
               // }
            }
            
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void defaultInit() {
        logger.info("default INIT in book repo bean");
    }

    private void defaultDestroy() {
        logger.info("default DESTROY in book repo bean");
    }
}



