describe('Inventario Management Flow', () => {
  beforeEach(() => {
    cy.fixture('users').then((users) => {
      cy.login(users.operador.email, users.operador.password);
    });
    cy.visit('/inventario');
  });

  it('should display inventario page', () => {
    cy.contains('Gestión de Inventario').should('be.visible');
    cy.contains('Control de stock y almacenamiento').should('be.visible');
  });

  it('should open entrada modal', () => {
    cy.contains('Nueva Entrada').click();
    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Entrada de Inventario').should('be.visible');
  });

  it('should register new inventory entry', () => {
    cy.contains('Nueva Entrada').click();

    // Select product
    cy.get('[data-testid="producto-select"]').click();
    cy.get('[data-testid="producto-option-1"]').click();

    // Fill quantity and state
    cy.get('[data-testid="cantidad"]').type('100');
    cy.get('[data-testid="estado-select"]').select('DISPONIBLE');

    // Fill lot and location
    cy.get('[data-testid="lote"]').type('LOTE-2024-001');

    // Select location components
    cy.get('[data-testid="deposito"]').select('PRINCIPAL');
    cy.get('[data-testid="zona"]').select('A');
    cy.get('[data-testid="pasillo"]').select('P01');
    cy.get('[data-testid="rack"]').select('R15');
    cy.get('[data-testid="nivel"]').select('N3');

    // Verify location string
    cy.get('[data-testid="ubicacion-generated"]').should('contain', 'PRINCIPAL-A-P01-R15-N3');

    // Submit
    cy.get('[data-testid="submit-entrada"]').click();

    cy.contains('Entrada registrada exitosamente').should('be.visible');
  });

  it('should open salida modal', () => {
    cy.contains('Registrar Salida').click();
    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Salida de Inventario').should('be.visible');
  });

  it('should register inventory exit', () => {
    cy.contains('Registrar Salida').click();

    // Select product
    cy.get('[data-testid="producto-select"]').click();
    cy.get('[data-testid="producto-option-1"]').click();

    cy.wait(500); // Wait for inventory records to load

    // Select inventory record
    cy.get('[data-testid="inventario-select"]').click();
    cy.get('[data-testid="inventario-option-1"]').click();

    // Select movement type
    cy.get('[data-testid="tipo-movimiento"]').select('SALIDA');

    // Enter quantity
    cy.get('[data-testid="cantidad"]').type('10');

    // Add reference
    cy.get('[data-testid="referencia"]').type('GUIA-2024-001');

    // Add observations
    cy.get('[data-testid="observaciones"]').type('Salida para entrega a cliente');

    // Submit
    cy.get('[data-testid="submit-salida"]').click();

    cy.contains('Salida registrada exitosamente').should('be.visible');
  });

  it('should prevent exit exceeding available quantity', () => {
    cy.contains('Registrar Salida').click();

    cy.get('[data-testid="producto-select"]').click();
    cy.get('[data-testid="producto-option-1"]').click();

    cy.wait(500);

    cy.get('[data-testid="inventario-select"]').click();
    cy.get('[data-testid="inventario-option-1"]').click();

    // Try to exit more than available
    cy.get('[data-testid="cantidad"]').type('99999');

    // Submit button should be disabled
    cy.get('[data-testid="submit-salida"]').should('be.disabled');

    // Should show error message
    cy.contains('Excede la cantidad disponible').should('be.visible');
  });

  it('should reserve inventory', () => {
    cy.contains('Registrar Salida').click();

    cy.get('[data-testid="producto-select"]').click();
    cy.get('[data-testid="producto-option-1"]').click();

    cy.wait(500);

    cy.get('[data-testid="inventario-select"]').click();
    cy.get('[data-testid="inventario-option-1"]').click();

    // Select RESERVA type
    cy.get('[data-testid="tipo-movimiento"]').select('RESERVA');

    // Pedido field should appear
    cy.get('[data-testid="pedido-select"]').should('be.visible');

    cy.get('[data-testid="cantidad"]').type('5');
    cy.get('[data-testid="submit-salida"]').click();

    cy.contains('Inventario reservado exitosamente').should('be.visible');
  });

  it('should filter inventory by status', () => {
    cy.get('[data-testid="estado-filter"]').click();
    cy.contains('Disponible').click();

    cy.get('[data-testid="inventario-table"]')
      .find('tbody tr')
      .each(($row) => {
        cy.wrap($row).should('contain', 'DISPONIBLE');
      });
  });

  it('should search inventory', () => {
    cy.get('[data-testid="search-input"]').type('PRINCIPAL');
    cy.wait(500); // Debounce

    cy.get('[data-testid="inventario-table"]')
      .find('tbody tr')
      .should('have.length.greaterThan', 0);
  });

  it('should display expiry warning for items close to expiration', () => {
    // This test assumes there are items with expiry dates
    cy.get('[data-testid="inventario-table"]')
      .find('[data-testid^="expiry-warning-"]')
      .should('have.class', 'text-orange-500'); // or text-red-500 for very close
  });

  it('should delete inventory record', () => {
    cy.get('[data-testid="inventario-table"] tbody tr').first().within(() => {
      cy.get('[data-testid="delete-button"]').click();
    });

    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('¿Eliminar registro de inventario?').should('be.visible');
    cy.contains('Eliminar').click();

    cy.contains('Inventario eliminado exitosamente').should('be.visible');
  });
});
