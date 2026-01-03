import { Input, InputProps } from 'antd';
import { ChangeEvent, forwardRef } from 'react';

interface RucInputProps extends Omit<InputProps, 'onChange' | 'maxLength'> {
  onChange?: (value: string) => void;
}

/**
 * Input especializado para RUC paraguayo
 * Formatea automáticamente: 12345678-9
 * Límite: 9 caracteres (8 dígitos + separador + 1 dígito verificador)
 */
export const RucInput = forwardRef<any, RucInputProps>(({ onChange, value, ...props }, ref) => {
  const formatRuc = (input: string): string => {
    // Remover todo lo que no sea dígito
    const digitsOnly = input.replace(/\D/g, '');

    // Limitar a 9 dígitos máximo
    const limited = digitsOnly.slice(0, 9);

    // Si tiene más de 8 dígitos, agregar el separador
    if (limited.length > 8) {
      return `${limited.slice(0, 8)}-${limited.slice(8)}`;
    }

    return limited;
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    const inputValue = e.target.value;
    const formatted = formatRuc(inputValue);

    if (onChange) {
      onChange(formatted);
    }
  };

  return (
    <Input
      {...props}
      ref={ref}
      value={value}
      onChange={handleChange}
      placeholder="12345678-9"
      maxLength={10} // 8 dígitos + 1 separador + 1 dígito = 10 caracteres
    />
  );
});

RucInput.displayName = 'RucInput';
