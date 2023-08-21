package ch.martinelli.demo.data.service;

import ch.jtaf.db.tables.records.PersonRecord;
import org.jooq.DSLContext;
import org.jooq.OrderField;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static ch.jtaf.db.tables.Person.PERSON;

@Service
public class PersonService {

    private final DSLContext context;

    public PersonService(DSLContext context) {
        this.context = context;
    }

    public Optional<PersonRecord> get(Long id) {
        return context.selectFrom(PERSON).where(PERSON.ID.eq(id)).fetchOptional();
    }

    public void store(PersonRecord person) {
        context.attach(person);
        person.store();
    }

    public void delete(Long id) {
        context.deleteFrom(PERSON).where(PERSON.ID.eq(id)).execute();
    }

    public Result<PersonRecord> list(int offset, int limit, List<OrderField<?>> orderFields) {
        return context.selectFrom(PERSON).orderBy(orderFields).offset(offset).limit(limit).fetch();
    }


    public int count() {
        return context.fetchCount(PERSON);
    }

}
