describe('Pedidos Management Flow', () => {
  beforeEach(() => {
    cy.fixture('users').then((users) => {
      cy.login(users.operador.email, users.operador.password);
    });
    cy.visit('/pedidos');
  });

  it('should display pedidos page', () => {
    cy.contains('Gestión de Pedidos').should('be.visible');
    cy.get('[data-testid="pedidos-table"]').should('exist');
  });

  it('should open create pedido modal', () => {
    cy.contains('Nuevo Pedido').click();
    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Crear Pedido').should('be.visible');
  });

  it('should create a new pedido', () => {
    cy.contains('Nuevo Pedido').click();

    // Fill form
    cy.get('[data-testid="codigo-tracking"]').type('TRACK-2024-001');
    cy.get('[data-testid="cliente-select"]').click();
    cy.get('[data-testid="cliente-option-1"]').click();

    cy.get('[data-testid="pais-origen"]').type('China');
    cy.get('[data-testid="pais-destino"]').type('Paraguay');

    cy.get('[data-testid="tipo-carga"]').click();
    cy.contains('FCL').click();

    cy.get('[data-testid="descripcion"]').type('Productos electrónicos');
    cy.get('[data-testid="peso"]').type('1500');
    cy.get('[data-testid="volumen"]').type('25');
    cy.get('[data-testid="valor-declarado"]').type('50000');

    // Submit
    cy.get('[data-testid="submit-pedido"]').click();

    // Verify success
    cy.contains('Pedido creado exitosamente').should('be.visible');
    cy.contains('TRACK-2024-001').should('be.visible');
  });

  it('should search pedidos', () => {
    cy.get('[data-testid="search-input"]').type('TRACK-2024');
    cy.wait(500); // Debounce

    cy.get('[data-testid="pedidos-table"]')
      .find('tbody tr')
      .should('have.length.greaterThan', 0);
  });

  it('should filter pedidos by status', () => {
    cy.get('[data-testid="status-filter"]').click();
    cy.contains('En Tránsito').click();

    cy.get('[data-testid="pedidos-table"]')
      .find('tbody tr')
      .each(($row) => {
        cy.wrap($row).should('contain', 'EN_TRANSITO');
      });
  });

  it('should view pedido details', () => {
    cy.get('[data-testid="pedidos-table"] tbody tr').first().click();

    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Detalle del Pedido').should('be.visible');
    cy.contains('Código Tracking').should('be.visible');
    cy.contains('Historial de Estados').should('be.visible');
  });

  it('should update pedido status', () => {
    cy.get('[data-testid="pedidos-table"] tbody tr').first().within(() => {
      cy.get('[data-testid="edit-button"]').click();
    });

    cy.get('[data-testid="estado-select"]').click();
    cy.contains('Recibido').click();

    cy.get('[data-testid="submit-pedido"]').click();

    cy.contains('Pedido actualizado exitosamente').should('be.visible');
  });

  it('should delete pedido', () => {
    cy.get('[data-testid="pedidos-table"] tbody tr').first().within(() => {
      cy.get('[data-testid="delete-button"]').click();
    });

    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('¿Eliminar Pedido?').should('be.visible');
    cy.contains('Eliminar').click();

    cy.contains('Pedido eliminado exitosamente').should('be.visible');
  });
});
