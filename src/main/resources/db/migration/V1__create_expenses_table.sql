-- Create expenses table
CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    notes TEXT,
    expense_date TIMESTAMP NOT NULL,
    receipt_number VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_expenses_date ON expenses(expense_date);
CREATE INDEX IF NOT EXISTS idx_expenses_category ON expenses(category);
CREATE INDEX IF NOT EXISTS idx_expenses_created_at ON expenses(created_at);

-- Add comments
COMMENT ON TABLE expenses IS 'Table to track pharmacy expenses';
COMMENT ON COLUMN expenses.description IS 'Description of the expense';
COMMENT ON COLUMN expenses.amount IS 'Amount spent';
COMMENT ON COLUMN expenses.category IS 'Category of expense (RENT, UTILITIES, SALARIES, etc.)';
COMMENT ON COLUMN expenses.notes IS 'Additional notes about the expense';
COMMENT ON COLUMN expenses.expense_date IS 'Date when the expense was incurred';
COMMENT ON COLUMN expenses.receipt_number IS 'Receipt or invoice number';
COMMENT ON COLUMN expenses.created_at IS 'When the record was created';
COMMENT ON COLUMN expenses.created_by IS 'Who created the record';
