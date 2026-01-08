describe('Authentication Flow', () => {
  beforeEach(() => {
    cy.visit('/login');
  });

  it('should display login page', () => {
    cy.contains('Iniciar Sesión').should('be.visible');
    cy.get('input[name="email"]').should('be.visible');
    cy.get('input[name="password"]').should('be.visible');
    cy.get('button[type="submit"]').should('be.visible');
  });

  it('should show validation errors for empty fields', () => {
    cy.get('button[type="submit"]').click();
    cy.contains('El email es requerido').should('be.visible');
    cy.contains('La contraseña es requerida').should('be.visible');
  });

  it('should show error for invalid credentials', () => {
    cy.get('input[name="email"]').type('invalid@example.com');
    cy.get('input[name="password"]').type('wrongpassword');
    cy.get('button[type="submit"]').click();

    cy.contains('Credenciales inválidas').should('be.visible');
    cy.url().should('include', '/login');
  });

  it('should successfully login with valid credentials', () => {
    cy.fixture('users').then((users) => {
      cy.get('input[name="email"]').type(users.admin.email);
      cy.get('input[name="password"]').type(users.admin.password);
      cy.get('button[type="submit"]').click();

      // Should redirect to dashboard
      cy.url().should('include', '/dashboard');

      // Should store auth token
      cy.window().its('localStorage').invoke('getItem', 'auth-token').should('exist');

      // Should display user menu
      cy.get('[data-testid="user-menu"]').should('be.visible');
    });
  });

  it('should successfully logout', () => {
    cy.fixture('users').then((users) => {
      // Login first
      cy.login(users.admin.email, users.admin.password);

      // Logout
      cy.logout();

      // Should redirect to login
      cy.url().should('include', '/login');

      // Should clear auth token
      cy.window().its('localStorage').invoke('getItem', 'auth-token').should('not.exist');
    });
  });

  it('should protect routes from unauthenticated users', () => {
    cy.visit('/dashboard');

    // Should redirect to login
    cy.url().should('include', '/login');
  });
});
