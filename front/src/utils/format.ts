/**
 * Formatea un número como moneda
 */
export const formatCurrency = (
  amount: number | null | undefined,
  currency: string = 'PYG',
  locale: string = 'es-PY'
): string => {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return '--';
  }
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: currency === 'PYG' ? 0 : 2,
  }).format(amount);
};

/**
 * Formatea una fecha en formato DD/MM/YYYY
 */
export const formatDate = (
  date: string | Date | null | undefined,
  format: 'short' | 'long' | 'datetime' = 'short'
): string => {
  if (!date) return '--';

  const dateObj = typeof date === 'string' ? new Date(date) : date;

  if (isNaN(dateObj.getTime())) return '--';

  if (format === 'datetime') {
    return formatDateTime(dateObj);
  }

  const day = String(dateObj.getDate()).padStart(2, '0');
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const year = dateObj.getFullYear();

  if (format === 'long') {
    return new Intl.DateTimeFormat('es-PY', {
      dateStyle: 'long',
    }).format(dateObj);
  }

  return `${day}/${month}/${year}`;
};

/**
 * Formatea una fecha con hora en formato DD/MM/YYYY HH:mm:ss
 */
export const formatDateTime = (
  date: string | Date | null | undefined,
  options?: Intl.DateTimeFormatOptions
): string => {
  if (!date) return '--';

  const dateObj = typeof date === 'string' ? new Date(date) : date;

  if (isNaN(dateObj.getTime())) return '--';

  const day = String(dateObj.getDate()).padStart(2, '0');
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const year = dateObj.getFullYear();
  const hours = String(dateObj.getHours()).padStart(2, '0');
  const minutes = String(dateObj.getMinutes()).padStart(2, '0');
  const seconds = String(dateObj.getSeconds()).padStart(2, '0');

  return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
};

/**
 * Formatea un RUC paraguayo
 */
export const formatRuc = (ruc: string, dv?: string): string => {
  if (dv) {
    return `${ruc}-${dv}`;
  }
  return ruc;
};

/**
 * Formatea un número de teléfono
 */
export const formatPhone = (phone: string): string => {
  // Formato paraguayo: +595 XXX XXX XXX
  const cleaned = phone.replace(/\D/g, '');
  if (cleaned.startsWith('595')) {
    return `+595 ${cleaned.slice(3, 6)} ${cleaned.slice(6, 9)} ${cleaned.slice(9)}`;
  }
  return phone;
};
