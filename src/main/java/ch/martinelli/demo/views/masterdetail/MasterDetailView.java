package ch.martinelli.demo.views.masterdetail;

import ch.jtaf.db.tables.Person;
import ch.jtaf.db.tables.records.PersonRecord;
import ch.martinelli.demo.data.service.PersonService;
import ch.martinelli.demo.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Optional;

import static ch.jtaf.db.tables.Person.PERSON;
import static io.seventytwo.vaadinjooq.util.VaadinJooqUtil.orderFields;

@PageTitle("Master-Detail")
@Route(value = "master-detail/:personID?/:action?(edit)", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private final String PERSON_ID = "personID";
    private final String PERSON_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private final Grid<PersonRecord> grid = new Grid<>(PersonRecord.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField occupation;
    private TextField role;
    private Checkbox important;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<PersonRecord> binder;

    private PersonRecord person;

    private final PersonService personService;

    public MasterDetailView(PersonService personService) {
        this.personService = personService;
        addClassNames("master-detail-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true).setSortProperty(PERSON.FIRST_NAME.getName());
        grid.addColumn("lastName").setAutoWidth(true).setSortProperty(PERSON.LAST_NAME.getName());;
        grid.addColumn("email").setAutoWidth(true).setSortProperty(PERSON.EMAIL.getName());;
        grid.addColumn("phone").setAutoWidth(true).setSortProperty(PERSON.PHONE.getName());;
        grid.addColumn("dateOfBirth").setAutoWidth(true).setSortProperty(PERSON.DATE_OF_BIRTH.getName());;
        grid.addColumn("occupation").setAutoWidth(true).setSortProperty(PERSON.OCCUPATION.getName());;
        grid.addColumn("role").setAutoWidth(true).setSortProperty(PERSON.ROLE.getName());;
        LitRenderer<PersonRecord> importantRenderer = LitRenderer.<PersonRecord>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", important -> important.getImportant() ? "check" : "minus").withProperty("color",
                        important -> important.getImportant()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");
        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true).setSortProperty(PERSON.IMPORTANT.getName());;

        grid.setItems(query -> personService.list(query.getOffset(), query.getLimit(), orderFields(PERSON, query)).stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PERSON_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(PersonRecord.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.person == null) {
                    this.person = new PersonRecord();
                }
                binder.writeBean(this.person);
                personService.store(this.person);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(MasterDetailView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> personId = event.getRouteParameters().get(PERSON_ID).map(Long::parseLong);
        if (personId.isPresent()) {
            Optional<PersonRecord> personFromBackend = personService.get(personId.get());
            if (personFromBackend.isPresent()) {
                populateForm(personFromBackend.get());
            } else {
                Notification.show(String.format("The requested person was not found, ID = %s", personId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        dateOfBirth = new DatePicker("Date Of Birth");
        occupation = new TextField("Occupation");
        role = new TextField("Role");
        important = new Checkbox("Important");
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(PersonRecord value) {
        this.person = value;
        binder.readBean(this.person);

    }
}
