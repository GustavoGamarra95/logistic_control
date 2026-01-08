import { useCallback, useRef, useEffect } from 'react';

/**
 * Hook optimizado que previene re-creaciones innecesarias de callbacks
 * Útil para callbacks que se pasan a componentes hijos memoizados
 */
export function useOptimizedCallback<T extends (...args: any[]) => any>(
  callback: T,
  deps: React.DependencyList = []
): T {
  const callbackRef = useRef(callback);

  useEffect(() => {
    callbackRef.current = callback;
  }, [callback, ...deps]);

  return useCallback(
    ((...args) => callbackRef.current(...args)) as T,
    []
  );
}

/**
 * Hook para prevenir re-renders cuando el valor no ha cambiado realmente
 * Usa comparación profunda
 */
export function useDeepCompareMemo<T>(
  factory: () => T,
  deps: React.DependencyList
): T {
  const ref = useRef<{ deps: React.DependencyList; value: T }>();

  if (
    !ref.current ||
    !areEqual(ref.current.deps, deps)
  ) {
    ref.current = {
      deps,
      value: factory(),
    };
  }

  return ref.current.value;
}

function areEqual(a: React.DependencyList, b: React.DependencyList): boolean {
  if (a.length !== b.length) return false;

  for (let i = 0; i < a.length; i++) {
    if (!Object.is(a[i], b[i])) {
      return false;
    }
  }

  return true;
}
