package com.gutti.store.views;

import com.gutti.store.configs.AppRequestContext;
import com.gutti.store.domain.Organization;
import com.gutti.store.services.OrganizationService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

import java.util.UUID;
@PermitAll
@Route(value = "my-organization", layout = MainLayout.class)
@PageTitle("Mi Empresa | Gutti Store")
public class OrganizationDetailsView extends VerticalLayout {

    private final OrganizationService organizationService;
    private Organization currentOrganization;

    private final Binder<Organization> binder = new BeanValidationBinder<>(Organization.class);

    private final TextField name = new TextField("Nombre de la Empresa");
    private final TextField logoUrl = new TextField("URL del Logo");
    private final TextField phone = new TextField("Teléfono");
    private final TextField email = new TextField("Email de Contacto");
    private final TextField address = new TextField("Dirección");

    private final Button saveButton = new Button("Guardar Cambios");
    private final FormLayout formLayout = new FormLayout();

    public OrganizationDetailsView(OrganizationService organizationService) {
        this.organizationService = organizationService;

        addClassName("organization-details-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        // Configura el formulario
        formLayout.add(name, logoUrl, phone, email, address);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setMaxWidth("800px");

        // Enlaza los campos del formulario a la entidad Organization
        binder.bindInstanceFields(this);

        // Configura el botón de guardar
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> saveChanges());
    }

    /**
     * Se usa onAttach para asegurar que AppRequestContext esté disponible.
     * Cargar datos en el constructor es demasiado pronto y puede causar errores.
     */
    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        // Limpia la vista antes de añadir componentes para evitar duplicados
        removeAll();

        // Carga los datos de la empresa actual
        loadOrganizationData();

        // Añade los componentes a la vista
        add(new H2("Datos de mi Empresa"), formLayout, saveButton);
    }

    private void loadOrganizationData() {
        // Obtiene el ID de la organización del usuario actual (tenant)
        UUID organizationId = AppRequestContext.get().getOrganizationId();

        // Busca la organización en la BD o crea una nueva si no existe
        this.currentOrganization = organizationService.findById(organizationId).orElseGet(() -> {
            // Si no se encuentra la organización, crea una nueva instancia
            Organization newOrg = new Organization();
            // y le asigna el ID del tenant actual.
            newOrg.setId(organizationId);
            return newOrg;
        });

        // Carga los datos en el formulario
        binder.setBean(currentOrganization);
    }

    private void saveChanges() {
        if (binder.writeBeanIfValid(currentOrganization)) {
            organizationService.save(currentOrganization);
            Notification.show("Datos guardados correctamente.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification.show("Por favor, corrige los errores en el formulario.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
