import { memo } from 'react';
import { Table } from 'antd';
import type { TableProps } from 'antd';

// Memoized Table component to prevent unnecessary re-renders
export const MemoizedTable = memo(
  <T extends object>(props: TableProps<T>) => {
    return <Table {...props} />;
  },
  (prevProps, nextProps) => {
    // Custom comparison function
    return (
      prevProps.dataSource === nextProps.dataSource &&
      prevProps.loading === nextProps.loading &&
      prevProps.pagination === nextProps.pagination &&
      prevProps.columns === nextProps.columns
    );
  }
);

MemoizedTable.displayName = 'MemoizedTable';
