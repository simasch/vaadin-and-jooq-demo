package ch.martinelli.demo.data.service;

import ch.martinelli.demo.db.tables.records.ApplicationUserRecord;
import ch.martinelli.demo.db.tables.records.UserRolesRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ch.martinelli.demo.db.tables.ApplicationUser.APPLICATION_USER;
import static ch.martinelli.demo.db.tables.UserRoles.USER_ROLES;

@Service
public class UserService {

    private final DSLContext context;

    public UserService(DSLContext context) {
        this.context = context;
    }

    public Optional<ApplicationUserRecord> get(Long id) {
        return context.selectFrom(APPLICATION_USER).where(APPLICATION_USER.ID.eq(id)).fetchOptional();
    }

    public ApplicationUserRecord findByUsername(String username) {
        return context.selectFrom(APPLICATION_USER).where(APPLICATION_USER.USERNAME.eq(username)).fetchOne();
    }

    public Result<UserRolesRecord> findRolesByUserId(Long userId) {
        return context.selectFrom(USER_ROLES).where(USER_ROLES.USER_ID.eq(userId)).fetch();
    }
}
