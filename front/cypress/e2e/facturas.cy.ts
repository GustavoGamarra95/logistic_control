describe('Facturas Management Flow', () => {
  beforeEach(() => {
    cy.fixture('users').then((users) => {
      cy.login(users.admin.email, users.admin.password);
    });
    cy.visit('/facturas');
  });

  it('should display facturas page', () => {
    cy.contains('Gestión de Facturas').should('be.visible');
    cy.contains('Facturación electrónica con SIFEN').should('be.visible');
  });

  it('should open create factura modal', () => {
    cy.contains('Nueva Factura').click();
    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Nueva Factura').should('be.visible');
  });

  it('should create factura with items', () => {
    cy.contains('Nueva Factura').click();

    // Fill basic info
    cy.get('[data-testid="cliente-select"]').click();
    cy.get('[data-testid="cliente-option-1"]').click();

    cy.get('[data-testid="tipo-factura"]').click();
    cy.contains('CONTADO').click();

    cy.get('[data-testid="timbrado"]').type('12345678');
    cy.get('[data-testid="establecimiento"]').type('001');
    cy.get('[data-testid="punto-expedicion"]').type('001');

    // Add first item
    cy.contains('Agregar Ítem').click();

    cy.get('[data-testid="item-0-descripcion"]').type('Producto de prueba 1');
    cy.get('[data-testid="item-0-cantidad"]').type('10');
    cy.get('[data-testid="item-0-precio"]').type('50000');
    cy.get('[data-testid="item-0-tasa-iva"]').select('10');

    // Add second item
    cy.contains('Agregar Ítem').click();

    cy.get('[data-testid="item-1-descripcion"]').type('Producto de prueba 2');
    cy.get('[data-testid="item-1-cantidad"]').type('5');
    cy.get('[data-testid="item-1-precio"]').type('80000');
    cy.get('[data-testid="item-1-tasa-iva"]').select('10');

    // Verify totals calculation
    cy.get('[data-testid="subtotal"]').should('contain', '900,000');
    cy.get('[data-testid="iva-total"]').should('contain', '90,000');
    cy.get('[data-testid="total"]').should('contain', '990,000');

    // Submit
    cy.get('[data-testid="submit-factura"]').click();

    cy.contains('Factura creada exitosamente').should('be.visible');
  });

  it('should remove item from factura', () => {
    cy.contains('Nueva Factura').click();

    // Add items
    cy.contains('Agregar Ítem').click();
    cy.contains('Agregar Ítem').click();

    // Remove first item
    cy.get('[data-testid="remove-item-0"]').click();

    // Should only have one item now
    cy.get('[data-testid^="item-"]').should('have.length', 1);
  });

  it('should send factura to SIFEN', () => {
    // Find a factura in BORRADOR or EMITIDA status
    cy.get('[data-testid="facturas-table"] tbody tr').first().within(() => {
      cy.get('[data-testid="send-sifen-button"]').click();
    });

    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Enviar a SIFEN').should('be.visible');
    cy.contains('Enviar').click();

    cy.contains('Factura enviada a SIFEN').should('be.visible');
  });

  it('should view factura details with SIFEN panel', () => {
    cy.get('[data-testid="facturas-table"] tbody tr').first().within(() => {
      cy.get('[data-testid="detail-button"]').click();
    });

    cy.get('[role="dialog"]').should('be.visible');
    cy.contains('Estado SIFEN').should('be.visible');
    cy.contains('Ítems de la Factura').should('be.visible');
  });

  it('should filter facturas by status', () => {
    cy.get('[data-testid="estado-filter"]').click();
    cy.contains('Aprobada').click();

    cy.get('[data-testid="facturas-table"]')
      .find('tbody tr')
      .each(($row) => {
        cy.wrap($row).should('contain', 'APROBADA');
      });
  });

  it('should search facturas by number or client', () => {
    cy.get('[data-testid="search-input"]').type('001-001');
    cy.wait(500); // Debounce

    cy.get('[data-testid="facturas-table"]')
      .find('tbody tr')
      .should('have.length.greaterThan', 0);
  });
});
