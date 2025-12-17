import { Typography } from 'antd';
import { ReactNode } from 'react';

const { Title, Text } = Typography;

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  extra?: ReactNode;
}

export const PageHeader = ({ title, subtitle, extra }: PageHeaderProps) => {
  return (
    <div className="flex items-center justify-between mb-6">
      <div>
        <Title level={2} className="!mb-1">{title}</Title>
        {subtitle && (
          <Text type="secondary" className="text-base">{subtitle}</Text>
        )}
      </div>
      {extra && <div>{extra}</div>}
    </div>
  );
};
