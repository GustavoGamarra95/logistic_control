import { useState } from 'react';

export const usePagination = (initialPage: number = 0, initialSize: number = 20) => {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);

  const handlePageChange = (newPage: number) => {
    setPage(newPage - 1); // Ant Design uses 1-based, backend uses 0-based
  };

  const handleSizeChange = (newSize: number) => {
    setSize(newSize);
    setPage(0); // Reset to first page when changing size
  };

  return {
    page,
    size,
    handlePageChange,
    handleSizeChange,
    resetPagination: () => {
      setPage(0);
      setSize(initialSize);
    },
  };
};
