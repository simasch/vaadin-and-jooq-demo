package ch.martinelli.demo.data.service;

import ch.martinelli.demo.db.tables.records.PersonRecord;
import org.assertj.core.api.Assertions;
import org.jooq.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceTest {

    @Autowired
    private PersonService personService;

    @Test
    void list() {
        Result<PersonRecord> list = personService.list(0, 100, new ArrayList<>());

        assertThat(list).hasSize(100);
    }
}