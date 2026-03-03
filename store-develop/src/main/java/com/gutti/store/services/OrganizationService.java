package com.gutti.store.services;

import org.springframework.data.domain.PageRequest;
import com.gutti.store.domain.Organization;
import com.gutti.store.domain.OrganizationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Encuentra organizaciones filtrando por el ID del tenant y un texto de búsqueda.
     * (Este método es para vistas de usuario normal).
     */
    public List<Organization> findAll(UUID organizationId, String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return organizationRepository.findByOrganizationId(organizationId);
        }
        return organizationRepository.searchByOrganization(organizationId, filterText);
    }

    /**
     * Encuentra todas las organizaciones para el panel de administrador, sin filtrar por tenant.
     */
    public List<Organization> findAllForAdmin(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return organizationRepository.findAll();
        }
        return organizationRepository.searchAll(filterText);
    }

    /**
     * Busca una página de organizaciones para el panel de administrador.
     * @param page Contiene la información de paginación (número de página, tamaño).
     * @param filterText El texto para filtrar por nombre.
     * @return Una lista de organizaciones para la página actual.
     */
    public List<Organization> fetchPageForAdmin(int page, int pageSize, String filterText) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        if (filterText == null || filterText.isEmpty()) {
            return organizationRepository.findAll(pageRequest).getContent();
        } else {
            return organizationRepository.findByNameContainingIgnoreCase(filterText, pageRequest).getContent();
        }
    }

    /**
     * Cuenta el número total de organizaciones para la paginación del panel de administrador.
     * @param filterText El texto para filtrar por nombre.
     * @return El conteo total de organizaciones.
     */
    public int countForAdmin(String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return (int) organizationRepository.count();
        } else {
            return (int) organizationRepository.countByNameContainingIgnoreCase(filterText);
        }
    }

    public Optional<Organization> findById(UUID id) {
        return organizationRepository.findById(id);
    }

    public void delete(Organization organization) {
        organizationRepository.delete(organization);
    }

    public void save(Organization organization) {
        // Se deja que @GeneratedValue en la entidad se encargue de generar el UUID
        // para las nuevas organizaciones.
        organizationRepository.save(organization);
    }
}