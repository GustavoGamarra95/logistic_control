import { Spin } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';

interface LoadingSpinnerProps {
  size?: 'small' | 'default' | 'large';
  fullscreen?: boolean;
  tip?: string;
}

export const LoadingSpinner = ({ 
  size = 'large', 
  fullscreen = false,
  tip = 'Cargando...'
}: LoadingSpinnerProps) => {
  const spinner = (
    <Spin 
      size={size} 
      indicator={<LoadingOutlined spin />}
      tip={tip}
    />
  );

  if (fullscreen) {
    return (
      <div className="flex h-screen items-center justify-center bg-background">
        {spinner}
      </div>
    );
  }

  return (
    <div className="flex items-center justify-center p-8">
      {spinner}
    </div>
  );
};
