/**
 * Service Worker registration and utilities
 */

export interface ServiceWorkerConfig {
  onSuccess?: (registration: ServiceWorkerRegistration) => void;
  onUpdate?: (registration: ServiceWorkerRegistration) => void;
  onOffline?: () => void;
  onOnline?: () => void;
}

export function registerServiceWorker(config?: ServiceWorkerConfig) {
  if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
      const swUrl = '/sw.js';

      navigator.serviceWorker
        .register(swUrl)
        .then((registration) => {
          console.log('[SW] Service Worker registered:', registration);

          // Check for updates periodically
          setInterval(() => {
            registration.update();
          }, 60000); // Check every minute

          registration.onupdatefound = () => {
            const installingWorker = registration.installing;

            if (installingWorker) {
              installingWorker.onstatechange = () => {
                if (installingWorker.state === 'installed') {
                  if (navigator.serviceWorker.controller) {
                    // New update available
                    console.log('[SW] New content available, please refresh');
                    config?.onUpdate?.(registration);
                  } else {
                    // Content cached for offline use
                    console.log('[SW] Content cached for offline use');
                    config?.onSuccess?.(registration);
                  }
                }
              };
            }
          };
        })
        .catch((error) => {
          console.error('[SW] Service Worker registration failed:', error);
        });
    });

    // Listen to online/offline events
    window.addEventListener('online', () => {
      console.log('[SW] App is online');
      config?.onOnline?.();
    });

    window.addEventListener('offline', () => {
      console.log('[SW] App is offline');
      config?.onOffline?.();
    });
  }
}

export function unregisterServiceWorker() {
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.ready
      .then((registration) => {
        registration.unregister();
      })
      .catch((error) => {
        console.error('[SW] Error during service worker unregistration:', error);
      });
  }
}

export async function clearServiceWorkerCache() {
  if ('serviceWorker' in navigator && navigator.serviceWorker.controller) {
    navigator.serviceWorker.controller.postMessage({
      type: 'CLEAR_CACHE',
    });
  }

  if ('caches' in window) {
    const cacheNames = await caches.keys();
    await Promise.all(cacheNames.map((name) => caches.delete(name)));
    console.log('[SW] All caches cleared');
  }
}

export function skipWaiting() {
  if ('serviceWorker' in navigator && navigator.serviceWorker.controller) {
    navigator.serviceWorker.controller.postMessage({
      type: 'SKIP_WAITING',
    });
  }
}

export function isOnline(): boolean {
  return navigator.onLine;
}

export async function syncOfflineActions() {
  if ('serviceWorker' in navigator && 'sync' in window.registration) {
    try {
      await navigator.serviceWorker.ready;
      // @ts-ignore
      await window.registration.sync.register('sync-offline-actions');
      console.log('[SW] Background sync registered');
    } catch (error) {
      console.error('[SW] Background sync registration failed:', error);
    }
  }
}
