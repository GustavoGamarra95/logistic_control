/**
 * Formatea un número como moneda
 */
export const formatCurrency = (
  amount: number,
  currency: string = 'PYG',
  locale: string = 'es-PY'
): string => {
  return new Intl.NumberFormat(locale, {
    style: 'currency',
    currency,
    minimumFractionDigits: currency === 'PYG' ? 0 : 2,
  }).format(amount);
};

/**
 * Formatea una fecha
 */
export const formatDate = (
  date: string | Date,
  format: 'short' | 'long' | 'datetime' = 'short'
): string => {
  const dateObj = typeof date === 'string' ? new Date(date) : date;

  if (format === 'datetime') {
    return new Intl.DateTimeFormat('es-PY', {
      dateStyle: 'short',
      timeStyle: 'short',
    }).format(dateObj);
  }

  return new Intl.DateTimeFormat('es-PY', {
    dateStyle: format === 'long' ? 'long' : 'short',
  }).format(dateObj);
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
