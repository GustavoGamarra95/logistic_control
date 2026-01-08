describe('Dashboard Flow', () => {
  beforeEach(() => {
    cy.fixture('users').then((users) => {
      cy.login(users.admin.email, users.admin.password);
    });
    cy.visit('/dashboard');
  });

  it('should display dashboard page', () => {
    cy.contains('Dashboard').should('be.visible');
    cy.contains('Panel de control y métricas del sistema').should('be.visible');
  });

  it('should display main KPI cards', () => {
    // Pedidos Activos
    cy.contains('Pedidos Activos').should('be.visible');
    cy.get('[data-testid="kpi-pedidos"]').should('be.visible');

    // Facturas
    cy.contains('Facturas').should('be.visible');
    cy.get('[data-testid="kpi-facturas"]').should('be.visible');

    // Inventario
    cy.contains('Inventario').should('be.visible');
    cy.get('[data-testid="kpi-inventario"]').should('be.visible');

    // Clientes
    cy.contains('Clientes').should('be.visible');
    cy.get('[data-testid="kpi-clientes"]').should('be.visible');
  });

  it('should display stock alerts when applicable', () => {
    // Check if there are stock issues
    cy.get('body').then(($body) => {
      if ($body.find('[data-testid="stock-alert"]').length > 0) {
        cy.get('[data-testid="stock-alert"]').should('be.visible');
        cy.contains('Alerta de Inventario').should('be.visible');
      }
    });
  });

  it('should display recent pedidos table', () => {
    cy.contains('Pedidos Recientes').should('be.visible');
    cy.get('[data-testid="recent-pedidos-table"]').should('exist');

    // Verify "Ver todos" link
    cy.contains('Ver todos').should('have.attr', 'href', '/pedidos');
  });

  it('should display recent facturas table', () => {
    cy.contains('Facturas Recientes').should('be.visible');
    cy.get('[data-testid="recent-facturas-table"]').should('exist');

    // Verify "Ver todas" link
    cy.contains('Ver todas').should('have.attr', 'href', '/facturas');
  });

  it('should display low stock products table when applicable', () => {
    cy.get('body').then(($body) => {
      if ($body.find('[data-testid="low-stock-table"]').length > 0) {
        cy.contains('Productos con Stock Bajo').should('be.visible');
        cy.get('[data-testid="low-stock-table"]').should('exist');

        // Verify "Ver inventario completo" link
        cy.contains('Ver inventario completo').should('have.attr', 'href', '/inventario');
      }
    });
  });

  it('should display distribution cards', () => {
    // Pedidos distribution
    cy.contains('Distribución de Pedidos').should('be.visible');
    cy.contains('Registrados').should('be.visible');
    cy.contains('En Tránsito').should('be.visible');
    cy.contains('Entregados').should('be.visible');

    // Facturas distribution
    cy.contains('Estado de Facturas').should('be.visible');
    cy.contains('Pendientes').should('be.visible');
    cy.contains('Aprobadas').should('be.visible');
    cy.contains('Ingresos Totales').should('be.visible');
  });

  it('should display charts', () => {
    // Revenue trend chart
    cy.contains('Tendencia de Ingresos').should('be.visible');
    cy.get('[data-testid="revenue-chart"]').should('exist');

    // Orders by status chart
    cy.contains('Pedidos por Estado').should('be.visible');
    cy.get('[data-testid="orders-chart"]').should('exist');

    // Invoice status pie chart
    cy.contains('Distribución de Estados - Facturas').should('be.visible');
    cy.get('[data-testid="invoices-chart"]').should('exist');

    // Inventory levels chart
    cy.contains('Niveles de Inventario').should('be.visible');
    cy.get('[data-testid="inventory-chart"]').should('exist');
  });

  it('should navigate to pedidos page from recent table', () => {
    cy.contains('Ver todos').click();
    cy.url().should('include', '/pedidos');
  });

  it('should navigate to facturas page from recent table', () => {
    cy.contains('Facturas Recientes').parent().within(() => {
      cy.contains('Ver todas').click();
    });
    cy.url().should('include', '/facturas');
  });

  it('should navigate to inventario page from low stock alert', () => {
    cy.get('body').then(($body) => {
      if ($body.find('[data-testid="low-stock-table"]').length > 0) {
        cy.contains('Ver inventario completo').click();
        cy.url().should('include', '/inventario');
      }
    });
  });

  it('should display KPI values correctly', () => {
    // Verify KPI cards have numeric values
    cy.get('[data-testid="kpi-pedidos"]').find('[class*="ant-statistic-content-value"]')
      .invoke('text')
      .should('match', /^\d+$/);

    cy.get('[data-testid="kpi-facturas"]').find('[class*="ant-statistic-content-value"]')
      .invoke('text')
      .should('match', /^\d+$/);

    cy.get('[data-testid="kpi-inventario"]').find('[class*="ant-statistic-content-value"]')
      .invoke('text')
      .should('match', /^\d+$/);

    cy.get('[data-testid="kpi-clientes"]').find('[class*="ant-statistic-content-value"]')
      .invoke('text')
      .should('match', /^\d+$/);
  });

  it('should have responsive design on mobile', () => {
    cy.viewport('iphone-x');

    cy.contains('Dashboard').should('be.visible');
    cy.get('[data-testid="kpi-pedidos"]').should('be.visible');

    // KPI cards should stack vertically on mobile
    cy.get('[class*="ant-col-xs-24"]').should('exist');
  });
});
