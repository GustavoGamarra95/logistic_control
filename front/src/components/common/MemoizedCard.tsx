import { memo, ReactNode } from 'react';
import { Card } from 'antd';
import type { CardProps } from 'antd';

interface MemoizedCardProps extends CardProps {
  children: ReactNode;
}

// Memoized Card component
export const MemoizedCard = memo<MemoizedCardProps>(
  ({ children, ...props }) => {
    return <Card {...props}>{children}</Card>;
  },
  (prevProps, nextProps) => {
    return (
      prevProps.title === nextProps.title &&
      prevProps.loading === nextProps.loading &&
      prevProps.children === nextProps.children
    );
  }
);

MemoizedCard.displayName = 'MemoizedCard';
