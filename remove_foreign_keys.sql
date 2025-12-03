-- 移除外鍵約束以便資料初始化
-- 執行此腳本: psql -U postgres -d demo -f remove_foreign_keys.sql

-- 移除 products 表的外鍵約束
ALTER TABLE products DROP CONSTRAINT IF EXISTS products_category_id_fkey;
ALTER TABLE products DROP CONSTRAINT IF EXISTS products_supplier_id_fkey;

-- 將欄位改為可為 NULL
ALTER TABLE products ALTER COLUMN category_id DROP NOT NULL;
ALTER TABLE products ALTER COLUMN supplier_id DROP NOT NULL;

-- 查看 products 表結構確認
\d products

SELECT 'Foreign key constraints removed successfully!' AS status;
