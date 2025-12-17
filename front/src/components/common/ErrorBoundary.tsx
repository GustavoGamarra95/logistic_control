import React, { Component, ErrorInfo, ReactNode } from 'react';
import { Result, Button } from 'antd';

interface Props {
  children: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error boundary caught error:', error, errorInfo);
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: undefined });
    window.location.reload();
  };

  public render() {
    if (this.state.hasError) {
      return (
        <div className="flex h-screen items-center justify-center bg-background p-4">
          <Result
            status="error"
            title="Algo salió mal"
            subTitle="Lo sentimos, ha ocurrido un error inesperado. Por favor, intenta recargar la página."
            extra={[
              <Button type="primary" key="reload" onClick={this.handleReset}>
                Recargar Página
              </Button>,
              <Button key="home" onClick={() => window.location.href = '/'}>
                Ir al Inicio
              </Button>,
            ]}
          >
            {this.state.error && (
              <div className="mt-4 p-4 bg-muted rounded-lg">
                <p className="text-sm font-mono text-muted-foreground">
                  {this.state.error.message}
                </p>
              </div>
            )}
          </Result>
        </div>
      );
    }

    return this.props.children;
  }
}
