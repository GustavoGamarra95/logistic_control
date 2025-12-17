export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
export const APP_NAME = import.meta.env.VITE_APP_NAME || 'LogiControl';
export const IS_DEVELOPMENT = import.meta.env.DEV;
export const IS_PRODUCTION = import.meta.env.PROD;

// Pagination defaults
export const DEFAULT_PAGE_SIZE = 20;
export const PAGE_SIZE_OPTIONS = ['10', '20', '50', '100'];

// Tipos de servicio
export const TIPOS_SERVICIO = [
  { value: 'AEREO', label: 'Aéreo' },
  { value: 'MARITIMO', label: 'Marítimo' },
  { value: 'TERRESTRE', label: 'Terrestre' },
  { value: 'MULTIMODAL', label: 'Multimodal' },
];

// Monedas
export const MONEDAS = [
  { value: 'PYG', label: 'Guaraníes (₲)', symbol: '₲' },
  { value: 'USD', label: 'Dólares ($)', symbol: '$' },
  { value: 'EUR', label: 'Euros (€)', symbol: '€' },
];

// Países más comunes
export const PAISES_COMUNES = [
  'Paraguay',
  'Argentina',
  'Brasil',
  'Uruguay',
  'Chile',
  'Estados Unidos',
  'China',
  'México',
  'España',
];
