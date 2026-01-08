import { memo } from 'react';
import { Statistic } from 'antd';
import type { StatisticProps } from 'antd';

// Memoized Statistic component for KPIs
export const MemoizedStatistic = memo<StatisticProps>(
  (props) => {
    return <Statistic {...props} />;
  },
  (prevProps, nextProps) => {
    return (
      prevProps.value === nextProps.value &&
      prevProps.loading === nextProps.loading &&
      prevProps.title === nextProps.title
    );
  }
);

MemoizedStatistic.displayName = 'MemoizedStatistic';
